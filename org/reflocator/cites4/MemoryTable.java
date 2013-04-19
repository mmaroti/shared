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

package org.reflocator.cites4;

import java.util.*;

public class MemoryTable extends Table
{
	public MemoryTable(String name, String[] keys)
	{
		super(name, keys);
	}

	protected class MemoryRecord extends Record
	{
		String[] values;
		
		MemoryRecord()
		{
			values = new String[getTable().getKeys().length];
		}

		public Table getTable()
		{
			return MemoryTable.this;
		}
		
		public String getValue(int index)
		{
			String value = values[index];

			return value == null ? "" : value;
		}

		public void setValue(int index, String value)
		{
			values[index] = value;
		}
	};
	
	List<Record> records = new ArrayList<Record>();

	public int getSize()
	{
		return records.size();
	}
	
	public Record createRecord()
	{
		return new MemoryRecord();
	}

	public void addRecord(Record record)
	{
		if( !(record instanceof MemoryRecord) || record.getTable() != this )
			throw new IllegalArgumentException("You cannot add an incompatible record to memory table");
		
		records.add(record);
	}

	public void deleteRecord(Record record)
	{
		records.remove(record);
	}

	public Iterator<Record> iterator()
	{
		return records.iterator();
	}
	
	public List<Record> findRecords(Map<String,String> pattern)
	{
		List<Record> result = new ArrayList<Record>();

		for(Record record : records)
		{
			if( record.matches(pattern) )
				result.add(record);
		}
			
		return result;
	}
		
	public void print()
	{
		System.out.println("TABLE " + name);
		for(Record record : records)
			System.out.println(record);
	}
	
	public int getIndex(String key)
	{
		for(int i = 0; i < keys.length; ++i)
			if( key.equals(keys[i]) )
				return i;

		throw new IllegalArgumentException("Unknown key " + key);
	}
}
