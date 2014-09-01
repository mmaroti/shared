/**
 *	Copyright (C) Miklos Maroti, 2004-2007
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

package org.mmaroti.ua.set;

/**
 * This class implements partial maps from a base set to arbitrary objects.
 * The class also returns its domain as a subset of the base set. 
 */
public class Map extends SubUniverse
{
	/**
	 * Constructs an empty map with the given underlying universe.
	 */
	public Map(Universe base)
	{
		super(base);
		values = new Object[keys.length];
	}

	/**
	 * These are the matching values in index order 
	 */
	protected Object[] values;

	/**
	 * Returns the assigned object to the given integer,
	 * or <code>null</code> if no such exists.
	 */
	public Object getValue(Object key)
	{
		int index = getIndex(key);
		
		return index < 0 ? null : values[index];
	}

	/**
	 * Returns the value for the specified index, such as
	 * returned by the {@see #getIndex} method. 
	 * This method is a very fast, implemented by a simple lookup.
	 * 
	 * @param index the index of the search key
	 * @return the value assigned to the key whose index is specified.
	 */
	public Object getValue(int index)
	{
		if( index < 0 || index >= size )
			throw new IllegalArgumentException("invalid index");
		
		return values[index];
	}

	/**
	 * Sets the value for the specified index, such as
	 * returned by the {@see #getIndex} method. 
	 * This method is a very fast, implemented by a simple lookup.
	 * 
	 * @param index the index of the search key
	 * @param value the new value that will be assigned to the key specified by the index.
	 */
	public void setValue(int index, Object value)
	{
		if( index < 0 || index >= size )
			throw new IllegalArgumentException("invalid index");
		
		values[index] = value;
	}

	protected void moveValue(int source, int destination)
	{
		values[destination] = values[source];
		values[source] = null;
	}
	
	/**
	 * Assigns a new value to the give key and returns the 
	 * previous value assigned.
	 * 
	 * @param key the key object
	 * @param value the value object
	 * @return the old value object, or <code>null</code> if
	 * no such was found.
	 */
	public Object put(Object key, Object value)
	{
		final int prime = table.length;
		final int hash = hashCode(key) & Integer.MAX_VALUE;
		int slot = hash % prime;
		final int step = 1 + (hash % (prime-2));
		int free = -1;

		for(;;)
		{
			int index = table[slot];

			if( index < 0 )
			{
				if( free < 0 )
					free = slot;

				if( index == EMPTY )
					break;
			}
			else if( areEquals(key, keys[index]) )
			{
				Object old = values[index];
				values[index] = value;
				return old;
			}

			if( (slot -= step) < 0 )
				slot += prime;
		}		

		if( table[slot] == EMPTY )
			--emptySlots;

		table[slot] = size;
		keys[size] = key;
		values[size] = value;
		++size;

		if( size == keys.length || emptySlots < keys.length )
			resize(size);
		
		return null; 
	}
	
	protected void resizeValues(int capacity)
	{
		Object[] v = values;
		values = new Object[capacity];
		System.arraycopy(v, 0, values, 0, size);
	}
}
