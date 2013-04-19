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

import java.util.*;
import java.util.regex.*;

public class Printer
{
	public static String removeTex(String input)
	{
		input = input.replace("{\\'a}", "a");
		input = input.replace("{\\'i}", "i");
		input = input.replace("{\\i}", "i");
		input = input.replace("{\\'\\i}", "i");
		input = input.replace("{\\'o}", "o");
		input = input.replace("{\\'u}", "u");
		input = input.replace("{\\'e}", "e");
		input = input.replace("{\\'y}", "y");
		input = input.replace("{\\'c}", "c");
		input = input.replace("{\\`y}", "y");
		input = input.replace("{\\'A}", "A");
		input = input.replace("{\\'I}", "I");
		input = input.replace("{\\'O}", "O");
		input = input.replace("{\\'U}", "U");
		input = input.replace("{\\'E}", "E");
		input = input.replace("{\\\\\"a}", "a");
		input = input.replace("{\\\\\"o}", "o");
		input = input.replace("{\\\\\"O}", "O");
		input = input.replace("{\\\\\"u}", "u");
		input = input.replace("{\\v{z}}", "z");
		input = input.replace("{\\v{C}}", "C");
		input = input.replace("{\\v{c}}", "c");
		input = input.replace("{\\u{a}}", "a");
		input = input.replace("{\\c{c}}", "c");
		input = input.replace("{\\c{C}}", "C");
		input = input.replace("{\\c{S}}", "S");
		input = input.replace("{\\ss}", "ss");
		input = input.replace("{\\~n}", "n");
		input = input.replace("{\\u{g}}", "g");
		input = input.replace("{\\DJ}", "D");
		input = input.replace("\u2019", "'");
		input = input.replace("{\\`e}", "e");
		
		input = input.replace("$\\mu$", "u");
		input = input.replace(" \\& ", " and ");
		input = input.replace("\\& ", " and ");
		input = input.replace(" \\&", " and ");
		input = input.replace("\\&", " and ");
		
		return input;
	}
	
	public static boolean isIndependent(String authors, Table.Record record)
	{
		String aa = removeTex(record.getValue("author"));
		
		Matcher matcher = Pattern.compile("[a-zA-Z]*+").matcher(authors);
		while( matcher.find() )
		{
			String s = matcher.group();

			if( s.length() >= 4 && aa.contains(s) )
				return false;
		}
		
		return true;
	}
	
	public static String toHtml(Table.Record record)
	{
		String author = removeTex(record.getValue("author"));
		String title = removeTex(record.getValue("title"));
		String booktitle = removeTex(record.getValue("booktitle"));
		String journal = removeTex(record.getValue("journal"));

		Pattern authorPattern = Pattern.compile("^[a-zA-Z,. \\-']*$");
		Pattern titlePattern = Pattern.compile("^[0-9a-zA-Z,.:/()?+; \\-']*$");
		
		if( ! authorPattern.matcher(author).find() )
			throw new IllegalArgumentException(author);

		if( ! titlePattern.matcher(title).find() )
			throw new IllegalArgumentException(title);

		if( ! titlePattern.matcher(booktitle).find() )
		{
//			for(int i = 0; i < booktitle.length(); ++i)
//			System.err.println((int)booktitle.charAt(i));
		
			throw new IllegalArgumentException(booktitle + " (" + title + ")");
		}

		if( ! titlePattern.matcher(journal).find() )
			throw new IllegalArgumentException(journal + " (" + title + ")");

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
	
	public static void printReferences(Table.Record publication, Table references)
	{
		String author = removeTex(publication.getValue("author"));
		String parentid = publication.getValue("infoid");
		
		List<Table.Record> records = references.getRecords();
		for(Table.Record record : records)
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
