/**
 * 
 */
package org.mmaroti.cites3;

import java.util.*;

public class Record
{
	static class Entry
	{
		String label;
		String value;

		Entry(String label, String value)
		{
			this.label = label;
			this.value = value;
		}
	}

	List<Entry> entries = new ArrayList<Entry>();

	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
	
	public String getValue(String label)
	{
		checkLabel(label);
		
		for(Entry entry : entries)
		{
			if( entry.label.equals(label) )
				return entry.value;
		}
		
		return null;
	}

	public boolean hasValue(String label, String value)
	{
		checkLabel(label);

		for(Entry entry : entries)
		{
			if( entry.label.equals(label) && entry.value.equals(value) )
				return true;
		}
		
		return false;
		
	}
	
	public void addValue(String label, String value)
	{
		if( hasValue(label, value) )
			return;
		
		entries.add(new Entry(label, value));
	}

	public void addFirst(String label, String value)
	{
		if( hasValue(label, value) )
			return;
		
		entries.add(0, new Entry(label, value));
	}
	
	public void setValue(String label, String value)
	{
		checkLabel(label);
		
		for(Entry entry : entries)
		{
			if( entry.label.equals(label) )
			{
				entry.value = value;
				return;
			}
		}

		entries.add(new Entry(label, value));
	}
	
	private static final String[] labels = new String[]
	{
		"id",
		"author",
		"title",
		"type",
		"journal",
		"volume",
		"number",
		"book",
		"year",
		"pages",
		"issn",
		"isbn",
		"publisher",
		"doi",
		"url",
		"parent",
		"citedby",
		"google",
	};
	
	public static void checkLabel(String label)
	{
		for(String l : labels)
			if( l.equals(label) )
				return;
		
		throw new IllegalArgumentException("Unknown label: " + label);
	}
	
	public String toString()
	{
		String s = "";

		for(String label : labels)
		{
			for(Entry entry : entries)
			{
				if( label.equals(entry.label) )
					s += entry.label + ":\t" + entry.value + "\n";
			}
		}

		return s;
	}
}
