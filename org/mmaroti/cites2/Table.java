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

public class Table
{
	String name;
	
	public String getName()
	{
		return name;
	}
	
	String[] labels;

	public String[] getLabels()
	{
		return labels;
	}
	
	int getIndex(String label)
	{
		for(int i = 0; i < labels.length; ++i)
			if( label.equals(labels[i]) )
				return i;
		
		throw new IllegalArgumentException("Unknown label " + label);
	}
	
	Table(String name, String[] labels)
	{
		this.name = name;
		this.labels = labels;
	}

	List<Record> records = new ArrayList<Record>();

	public Record createRecord()
	{
		return new Record(this);
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
		int index = getIndex(label);
		
		for(Record record : records)
		{
			if( value.equals(record.getValue(index)) )
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
