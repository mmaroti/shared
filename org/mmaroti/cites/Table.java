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

public class Table
{
	String name;
	
	public String getName()
	{
		return name;
	}
	
	String[] labels;

	public int getIndex(String label)
	{
		for(int i = 0; i < labels.length; ++i)
			if( label.equals(labels[i]) )
				return i;
		
		return -1;
	}
	
	public Table(String name, String[] labels)
	{
		this.name = name;
		this.labels = labels;
	}

	public String[] getLabels()
	{
		return labels;
	}
	
	public class Record
	{
		String[] values;

		Record()
		{
			values = new String[labels.length];
			for(int i = 0; i < values.length; ++i)
				values[i] = "";
		}
		
		public String getValue(int index)
		{
			return values[index];
		}
		
		public String getValue(String label)
		{
			return values[getIndex(label)];
		}
		
		public void setValue(String label, String value)
		{
			int index = getIndex(label);
			if( index < 0 )
				throw new IllegalArgumentException("Label " + label + " is not found in the table");

			values[index] = value;
		}

		public boolean isEmpty()
		{
			for(String value : values)
				if( value.length() != 0 )
					return false;
			
			return true;
		}
		
		public String toString()
		{
			String s = "";

			for(int i = 0; i < labels.length; ++i)
			{
				if( s.length() > 0 )
					s += ' ';

				s += labels[i] + "=\"" + values[i] + "\"";
			}

			return s;
		}
	}
	
	List<Record> records = new ArrayList<Record>();

	public Record createRecord()
	{
		return new Record();
	}
	
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
			if( value.equals(record.getValue(label)) )
				return record;
		}
		
		return null;
	}
	
	public void print()
	{
		System.out.println("TABLE " + name + " with " + records.size() + " records");
		for(int i = 0; i < records.size() && i < 10; ++i)
			System.out.println(records.get(i));
	}
}
