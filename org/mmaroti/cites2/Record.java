/**
 * 
 */
package org.mmaroti.cites2;

public class Record
{
	private Table table;
	
	public Table getTable()
	{
		return table;
	}
	
	private String[] values;

	String getValue(int index)
	{
		return values[index];
	}
	
	public String getValue(String label)
	{
		return values[table.getIndex(label)];
	}

	void setValue(int index, String value)
	{
		values[index] = value;
	}
	
	public void setValue(String label, String value)
	{
		values[table.getIndex(label)] = value;
	}

	Record(Table table)
	{
		this.table = table;
		
		values = new String[table.getLabels().length];
		for(int i = 0; i < values.length; ++i)
			values[i] = "";
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
		String[] labels = table.getLabels();
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