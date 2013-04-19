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

import java.util.*;
import java.util.regex.*;

public class Printer
{
	public static boolean isIndependent(String authors, Record record)
	{
		String aa = Conversion.removeTex(record.getValue("author"));
		
		Matcher matcher = Pattern.compile("[a-zA-Z]*+").matcher(authors);
		while( matcher.find() )
		{
			String s = matcher.group();

			if( s.length() >= 4 && aa.contains(s) )
				return false;
		}
		
		return true;
	}
	
	public static String toHtml(Record record)
	{
		String author = Conversion.removeTex(record.getValue("author"));
		String title = Conversion.removeTex(record.getValue("title"));
		String booktitle = Conversion.removeTex(record.getValue("booktitle"));
		String journal = Conversion.removeTex(record.getValue("journal"));

		Conversion.verifyAuthor(author);
		Conversion.verifyTitle(title);
		Conversion.verifyTitle(booktitle);
		Conversion.verifyTitle(journal);
		
		if( booktitle.length() > 0 && journal.length() > 0 )
			booktitle += ", ";
		
		booktitle += journal;
		
		String line = "";
		line += "<li>";
		line += author;
		line += ",<br/> <i>" + title + "</i>:<br/> ";
		line += booktitle;
		
		String part;
		if( (part = record.getValue("volume")).length() > 0 )
			line += ", vol. " + part;
		
		if( (part = record.getValue("number")).length() > 0 )
			line += ", no. " + part;
		
		if( (part = record.getValue("isbn")).length() > 0 )
			line += ", isbn " + part;
		
//		if( (part = record.getValue("issn")).length() > 0 )
//			line += ", issn " + part;

		if( (part = record.getValue("year")).length() > 0 )
			line += ", " + part;

		if( (part = record.getValue("pages")).length() > 0 )
		{
			part = part.replace("--", "&ndash;");
			line += ", pp. " + part;
		}
		
		line += ".<p/></li>";
		
		return line;
	}
	
	public static void printReferences(Record publication, Table references)
	{
		String author = Conversion.removeTex(publication.getValue("author"));
		String parentid = publication.getValue("infoid");
		
		List<Record> records = references.getRecords();
		for(Record record : records)
		{
			String valid = record.getValue("valid").trim();
			if( valid.equals("n") || valid.equals("") )
				;
			else if( valid.equals("y") )
			{
				if( record.getValue("parentid").contains(parentid)
						&& isIndependent(author, record) )
					System.out.println(toHtml(record));
			}
			else
				throw new IllegalArgumentException("Illegal valid field: " + valid);
		}
	}
}
