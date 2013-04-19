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
 * A utility class for enumerating all possible arguments of a
 * finitary operation over a finite set. 
 */
public class CubeArgument extends Argument
{
	/**
	 * Constructs an argument enumerator for a finitary operation 
	 * over a finite set.
	 *
	 * @param arity The arity of the operation.
	 * @param size The size of the underlying set.
	 */
	public CubeArgument(int arity, int size)
	{
		super(arity);

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
		return power(size, vector.length);
	}

	public int getIndex()
	{
		int s = 0;
		
		for(int i = 0; i < vector.length; ++i)
		{
			s *= size;
			s += vector[i];
		}
		
		return s;
	}

	public void setIndex(int index)
	{
		int i = vector.length;
		while( --i >= 0 )
		{
			vector[i] = index % size;
			index /= size;
		}
	}

	public boolean next()
	{
		int i = vector.length;
		while( --i >= 0 )
			if( ++vector[i] >= size )
				vector[i] = 0;
			else
				return true;
		
		return false;
	}

	public boolean reset()
	{
		for(int i = 0; i < vector.length; ++i)
			vector[i] = 0;
		
		return size > 0 || vector.length == 0;
	}

	/**
	 * Implements the power function.
	 */
	public static int power(int base, int exponent)
	{
		if( base < 0 )
			throw new IllegalArgumentException("base must be non-negative");

		if( exponent < 0 )
			throw new IllegalArgumentException("exponent must be non-negative");
		
		int a = 1;
		while( --exponent >= 0 )
			a *= base;
		
		return a;
	}

	/**
	 * Implements the logarithm function, the power must be
	 * a whole power of the base.
	 */
	public static int logarithm(int power, int base)
	{
		if( base <= 0 )
			throw new IllegalArgumentException("the base must be positive");
		
		int a = 1;
		int b = 0;
		while( a != power )
		{
			if( power < a )
				throw new IllegalArgumentException("not an integer power");

			a *= base;
			++b;
		}

		return b;
	}
}
