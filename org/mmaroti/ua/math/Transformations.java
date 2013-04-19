/**
 *	Copyright (C) Miklos Maroti, 2010
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

package org.mmaroti.ua.math;

import org.mmaroti.ua.set.*;

/**
 * This class captures transformation semigroups
 */
public class Transformations extends Set
{
	int universe;
	int size;
	
	public Transformations(int universe)
	{
		if( universe < 1 || universe > 9 )
			throw new IllegalArgumentException();
		
		this.universe = universe;

		temp1 = new int[universe];
		temp2 = new int[universe];

		size = 1;
		for(int i = 0; i < universe; ++i)
			size *= universe;
	}
	
	public int getSize()
	{
		return size;
	}

	public int getUniverse()
	{
		return universe;
	}
	
	public int getIndex(int[] map)
	{
		assert( map.length == universe );
		
		int a = 0;
		int i = universe;
		for(;;)
		{
			a += map[--i];

			assert( 0 <= map[i] && map[i] < universe );

			if( i <= 0 )
				break;

			a *= universe; 
		}

		return a;
	}

	public int getIndex(Object element)
	{
		return getIndex((int[])element);
	}

	public Object getElement(int index)
	{
		assert( 0 <= index && index < universe );

		int[] map = new int[universe];
		
		int i = 0;
		while( index > 0 )
		{
			map[i++] = index % universe;
			index /= universe;
		}
		
		return map;
	}
	
	public boolean areEquals(int[] map1, int[] map2)
	{
		assert( map1.length == universe );
		assert( map2.length == universe );
		
		int i = universe;
		while( --i >= 0 )
			if( map1[i] != map2[i] )
				return false;
		
		return true;
	}

	public boolean areEquals(Object elem1, Object elem2)
	{
		return areEquals((int[])elem1, (int[])elem2);
	}

	/**
	 * Calculates the hash code from the list of objects.
	 * This method is based on the code of Daniel Phillips 
	 * <phillips@innominate.de>
	 */
	public int hashCode(int[] map)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = universe;
		while( --i >= 0 )
		{
			int hash = hash1 + (hash0 ^ (map[i] * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}

	public int hashCode(Object elem)
	{
		return hashCode((int[])elem);
	}

	public String toString(int[] map)
	{
		String s = "";

		for(int i = 0; i < universe; ++i)
		{
			s += map[i];
		}

		return s;
	}

	public String toString(Object elem)
	{
		return toString((int[])elem);
	}

	public Object parse(String string)
	{
		string = string.trim();

		if( string.length() != universe )
			return null;
		
		int[] map = new int[universe];
		for(int i = 0; i < universe; ++i)
		{
			int a = string.charAt(i) - '0';
			if( a < 0 || a >= universe )
				return null;

			map[i] = a; 
		}
		
		return map;
	}

	public int[] product(int[] map1, int[] map2)
	{
		assert( map1.length == universe );
		assert( map2.length == universe );

		int[] map = new int[universe];
		
		for(int i = 0; i < universe; ++i)
			map[i] = map1[map2[i]];
		
		return map;
	}

	private int[] temp1;
	private int[] temp2;
	
	public int product(int map1, int map2)
	{
		assert( map1 >= 0 && map1 < size );
		assert( map2 >= 0 && map2 < size );
		
		int i = 0;
		while( i < universe )
		{
			temp1[i++] = map1 % universe;
			map1 /= universe;
			
			temp2[i++] = map2 % universe;
			map2 /= universe;
		}

		assert( i == universe );

		int a = 0;
		for(;;)
		{
			a += temp1[temp2[--i]];

			if( i <= 0 )
				break;

			a *= universe; 
		}

		return a;
	}
	
	public int[] getIdentity()
	{
		int[] map = new int[universe];
		
		for(int i = 0; i < universe; ++i)
			map[i] = i;
		
		return map;
	}
	
	public int[] getConstant(int a)
	{
		int[] map = new int[universe];
		
		for(int i = 0; i < universe; ++i)
			map[i] = a;
		
		return map;
	}
}
