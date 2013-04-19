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

import java.util.*;

/**
 * This class captures transformation monoids
 */
public class TransSemigroup
{
	int universe;
	
	/**
	 * Returns the size of the universe
	 */
	public int getUniverse()
	{
		return universe;
	}
	
	public TransSemigroup(int universe)
	{
		assert( universe > 0 );
		
		this.universe = universe;
	}
	
	List<int[]> elements = new ArrayList<int[]>();

	/**
	 * Returns the number of transformations in the monoid
	 */
	public int getSize()
	{
		return elements.size();
	}
	
	/**
	 * Returns the transformations in a list
	 */
	public List<int[]> getElements()
	{
		return elements;
	}
	
	/**
	 * Returns the <code>index</code>-th element.
	 */
	public int[] getElement(int index)
	{
		return elements.get(index);
	}

	/**
	 * Return the index of the given transformation
	 */
	public int getIndex(int[] transformation)
	{
		assert( transformation.length == universe );

		outer: for(int j = 0; j < elements.size(); ++j)
		{
			int[] t = elements.get(j);

			int i = transformation.length;
			while( --i >= 0 )
			{
				if( transformation[i] != t[i] )
					continue outer;
			}
			
			if( i < 0 )
				return j;
		}

		return -1;
	}
	
	public boolean contains(int[] transformation)
	{
		return getIndex(transformation) >= 0;
	}

	/**
	 * Adds the transformation to the semigroup if it is not already there.
	 */
	public void add(int[] transformation)
	{
		if( getIndex(transformation) < 0 )
			elements.add(transformation);
	}

	public static int[] product(int[] a, int[] b)
	{
		assert( a.length == b.length );
		
		int[] c = new int[b.length];
		for(int i = 0; i < c.length; ++i)
			c[i] = a[b[i]];
		
		return c;
	}
	
	public String printElements()
	{
		String s = "";
		
		for(int[] t : elements)
		{
			if( s.length() > 0 )
				s += ' ';
			
			for(int i = 0; i < t.length; ++i)
				s += t[i];
		}
		
		return s;
	}

	public String printOrderedElements()
	{
		String[] strings = new String[elements.size()];

		for(int a = 0; a < strings.length; ++a)
		{
			int[] t = elements.get(a);
			String s = "";
			
			for(int i = 0; i < t.length; ++i)
				s += t[i];
			
			strings[a] = s;
		}
		
		Arrays.sort(strings);
		
		String s = "";
		for(int a = 0; a < strings.length; ++a)
		{
			if( a != 0 )
				s += ' ';
			
			s += strings[a];
		}
	
		return s;
	}
	
	public void calculateClosure()
	{
		for(int i = 0; i < elements.size(); ++i)
			for(int j = 0; j <= i; ++j)
			{
				add(product(elements.get(i), elements.get(j)));
				
				if( i != j )
					add(product(elements.get(j), elements.get(i)));
			}
	}
}
