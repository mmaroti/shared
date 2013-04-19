/**
 *	Copyright (C) Miklos Maroti, 2003
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

package org.mmaroti.math.frankl;

/**
 * The Set class holds a subset of the set <code>{0,1,...,31}</code>.
 * The set is stored in a single integer, each bit represeting one
 * element.
 */
final class Set implements Comparable<Set>, Cloneable
{
	protected final int BITS = 32;
	public int elems;

	/**
	 * Constructs an empty set.
	 */
	public Set()
	{
		elems = 0;
	}

	public Set(int elems)
	{
		this.elems = elems;
	}

	public int compareTo(Set o)
	{
		return elems - o.elems;
	}

	public boolean equals(Object o)
	{
		return elems == ((Set)o).elems;
	}

	public int hashCode()
	{
		return elems;
	}

	public String toString()
	{
		String s = new String();

		for(int i = 0; i < BITS; ++i)
			if( (elems & (1<<i)) != 0 )
				s += "," + i;

		return "{" + (s.length() == 0 ? "" : s.substring(1)) + "}";
	}

	public void union(Set s)
	{
		elems |= s.elems;
	}

	public static Set union(Set a, Set b)
	{
		return new Set(a.elems | b.elems);
	}

	public void intersection(Set s)
	{
		elems &= s.elems;
	}

	public static Set intersection(Set a, Set b)
	{
		return new Set(a.elems & b.elems);
	}

	public void difference(Set s)
	{
		elems &= ~s.elems;
	}

	public static Set difference(Set a, Set b)
	{
		return new Set(a.elems & (~b.elems));
	}

	public boolean isSubsetOf(Set s)
	{
		return (elems & s.elems) == elems;
	}

	/**
	 * Returns the number of elements of this set.
	 */
	public int size()
	{
		int s = 0;

		for(int i = 1; i != 0; i <<= 1)
			if( (elems & i) != 0 )
				++s;

		return s;
	}

	public boolean isEmpty()
	{
		return elems == 0;
	}

	/**
	 * Adds the element to this set, if it is not already there.
	 */
	public void add(int element)
	{
		elems |= 1 << element;
	}

	/**
	 * Removes this element from the set, if it is there.
	 */
	public void remove(int element)
	{
		elems &= ~(1 << element);
	}

	public boolean has(int element)
	{
		return (elems & (1 << element)) != 0;
	}
}
