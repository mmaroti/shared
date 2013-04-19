/**
 *	Copyright (C) Miklos Maroti, 2005
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
 * This class represents finite fields.
 */
public class FiniteField extends FiniteSet implements Field
{
	/**
	 * Constructs the Galois field of order <code>size<code>.
	 * @param size the size of the finite field, which
	 * must be a prime power.
	 */
	public FiniteField(int size)
	{
		super(size);
		
		if( size <= 0 )
			throw new IllegalArgumentException();
		else if( size != 2 && size != 3 && size != 4 && size != 5 )
			throw new IllegalArgumentException(
				"currently only fields of order 2, 3, 4 and 5 are supported");
	}

	public int sum(int a, int b)
	{
		if( size == 4 )
			return a^b;	// binary exclusive or
		else
			return (a+b) % size;
	}

	public int negative(int a)
	{
		if( size == 4 )
			return a;
		else
			return a == 0 ? 0 : size - a;
	}

	public int zero()
	{
		return 0;
	}

	public int product(int a, int b)
	{
		if( size == 4 )
		{
			if( a == 0 || b == 0 )
				return 0;
			else
				return (a + b + 1) % 3 + 1;
		}
		else
			return (a*b) % size;
	}

	public int inverse(int a)
	{
		if( a == 0 )
			throw new IllegalArgumentException("zero element has no inverse");
		else if( a == 1 )
			return a;
		
		if( size == 4 || (size == 5 && a < 4) )
			return 5 - a;
		else
			return a;
	}

	public int unit()
	{
		return 1;
	}

	public int character()
	{
		if( size == 4 )
			return 2;
		else
			return size;
	}

	public Object sum(Object a, Object b)
	{
		return getElement(sum(getIndex(a), getIndex(b)));
	}

	public Object negative(Object a)
	{
		return getElement(negative(getIndex(a)));
	}

	public Object zeroElement()
	{
		return getElement(zero());
	}

	public Object product(Object a, Object b)
	{
		return getElement(product(getIndex(a), getIndex(b)));
	}

	public Object inverse(Object a)
	{
		return getElement(inverse(getIndex(a)));
	}

	public Object unitElement()
	{
		return getElement(unit());
	}
	
	public static void main(String[] args)
	{
		int size = 5;
		Field field = new FiniteField(size);
		
		System.out.println("addition");
		for(int i = 0; i < size; ++i)
		{
			for(int j = 0; j < size; ++j)
				System.out.print(" " + field.sum(i, j));
			
			System.out.println();
		}

		System.out.println("negative");
		for(int i = 0; i < size; ++i)
			System.out.print(" " + field.negative(i));
		System.out.println();

		System.out.println("multiplication");
		for(int i = 0; i < size; ++i)
		{
			for(int j = 0; j < size; ++j)
				System.out.print(" " + field.product(i, j));
			
			System.out.println();
		}
		
		System.out.println("inverse");
		System.out.print(" -");
		for(int i = 1; i < size; ++i)
			System.out.print(" " + field.inverse(i));
		System.out.println();
	}
}
