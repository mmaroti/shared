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

package org.mmaroti.cites;

import java.io.*;
import java.util.regex.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Scholar
{
	static int maxConnections = 11111;
	
	public static class TooManyConnectionsException extends IOException
	{
		private static final long serialVersionUID = 51944780478954114L;

		TooManyConnectionsException(String message)
		{
			super(message);
		}
	}
	
	public static Connection getConnection(String url) throws IOException
	{
		if( --maxConnections <= 0 )
			throw new TooManyConnectionsException("Too many HTML requests");
		
		Connection conn = Jsoup.connect(url);
		conn.header("User-Agent", "Mozilla");
		conn.header("Accept", "text/html,application/xml,text/plain");
		conn.header("Accept-Language", "en-us,en");
		conn.header("Accept-Encoding", "gzip,deflate");
		conn.header("Accept-Charset", "utf-8");
		conn.header("Cookie", "S=sorry=Xi3MCxl6E0LAVxIO5YW-pQ; GDSESS=ID=8afb9fc8df93df11:EX=1298783648:S=ADSvE-fZS4-Bj7M1jg5Fpqm1ndX7Ta87pw; GSP=ID=b8cbb3d20a139154:IN=c1ecea4bf80120d+e10d64a62237bf8e+d0f331f4a7810a69:CF=4; PREF=ID=b8cbb3d20a139154:LD=en:NR=10:TM=1298772849:LM=1298772857:S=iB_jNfKc_jiuMVsx");
		return conn;
	}

	public static void parseBibTex(Table.Record record, String bibtex)
	{
		Matcher matcher = Pattern.compile("^\\s*@(\\w*)\\{").matcher(bibtex);
		if( ! matcher.find() )
			throw new IllegalArgumentException("Incorrect bibtex publication type");
		record.setValue("type", matcher.group(1));

		matcher = Pattern.compile("\\{([^\\{\\} =,]*),").matcher(bibtex);
		if( ! matcher.find() )
			throw new IllegalArgumentException("Incorrect bibtex identifier");
		record.setValue("bibid", matcher.group(1));
		
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
			
			if( label.equals("title") 
				| label.equals("author")
				| label.equals("journal")
				| label.equals("booktitle")
				| label.equals("pages")
				| label.equals("year")
				| label.equals("volume")
				| label.equals("number")
				| label.equals("isbn")
				| label.equals("issn")
				)
			{
				record.setValue(label, value);
			}
			else if( label.equals("organization") 
				| label.equals("institution") 
				| label.equals("school") 
				| label.equals("publisher")
				)
				;
			else
				System.err.println("Unknown BibTex label: " + label + "=\"" + value + "\"");
		}
	}

	public static void parsePublication(Table table, String articlelink, 
			String citeslink, String bibtexlink, String citedby, String parentid) throws IOException
	{
		Matcher matcher = Pattern.compile("info:([\\w-]*):").matcher(bibtexlink);
		if( ! matcher.find() )
			throw new IllegalArgumentException("BibTex url does not contain the info field"); 
		String infoid = matcher.group(1);

		Table.Record record = table.findRecord("infoid", infoid);

		boolean oldRecord = true;
		if( record == null )
		{
			record = table.createRecord();
			oldRecord = false;
		}

		record.setValue("citedby", citedby);
		
		if( parentid != null )
		{
			String parents = record.getValue("parentid");
			if( parents.length() > 0 )
				parents += ',';
			parents += parentid;
			record.setValue("parentid", parents);
		}

		if( oldRecord )
			return;

		record.setValue("infoid", infoid);

		if( citeslink != null && table.getIndex("citesid") >= 0 )
		{
			matcher = Pattern.compile("cites=([\\d]*)\\&").matcher(citeslink);
			if( ! matcher.find() )
				throw new IllegalArgumentException("Cites url does not contain the cites field"); 
			record.setValue("citesid", matcher.group(1));
		}
		
		String bibtex = getConnection(bibtexlink).get().body().text();
		parseBibTex(record, bibtex);

		table.addRecord(record);
		System.out.println(record);
	}

	public static void parsePublications(Table table, String scholarlink, String parentid) throws IOException
	{
		Document doc = getConnection(scholarlink).get();

		outer: for(;;)
		{
			Elements records = doc.select("div.gs_r");
			for(Element record : records)
			{
				String articlelink = null;
				String citeslink = null;
				String bibtexlink = null;
				String citedby = "0";

				Elements links = record.select(".gs_rt a[href]");
				if( links.size() >= 2 )
					throw new IllegalArgumentException("Too many article links in scholar record");
				
				if( links.size() == 1 )
					articlelink = links.first().attr("href");
				
				links = record.select(".gs_fl a[href]");
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
					
					if( text.equals("Import into BibTeX") )
					{
						bibtexlink = link.attr("href");
						if( ! bibtexlink.startsWith("/scholar.bib?") )
							throw new IOException("Unexpected format of bibtex link");

						bibtexlink = "http://scholar.google.com" + bibtexlink;
						bibtexlink = bibtexlink.replace("oe=ASCII", "oe=UTF8");
					}
				}
				
				parsePublication(table, articlelink, citeslink, bibtexlink, citedby, parentid);
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

		// TODO: make sure that the CD counter is incremented properly in the url list 
	}

	public static void updateRecords(Database database)
	{
		try
		{
// read all publications of maroti
//			parsePublications(database.getTable("Publications"),
//				"http://scholar.google.com/scholar?hl=en&q=author:%22m+maroti%22&btnG=Search&as_sdt=0,5&as_ylo=&as_vis=0&num=100",
//				null);
	
			List<Table.Record> records = database.getTable("Publications").getRecords();
			for(Table.Record record : records)
			{
				if( record.getValue("valid").equals("y") )
				{
					String citesid = record.getValue("citesid");
					String infoid = record.getValue("infoid");

					parsePublications(database.getTable("References"),
						"http://scholar.google.com/scholar?cites=" + citesid + "&as_sdt=2005&sciodt=0,5&hl=en&num=100&start=0",
						infoid);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		Database database = new Database();
		database.loadExcel("Publications.xls");

		List<Table.Record> parents = database.getTable("Publications").getRecords();
		for(int i = 0; i < 10; ++i)
		{
			Table.Record parent = parents.get(i);

			String header = Printer.toHtml(parent);
			header = header.replace("li>", "h3>");
			System.out.println(header);
			System.out.println("<ol>");
			Printer.printReferences(parent, database.getTable("References"));
			System.out.println("</ol>");
		}

//		updateRecords(database);
//		database.saveExcel("Publications.xls");
	}
}
