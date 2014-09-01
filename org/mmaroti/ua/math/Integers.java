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
import java.math.BigInteger;

/**
 * This class represents the ring of integers.
 */
public class Integers extends Universe implements Ring
{
	private SubUniverse integers = new SubUniverse(Objects.INSTANCE);

	public int getIndex(Object a)
	{
		return integers.getIndex(integers.add(a));
	}

	public Object getElement(int a)
	{
		if( a < 0 || a >= integers.getSize() )
			throw new IllegalArgumentException("this index was never assigned to an" +				" integer from this class");
		
		return integers.getElement(a);
	}
	
	public int getSize()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs the field of reals.
	 */
	public Integers()
	{
	}

	public int sum(int a, int b)
	{
		return getIndex(sum(getElement(a), getElement(b)));
	}

	public int negative(int a)
	{
		return getIndex(negative(getElement(a)));
	}

	public int zero()
	{
		return getIndex(zeroElement());
	}

	public int product(int a, int b)
	{
		return getIndex(product(getElement(a), getElement(b)));
	}

	public int unit()
	{
		return getIndex(unitElement());
	}

	public Object sum(Object a, Object b)
	{
		return ((BigInteger)a).add((BigInteger)b);
	}

	public Object negative(Object a)
	{
		return ((BigInteger)a).negate();
	}

	public Object zeroElement()
	{
		return BigInteger.ZERO;
	}

	public Object product(Object a, Object b)
	{
		return ((BigInteger)a).multiply((BigInteger)b);
	}

	public Object unitElement()
	{
		return BigInteger.ONE;
	}

	public boolean areEquals(Object elem1, Object elem2)
	{
		return elem1.equals(elem2);
	}

	public int hashCode(Object element)
	{
		return element.hashCode();
	}

	public String toString(Object element)
	{
		return element.toString();
	}

	public Object parse(String string)
	{
		string = string.trim();
	
		long d = Long.parseLong(string);
		return BigInteger.valueOf(d);
	}
}
