/**
 *	Copyright (C) Miklos Maroti, 2009
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
 * A utility class for enumerating all permutations of a
 * a finite set.
 */
public class PermArgument extends Argument
{
	/**
	 * Constructs an enumerator for all permutations 
	 * over a finite set. 
	 *
	 * @param size The size of the underlying set.
	 */
	public PermArgument(int size)
	{
		super(size);
		inverse = new int[size];
	}

	/**
	 * Returns the size of the underlying set.
	 */
	public int getSize()
	{
		return vector.length;
	}

	/**
	 * The inverse of the permutation
	 */
	protected int[] inverse;

	/**
	 * Returns the inverse of the current permutation.
	 * This array is updated, so it always reflects the
	 * inverse of the current permutation.
	 */
	public int[] getInverse()
	{
		return inverse;
	}
	
	public int getMaxIndex()
	{
		int a = 1;
		for(int i = 2; i <= vector.length; ++i)
			a *= i;
		
		return a;
	}

	public int getIndex()
	{
		int a = 0;
		for(int i = 0; i < vector.length; ++i)
		{
			int b = vector[i];
			int c = 0;
			for(int j = 0; j < i; ++j)
				if( vector[j] < b )
					++c;
			
			a *= vector.length - i;
			a += b - c;
		}
		return a;
	}

	public void setIndex(int index)
	{
		java.util.Arrays.fill(inverse, -1);
		
		int a = getMaxIndex();
		for(int i = 0; i < vector.length; ++i)
		{
			a /= vector.length - i;
			
			int b = index / a;
			index %= a;

			int c = -1;
			while( b >= 0 )
				if( inverse[++c] < 0 )
					--b;
			
			vector[i] = c;
			inverse[c] = i;
		}
	}

	// TODO: optimize this method, this is highly non-efficient
	public boolean next()
	{
		int a = getIndex() + 1;
		if( a >= getMaxIndex() )
			return false;
		
		setIndex(a);
		return true;
	}

	public boolean reset()
	{
		for(int i = 0; i < vector.length; ++i)
		{
			vector[i] = i;
			inverse[i] = i;
		}
		
		return true;
	}

	public String toString()
	{
		String s = "[";
		
		if( vector.length > 0 )
		{
			s += Integer.toString(vector[0]);
		
			for(int i = 1; i < vector.length; ++i)
				s += "," + Integer.toString(vector[i]);
		}
		
		s += "],[";
		
		if( vector.length > 0 )
		{
			s += Integer.toString(inverse[0]);
		
			for(int i = 1; i < inverse.length; ++i)
				s += "," + Integer.toString(inverse[i]);
		}
		
		return s + "]";
	}
}
