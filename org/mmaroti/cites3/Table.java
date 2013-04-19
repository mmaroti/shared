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

package org.mmaroti.cites3;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Table
{
	List<Record> records = new ArrayList<Record>();

	public void addRecord(Record record)
	{
		if( ! record.isEmpty() )
			records.add(record);
	}

	public List<Record> getRecords()
	{
		return records;
	}

	public Record findRecord(String label, String value)
	{
		for(Record record : records)
		{
			if( record.hasValue(label, value) )
				return record;
		}
		
		return null;
	}
	
	public void print()
	{
		for(int i = 0; i < records.size() && i < 10; ++i)
			System.out.println(records.get(i));
	}
	
	private static Pattern linePattern = Pattern.compile("^([a-z\\-]+):\\s*(.*)$");
	
	public void load(String filename) throws IOException
	{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), "ISO-8859-2"));
		
		int index = 0;
		String line;
		Record record = new Record();
		
		while( (line = reader.readLine()) != null )
		{
			++index;

			line = line.trim();
			if( line.length() == 0 )
			{
				if( ! record.isEmpty() )
				{
					addRecord(record);
					record = new Record();
				}
			}
			else
			{
				Matcher matcher = linePattern.matcher(line); 
				if( ! matcher.matches() )
					throw new IllegalArgumentException("Invalid line at " + index);
			
				record.addValue(matcher.group(1), matcher.group(2));
			}
		}

		if( ! record.isEmpty() )
			addRecord(record);
		
		reader.close();
	}
	
	public void save(String filename) throws IOException
	{
		File newfile = new File(filename + ".tmp");
		if( newfile.exists() && ! newfile.delete() )
			throw new IllegalArgumentException("Could not delete file: " + newfile.getName());

		Writer out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename + ".tmp"), "ISO-8859-2"));
		
		for(Record record : records)
			out.write(record.toString() + "\n\n");

		out.close();
		
		File oldfile = new File(filename);
		if( oldfile.exists() )
		{
			File bakfile = new File(filename + ".bak");

			if( bakfile.exists() && ! bakfile.delete() )
				throw new IllegalArgumentException("Could not delete file: " + bakfile.getName());
			
			if( ! oldfile.renameTo(bakfile) )
				throw new IllegalArgumentException("Could not rename file: " + oldfile.getName());
		}
		
		if( ! newfile.renameTo(oldfile) )
			throw new IllegalArgumentException("Could not rename file: " + newfile.getName());
	}

	public static void main(String[] args) throws IOException
	{
		Table table = new Table();
		table.load("publications.txt");
		
//		table.print();
		table.save("publications.txt");
	}
}
