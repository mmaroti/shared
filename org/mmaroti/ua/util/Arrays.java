/**
 *	Copyright (C) Miklos Maroti, 2001-2005
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

import java.util.*;

/**
 * This class contains only static helper methods to work 
 * with arrays.
 */
public class Arrays
{
	/**
	 * Returns <code>true</code> if the two arrays have the same length 
	 * and each entry points to the same objects.
	 */
	public static boolean shallowEquals(Object[] a, Object[] b)
	{
		if( a == null || b == null )
			return a == b;
		
		int i = a.length;
		if( i != b.length )
			return false;
		
		while( --i >= 0 )
			if( a[i] != b[i] )
				return false;
		
		return true;
	}
	
	/**
	 * Calculates the hashcode from the list of integers.
	 * This method is based on the code of Daniel Phillips 
	 * <phillips@innominate.de>
	 */
	public static int hashCode(int[] a)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = a.length;
		while( --i >= 0 )
		{
			int hash = hash1 + (hash0 ^ (a[i] * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}
	
	/**
	 * Calculates the hashcode from the list of objects
	 * based on the addresses of the contained object.
	 * This method is based on the code of Daniel Phillips 
	 * <phillips@innominate.de>
	 */
	public static int shallowHashCode(Object[] a)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = a.length;
		while( --i >= 0 )
		{
			int hash = hash1 + 
				(hash0 ^ (System.identityHashCode(a[i]) * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}
	
	/**
	 * Converts a collection of Integer objects to a array
	 * of integers.
	 */
	public static int[] toIntArray(Collection<Integer> collection)
	{
		int[] ret = new int[collection.size()];
		int index = 0;
		
		Iterator<Integer> iter = collection.iterator();
		while( iter.hasNext() )
			ret[index++] = iter.next().intValue();
		
		return ret;
	}
}
