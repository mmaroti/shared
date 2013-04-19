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

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * A single row in a database with values for a fixed number of keys (columns).
 */
public class Engine
{
	protected Collection<Table> tables;
	
	public Engine(Collection<Table> tables)
	{
		this.tables = tables;
	}

	protected Table getTable(String name)
	{
		for(Table table : tables)
			if( table.getName().equals(name) )
				return table;
		
		throw new IllegalArgumentException("Table not found");
	}
	
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	protected String today = dateFormat.format(new Date());

	protected static Date parseDate(String date) throws ParseException
	{
		date = date.trim();
		if( date.length() == 0 )
			return new Date(0);
		
		return dateFormat.parse(date);
	}

	protected static List<String> getParents(Record record)
	{
		List<String> list = new ArrayList<String>();
		
		String[] parents = record.getValue("parents").split(",");
		for(String parent : parents)
			list.add(parent.trim());
		
		return list;
	}
	
	protected static void setParents(Record record, List<String> parents)
	{
		String p = "";
		
		for(String parent : parents)
		{
			if( p.length() > 0 )
				p += ',';
			
			p += parent;
		}

		record.setValue("parents", p);
	}
	
	protected static void addParent(Record record, String parent)
	{
		List<String> parents = getParents(record);

		if( ! parents.contains(parent) )
		{
			parents.add(parent);
			setParents(record, parents);
		}
	}

	public void mergeRecord(String parent, Record record, Table table, DataProvider provider) throws IOException
	{
		List<Record> identical = new ArrayList<Record>();
		List<Record> equivalent = new ArrayList<Record>();
		List<Record> similar = new ArrayList<Record>();

		for(int tries = 1; tries <= 2; ++tries)
		{
			if( tries == 2 )
			{
				record = provider.getFullRecord(record);

				identical.clear();
				equivalent.clear();
				similar.clear();
			}
				
			for(Record target : table)
			{
				int c = provider.compare(record, target);

				if( c == DataProvider.IDENTICAL )
					identical.add(target);
				else if( c == DataProvider.EQUIVALENT )
					equivalent.add(target);
				else if( c == DataProvider.SIMILAR )
					similar.add(target);
			}
		
			if( identical.size() >= 1 )
			{
				if( identical.size() >= 2 )
				{
					System.out.print("More then one identical records found:");
					for(Record target : identical)
						System.out.print(" " + target.getValue("uuid"));
					System.out.println();
				}

				Record target = identical.get(0);
				if( tries == 1 && target.getValue("bibid").equals("") )
					continue;

				target.setValue("updated", today);
				addParent(target, parent);

				target.setValues(record);
				return;
			}
		}

		String uuid;
		if( equivalent.size() > 0 )
			uuid = equivalent.get(0).getValue("uuid");
		else
			uuid = UUID.randomUUID().toString();
			
		Record target = table.createRecord();

		target.setValue("uuid", uuid);
		addParent(target, parent);
		target.setValue("created", today);
		target.setValue("updated", today);

		target.setValues(record);
		table.addRecord(target);
	}
	
	public void mergeTable(String parent, Table source, Table table, DataProvider comparator) throws IOException
	{
		for(Record record : source)
			mergeRecord(parent, record, table, comparator);
	}

	protected GoogleScholar scholar = new GoogleScholar();
	
	public void processSources() throws ParseException, IOException
	{
		try
		{
			Date yesterday = new Date(new Date().getTime() - 24*60*60*1000);

			Table sources = getTable("queries");
			for(Record record : sources)
			{
				Date updated = parseDate(record.getValue("updated"));
				if( updated.after(yesterday) )
					continue;

				DataProvider provider;
				String providerName = record.getValue("provider");
			
				if( providerName.equals("google") )
					provider = scholar;
				else
					throw new IllegalArgumentException("Unknown provider: " + providerName);

				Table table = provider.getPartialRecords(record.getValue("query"));
				mergeTable(record.getValue("uuid"), table, getTable(providerName), provider);
			
				record.setValue("updated", today);
			}
		}
		catch(IOException e)
		{
			System.err.println("IO exception: " + e.getMessage());
		}
	}
	
	protected void checkDates(Table table, String key)
	{
		for(Record record : table)
		{
			try
			{
				Date date = parseDate(record.getValue(key));
				record.setValue(key, dateFormat.format(date));
			}
			catch(ParseException e)
			{
				System.err.println("Unrecognized date '" + record.getValue(key)
						+ "' in table " + table.getName());
			}
		}
	}
	
	public void checkDates()
	{
		checkDates(getTable("queries"), "updated");
		checkDates(getTable("google"), "created");
		checkDates(getTable("google"), "updated");
	}
	
	public static void main(String[] args) throws Exception
	{
		ExcelDatabase database = new ExcelDatabase();
		database.open("database.xls");
		
		Engine engine = new Engine(database.getTables());
		
		engine.checkDates();
		engine.processSources();
		
		database.save();
	}
}
