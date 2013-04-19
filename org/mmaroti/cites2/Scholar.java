/**
 * Copyright (C) Miklos Maroti, 2011
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.mmaroti.cites2;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.regex.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Scholar
{
	int maxConnections = 10;
	
	public static class TooManyConnectionsException extends IOException
	{
		private static final long serialVersionUID = 51944780478954114L;

		TooManyConnectionsException(String message)
		{
			super(message);
		}
	}
	
	public Connection getConnection(String url) throws IOException
	{
		if( --maxConnections <= 0 )
			throw new TooManyConnectionsException("Too many HTML requests");
		
		Connection conn = Jsoup.connect(url);
		conn.header("User-Agent", "Mozilla");
		conn.header("Accept", "text/html,application/xml,text/plain");
		conn.header("Accept-Language", "en-us,en");
		conn.header("Accept-Encoding", "gzip,deflate");
		conn.header("Accept-Charset", "utf-8");
		conn.header("Cookie", "PREF=ID=fc3f16914003fdef:LD=en:NR=10:CR=2:TM=1298983400:LM=1298983411:S=jjFFG0ocmc2JzRYc; GSP=ID=fc3f16914003fdef:IN=c1ecea4bf80120d+e10d64a62237bf8e+d0f331f4a7810a69:CF=4");
		return conn;
	}

	public static String getHash(String string) throws Exception
	{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.reset();
		digest.update(string.getBytes(Charset.forName("UTF8")));
		byte[] result = digest.digest();
		
		long hash = 0;
		for(int i = 0; i < result.length; ++i)
		{
			long a = result[i] & 0xFF;
			a <<= 8*(i % 8);
			hash ^= a;
		}
		
		return Long.toHexString(hash);
	}
	
	public static void parseBibTex(Record record, String bibtex) throws Exception
	{
		Matcher matcher = Pattern.compile("^\\s*@(\\w*)\\{").matcher(bibtex);
		if( ! matcher.find() )
			throw new IllegalArgumentException("Incorrect bibtex publication type");
		record.setValue("type", matcher.group(1));

		matcher = Pattern.compile("\\{([^\\{\\} =,]*),").matcher(bibtex);
		if( ! matcher.find() )
			throw new IllegalArgumentException("Incorrect bibtex identifier");
		
		String bibid = matcher.group(1);
		bibid = Conversion.removeAccents(bibid);
		record.setValue("bibid", bibid);
		
		record.setValue("bibmd4", getHash(bibtex));
		
		matcher = Pattern.compile("\\s*(\\w*)=\\{(([^\\{\\}]|\\{([^\\{\\}]|\\{[^\\{\\}]*\\})*\\})*)\\}").matcher(bibtex);
		while( matcher.find() )
		{
			String label = matcher.group(1);
			String value = matcher.group(2);

			if( label.equals("title") )
			{
				if( value.startsWith("{") && value.endsWith("}") )
					value = value.substring(1,value.length()-1);
				else
					System.err.println("BibTex title is not enclosed in {{ ... }}");
			}
			
			if( label.equals("author") )
				{
					value = Conversion.removeTex(value);
					record.setValue(label, value);
				}
			else if( label.equals("title") 
				| label.equals("journal")
				| label.equals("booktitle")
				| label.equals("pages")
				| label.equals("year")
				| label.equals("volume")
				| label.equals("number")
				| label.equals("isbn")
				| label.equals("issn")
				| label.equals("publisher")
				)
			{
				record.setValue(label, value);
			}
			else if( label.equals("organization") 
				| label.equals("institution") 
				| label.equals("school") 
				)
				;
			else
				System.err.println("Unknown BibTex label: " + label + "=\"" + value + "\"");
		}
	}

	Database database = new Database();
	
	public void downloadPublications(String parent, String scholarlink)
	{
		try
		{
			Table table = database.getTable("Scholar");
			Document doc = getConnection(scholarlink).get();
	
			outer: for(;;)
			{
				Elements records = doc.select("div.gs_r");
				for(Element recordx : records)
				{
					String articlelink = null;
	
					Elements links = recordx.select(".gs_rt a[href]");
					if( links.size() >= 2 )
						throw new IllegalArgumentException("Too many article links in scholar record");
					else if( links.size() == 1 )
						articlelink = links.first().attr("href");
					
					String citeslink = null;
					String bibtexlink = null;
					String finditlink = null;
					String citedby = "0";
					String doi = "";
	
					links = recordx.select(".gs_fl a[href]");
					for(Element link : links)
					{
						String text = link.text();
	
						if( text.startsWith("Cited by ") )
						{
							citeslink = link.attr("href");
							if( ! citeslink.startsWith("/scholar?cites") )
								throw new IOException("Unexpected format of cites link");
							
							citeslink = "http://scholar.google.com" + citeslink;
							citedby = text.substring(9);
						}
						
						if( text.startsWith("Find it") )
						{
							finditlink = link.attr("href");
							
							Matcher matcher = Pattern.compile("id=doi:([^&]*)").matcher(finditlink);
							if( matcher.find() )
								doi = matcher.group(1);
						}
						
						if( text.equals("Import into BibTeX") )
						{
							bibtexlink = link.attr("href");
							if( ! bibtexlink.startsWith("/scholar.bib?") )
								throw new IOException("Unexpected format of bibtex link");
	
							bibtexlink = "http://scholar.google.com" + bibtexlink;
							bibtexlink = bibtexlink.replace("oe=ASCII", "oe=UTF8");
						}
					}
					
					Matcher matcher = Pattern.compile("info:([\\w-]*):").matcher(bibtexlink);
					if( ! matcher.find() )
						throw new IllegalArgumentException("BibTex url does not contain the info field"); 
					String infoid = matcher.group(1);

					Record record = table.findRecord("infoid", infoid);
					if( record == null )
						record = table.createRecord();
	
					record.setValue("citedby", citedby);
					
					if( parent != null )
					{
						String parents = record.getValue("parents");
						if( ! ("," + parents + ",").contains("," + parent + ",") )
						{
							if( parents.length() > 0 )
								parents += ',';
					
							parents += parent;
							record.setValue("parents", parents);
						}
					}
	
					if( doi.length() > 0 )
					{
						String old = record.getValue("doi");
						if( old.length() == 0 )
							record.setValue("doi", doi);
						else if( ! old.equals(doi) )
							System.out.println("DOIs do not match for " + record.getValue("bibid"));
					}

					if( citeslink != null && table.getIndex("citesid") >= 0 )
					{
						matcher = Pattern.compile("cites=([\\d]*)\\&").matcher(citeslink);
						if( ! matcher.find() )
							throw new IllegalArgumentException("Cites url does not contain the cites field"); 
						record.setValue("citesid", matcher.group(1));
					}
	
					if( articlelink != null )
						record.setValue("link", articlelink);
					
					// if newly found
					if( record.getValue("bibid").length() == 0 )
					{
						record.setValue("infoid", infoid);
	
						String bibtex = getConnection(bibtexlink).get().body().text();
						parseBibTex(record, bibtex);
	
						table.addRecord(record);
						System.out.println(record);
					}
				}
				
				Elements links = doc.select("div.n a[href]");
				for(Element link : links)
				{
					String text = link.text();
				
					if( text.equals("Next") )
					{
						String href = link.attr("href");
						if( ! href.startsWith("/scholar?start") )
							throw new IOException("Unexpected format of next link");
						
						href = "http://scholar.google.com" + href;
						doc = getConnection(href).get();
	
						continue outer;
					}
				}
				
				// exit if no more Next links
				break;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}

		// TODO: make sure that the CD counter is incremented properly in the url list 
	}

	public void downloadRecords()
	{
/*		
		Table table = database.getTable("Sources");
		for(Record record : table.getRecords())
		{
			String url = record.getValue("url");
			if( url.contains("scholar.google.com") )
				downloadPublications(record.getValue("parent"), url);
		}
*/

		downloadPublications("maroti", "http://scholar.google.com/scholar?start=0&q=author:%22m+maroti%22&hl=en&as_sdt=1,5&as_ylo=1987&as_subj=eng&num=100");
	}
	
	public static void main(String[] args) throws Exception
	{
		Scholar scholar = new Scholar();

		scholar.database.loadExcel("Publications.xls");
		scholar.downloadRecords();
		scholar.database.saveExcel("Publications.xls");
	}
}
