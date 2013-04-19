/**
 *	Copyright (C) Miklos Maroti, 2003-2006
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

import org.mmaroti.ua.util.*;
import java.util.*;

/**
 * This class can calculate the product of finitely many sets. This class
 * can work for both enumerable and non-enumerable universes. The elements
 * of the product are enumerated (if that is possible) such that 
 * <pre>
 *      0 = &lt;0,...,0&gt;
 *      1 = &lt;0,...,1&gt;
 *       ...
 * size-1 = &lt;size_0-1,...,size_{k-1}-1&gt;
 * </pre>
 * where <code>size = size_0 * ... * size_{k-1}</code>. 
 *  
 * @author mmaroti@math.u-szeged.hu
 */
public class ProductSet extends Set
{
	/**
	 * Holds the factor universes.
	 */
	protected Set[] factors;
	
	/**
	 * Holds the sizes of the factor universes if the product is enumerable.
	 */
	protected int[] universeSizes;
	
	/**
	 * @return the list of factor universes that was originally
	 * passed to {@link ProductSet#ProductSet}. 
	 * The returned list should not be modified.
	 */
	public Set[] getFactors()
	{
		return factors;
	}
	
	/**
	 * Holds the size of the product set. This value is 0 if one of the
	 * factors is not enumerable, -1 if the size would not be representable
	 * as an integer.
	 */
	protected int size;

	protected String openingBracket = "[";
	protected String closingBracket = "]";
	protected String separatorToken = ",";
	
	public int getSize()
	{
		if( size > 0 )
			return size;
		
		throw new UnsupportedOperationException( size == 0 ?
				"One of the factor algebras is not enumerable" :
				"The size of the algebra cannot be represented in an Integer");
	}

	private void calculateSizes()
	{
		universeSizes = new int[factors.length];
		try
		{
			long s = 1;
			for(int i = 0; i < factors.length; ++i)
			{
				Set universe = factors[i];
				universeSizes[i] = universe.getSize();

				s *= universeSizes[i];
				if( s > Integer.MAX_VALUE )
				{
					size = -1;
					break;
				}
			}
			size = (int)s;
		}
		catch(UnsupportedOperationException e)
		{
			size = 0;
		}
	}
	
	private void calculateStyle(String style)
	{
		openingBracket = "";
		separatorToken = "";
		closingBracket = "";
		
		if( style.length() == 3 )
		{
			openingBracket = style.substring(0, 1);
			separatorToken = style.substring(1, 2);
			closingBracket = style.substring(2, 3);
		}
		else if( style.length() == 2 )
		{
			openingBracket = style.substring(0, 1);
			closingBracket = style.substring(1, 2);
		}
		else if( style.length() == 1 )
			separatorToken = style.substring(0, 1);
		else if( style.length() == 0 )
			;
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * Creates a product universe of a non-empty list of universes.
	 * 
	 * @param universes The list of universes.
	 */
	public ProductSet(List<? extends Set> universes)
	{
		this.factors = new Set[universes.size()];
		universes.toArray(this.factors);

		calculateSizes();
	}

	/**
	 * Creates a power of the given set.
	 * 
	 * @param universe the underlying set
	 * @param power the exponent
	 */
	public ProductSet(Set universe, int power)
	{
		if(power <= 0 )
			throw new IllegalArgumentException("the exponent must be positive");
		
		factors = new Set[power];
		for(int i = 0; i < power; ++i)
			factors[i] = universe;
	
		calculateSizes();
	}
	
	/**
	 * Creates a power of the given set.
	 * 
	 * @param universe the underlying set
	 * @param power the exponent
	 * @param style the style of the string representation
	 */
	public ProductSet(Set universe, int power, String style)
	{
		if(power <= 0 )
			throw new IllegalArgumentException("the exponent must be positive");
		
		factors = new Set[power];
		for(int i = 0; i < power; ++i)
			factors[i] = universe;
	
		calculateSizes();
		calculateStyle(style);
	}
	
	/**
	 * Calculates the index of an element. 
	 *
	 * @param coords The indices of the coordinates of an element.
	 * The length of this array must be equal to the number of factors
	 * of this product and each coordinate must be of the proper size.
	 * @return The index of the element identified by the coordinates, or
	 * <code>-1</code> if the object is <code>null</code>.
	 */
	public int getIndex(Object[] vector)
	{
		if( size <= 0 )
			throw new UnsupportedOperationException();
		
		if( vector == null )
			return -1;
		
		int index = 0;

		for(int i = 0; i < universeSizes.length; ++i)
		{
			index *= universeSizes[i];
			index += factors[i].getIndex(vector[i]);
		}

		return index;
	}

	public int getIndex(Object element)
	{
		return getIndex((Object[])element);
	}

	public Object getElement(int index)
	{
		if( size <= 0 )
			throw new UnsupportedOperationException();
		
		if( index < 0 )
			return null;
		
		Object[] vector = new Object[factors.length];
		
		int i = factors.length;
		while( --i >= 0 )
		{
			vector[i] = factors[i].getElement(index % universeSizes[i]);
			index /= universeSizes[i];
		}

		return vector;
	}

	public boolean areEquals(Object[] vector1, Object[] vector2)
	{
		if( vector1.length != factors.length || vector2.length != factors.length )
			throw new IllegalArgumentException("invaid size of arrays");
		
		int i = factors.length;
		while( --i >= 0 )
			if( ! factors[i].areEquals(vector1[i], vector2[i]) )
				return false;
		
		return true;
	}

	public boolean areEquals(Object elem1, Object elem2)
	{
		return areEquals((Object[])elem1, (Object[])elem2);
	}

	/**
	 * Calculates the hash code from the list of objects.
	 * This method is based on the code of Daniel Phillips 
	 * <phillips@innominate.de>
	 */
	public int hashCode(Object[] vector)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = vector.length;
		while( --i >= 0 )
		{
			int hash = hash1 + (hash0 ^ (factors[i].hashCode(vector[i]) * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}

	public int hashCode(Object elem)
	{
		return hashCode((Object[])elem);
	}

	public String toString(Object[] vector)
	{
		String s = openingBracket;

		for(int i = 0; i < vector.length; ++i)
		{
			if( i > 0 )
				s += separatorToken;
			s += factors[i].getIndex(vector[i]);
		}

		s += closingBracket;
		return s;
	}

	public String toString(Object elem)
	{
		return toString((Object[])elem);
	}
	
	public Object parse(String string)
	{
		Parser parser = new Parser();

		String[] substrings = parser.parseList(
			parser.parseEnclosingTokens(string.trim(), openingBracket, closingBracket), separatorToken);
		
		if( substrings == null || substrings.length != factors.length )
			return null;
		
		Object[] vector = new Object[substrings.length];
		for(int i = 0; i < vector.length; ++i)
			if( (vector[i] = factors[i].parse(substrings[i])) == null )
				return null;
		
		return vector;
	}
}
