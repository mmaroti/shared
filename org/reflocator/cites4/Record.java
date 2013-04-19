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

/**
 * A single row in a database with values for a fixed number of keys (columns).
 */
public abstract class Record
{
	/**
	 * Returns the table that owns this record. The table might not
	 * contain this record if it has been removed or not yet added,
	 * but it still owns the record.
	 */
	public abstract Table getTable();

	/**
	 * Returns the value of the given key by index. We use the order
	 * of keys as returned by <code>getTable().getKeys()</code>.
	 */
	public abstract String getValue(int index);

	/**
	 * Returns the value corresponding to the given key.
	 */
	public String getValue(String key)
	{
		return getValue(getTable().getIndex(key));
	}

	/**
	 * Sets the value of the given key by index. We use the order
	 * of keys as returned by <code>getTable().getKeys()</code>.
	 */
	public abstract void setValue(int index, String value);

	/**
	 * Sets the value corresponding to the given key.
	 */
	public void setValue(String key, String value)
	{
		setValue(getTable().getIndex(key), value);
	}
	
	/**
	 * Returns <code>true</code> if the given record (or rather its
	 * table) has value for the given key.
	 */
	public boolean hasKey(String key)
	{
		return getTable().hasKey(key);
	}

	/**
	 * Returns the set of keys of this record (or rather its table).
	 */
	public String[] getKeys()
	{
		return getTable().getKeys();
	}
	
	/**
	 * Copies all values from the origin record to this one.
	 */
	public void setValues(Record origin)
	{
		for(String key : origin.getKeys())
		{
			if( hasKey(key) && ! origin.getValue(key).equals("") )
				setValue(key, origin.getValue(key));
		}
	}
	
	/**
	 * Returns some very basic string representation of the record
	 */
	public String toString()
	{
		String s = "";

		for(String key : getTable().getKeys() )
			s += key + ":\t" + getValue(key) + "\n";

		return s;
	}
	
	/**
	 * Returns <code>true</code>, if this record contains those keys
	 * in the pattern and has the exact same values for them.
	 */
	public boolean matches(Map<String,String> pattern)
	{
		for( Map.Entry<String, String> entry : pattern.entrySet())
			if( ! entry.getValue().equals(getValue(entry.getKey())) )
				return false;
		
		return true;
	}
}
