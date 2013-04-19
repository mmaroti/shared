/**
 *	Copyright (C) Miklos Maroti, 2002
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

package org.mmaroti.ua.util;

/**
 * A utility class for enumerating all possible k-element subsets of
 * an n-element set.
 */
public class SubsetArgument extends Argument
{
	/**
	 * Constructs an argument enumerator for all possible 
	 * <code>set</code>-element subset of the set 
	 * <code>0,...,size-1</code>.
	 *
	 * @param set the size of the subsets.
	 * @param size The size of the underlying set.
	 */
	public SubsetArgument(int set, int size)
	{
		super(set);

		if( set > size )
			throw new IllegalArgumentException("the subset size cannot be larger than the underlzing set");
		
		if( size < 0 )
			throw new IllegalArgumentException("size must be non-negative");

		this.size = size;
	}

	/**
	 * The size of the underlying set.
	 */
	protected int size;

	/**
	 * Returns the size of the underlying set.
	 */
	public int getSize()
	{
		return size;
	}

	public int getMaxIndex()
	{
		int c = 1;

		for(int i = 0; i < vector.length; ++i)
		{
			c *= size - i;
			c /= i + 1;
		}

		return c;
	}

	public int getIndex()
	{
		int index = 0;
		
		for(int i = vector.length-1; i >= 0; --i)
		{
			int r = 1;
			for(int j = i; j < vector[i];)
			{
				index += r;
				++j;
				r *= j;
				r /= j-i;
			}
		}
		
		return index;
	}

	public void setIndex(int index)
	{
		for(int i = vector.length-1; i >= 0; --i)
		{
			int r = 1;
			int j = i;
			
			while( index >= r )
			{
				index -= r;
				++j;
				r *= j;
				r /= j-i;
			}
			
			vector[i] = j;
		}
	}

	public boolean next()
	{
		for(int i = 0; i < vector.length; ++i)
		{
			if( i == vector.length-1 )
				return ++vector[i] < size;

			if( ++vector[i] < vector[i+1] )
				return true;
			
			vector[i] = i;
		}
		
		return vector.length > 0;
	}

	public boolean reset()
	{
		for(int i = 0; i < vector.length; ++i)
			vector[i] = i;
		
		return true;
	}
}
