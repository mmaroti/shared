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
 * This class approximates the field of reals. Note, that
 * this class does not satisfy the axioms of reals, only
 * approximate values are returned.
 */
public class Reals extends Universe implements Field
{
	private SubUniverse reals = new SubUniverse(Objects.INSTANCE);
	
	public int getIndex(Object a)
	{
		return reals.getIndex(reals.add(a));
	}
	
	public Object getElement(int a)
	{
		if( a < 0 || a >= reals.getSize() )
			throw new IllegalArgumentException("this index was never assigned to a" +				" real number from this class");
		
		return reals.getElement(a);
	}
	
	public int getSize()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs the field of reals.
	 */
	public Reals()
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

	public int inverse(int a)
	{
		return getIndex(inverse(getElement(a)));
	}

	public int unit()
	{
		return getIndex(unitElement());
	}

	public int character()
	{
		return 0;
	}

	public Object sum(Object a, Object b)
	{
		return new Double(((Double)a).doubleValue() + ((Double)b).doubleValue());
	}

	public Object negative(Object a)
	{
		return new Double(-((Double)a).doubleValue());
	}

	public Object zeroElement()
	{
		return new Double(0.0);
	}

	public Object product(Object a, Object b)
	{
		return new Double(((Double)a).doubleValue() * ((Double)b).doubleValue());
	}

	public Object inverse(Object a)
	{
		return new Double(1.0/((Double)a).doubleValue());
	}

	public Object unitElement()
	{
		return new Double(1.0);
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
		
		double d = Double.parseDouble(string);
		if( Double.toString(d).equals(string) )
			return new Double(d);
		
		return null;
	}
}
