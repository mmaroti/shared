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
 * This class represents the ring of residue classes module
 * a fixed positive integer.
 */
public class RingZn extends FiniteSet implements Ring
{
	/**
	 * Constructs the ring of residue classes modulo a positive integer.
	 * This ring is always finite, the elements are numbered from 
	 * <code>0</code> till <code>size-1</code>.
	 */
	public RingZn(int size)
	{
		super(size);
		
		if( size <= 0 )
			throw new IllegalArgumentException();
	}

	public int sum(int a, int b)
	{
		return (a+b) % size;
	}

	public int negative(int a)
	{
		return a == 0 ? 0 : size - a;
	}

	public int zero()
	{
		return 0;
	}

	public int product(int a, int b)
	{
		return (a*b) % size;
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
}
