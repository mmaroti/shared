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

import java.util.*;

/**
 * The SmallFamily is a family of subsets of <code>{0,1,2,3,4}</code>. 
 * The family is stored in a single integer, each bit corresponds 
 * to a subset of <code>{0,1,2,3,4}</code>.
 */
final class SmallFamily implements Comparable<SmallFamily>, Cloneable
{
	protected final int BITS = 32;
	public int subsets;

	/**
	 * Constructs the empty family.
	 */
	public SmallFamily()
	{
		subsets = 0;
	}

	public SmallFamily(int subsets)
	{
		this.subsets = subsets;
	}

	public SmallFamily(SmallFamily f)
	{
		subsets = f.subsets;
	}

	public int compareTo(SmallFamily o)
	{
		return subsets - o.subsets;
	}

	public boolean equals(Object o)
	{
		return subsets == ((SmallFamily)o).subsets;
	}

	public int hashCode()
	{
		return subsets;
	}

	public String toString()
	{
		String s = new String();

		for(int i = 0; i < BITS; ++i)
			if( (subsets & (1<<i)) != 0 )
				s += "," + new Set(i);

		return "{" + (s.length() == 0 ? "" : s.substring(1)) + "}";
	}

	public void close()
	{
		if( subsets >= 0 )
		{
			for(int i = 0, a = 1; a <= subsets; ++i, a <<= 1)
				if( (subsets & a) != 0 )
					for(int j = 0, b = 1; j < i; ++j, b <<= 1)
						if( (subsets & b) != 0 )
							subsets |= 1 << (i|j);
		}
		else
		{
			for(int i = 0, a = 1; a != 0; ++i, a <<= 1)
				if( (subsets & a) != 0 )
					for(int j = 0, b = 1; j < i; ++j, b <<= 1)
						if( (subsets & b) != 0 )
							subsets |= 1 << (i|j);
		}
	}

	public boolean isClosed()
	{
		if( subsets >= 0 )
		{
			for(int i = 0, a = 1; a <= subsets; ++i, a <<= 1)
				if( (subsets & a) != 0 )
					for(int j = 0, b = 1; j < i; ++j, b <<= 1)
						if( (subsets & b) != 0 )
							if( (subsets | 1 << (i|j)) != subsets )
								return false;
		}
		else
		{
			for(int i = 0, a = 1; a != 0; ++i, a <<= 1)
				if( (subsets & a) != 0 )
					for(int j = 0, b = 1; j < i; ++j, b <<= 1)
						if( (subsets & b) != 0 )
							if( (subsets | 1 << (i|j)) != subsets )
								return false;
		}

		return true;
	}

	public void union(SmallFamily s)
	{
		subsets |= s.subsets;
	}

	public static SmallFamily union(SmallFamily a, SmallFamily b)
	{
		return new SmallFamily(a.subsets | b.subsets);
	}

	public void intersection(SmallFamily s)
	{
		subsets &= s.subsets;
	}

	public static SmallFamily intersection(SmallFamily a, SmallFamily b)
	{
		return new SmallFamily(a.subsets & b.subsets);
	}

	public void difference(SmallFamily s)
	{
		subsets &= ~s.subsets;
	}

	public static SmallFamily difference(SmallFamily a, SmallFamily b)
	{
		return new SmallFamily(a.subsets & (~b.subsets));
	}

	public boolean isSubsetOf(SmallFamily s)
	{
		return (subsets & s.subsets) == subsets;
	}

	/**
	 * We assume that the two families are both union-closed.
	 * The product will be union closed.
	 */
	public void product(SmallFamily f)
	{
		subsets = product(this, f).subsets;
	}

	/**
	 * We assume that the two families are both union-closed.
	 * The product will be union closed.
	 */
	public static SmallFamily product(SmallFamily f, SmallFamily g)
	{
		int subsets = 0;

		if( f.subsets >= 0 && g.subsets >= 0 )
		{
			for(int i = 0, a = 1; a <= f.subsets; ++i, a <<= 1)
				if( (f.subsets & a) != 0 )
					for(int j = 0, b = 1; b <= g.subsets; ++j, b <<= 1)
						if( (g.subsets & b) != 0 )
							subsets |= 1 << (i|j);
		}
		else
		{
			for(int i = 0, a = 1; a != 0; ++i, a <<= 1)
				if( (f.subsets & a) != 0 )
					for(int j = 0, b = 1; b != 0; ++j, b <<= 1)
						if( (g.subsets & b) != 0 )
							subsets |= 1 << (i|j);
		}

		return new SmallFamily(subsets);
	}

	/**
	 * We assume that the two families are both union-closed.
	 * The join will be union closed.
	 */
	public void join(SmallFamily f)
	{
		int a = subsets | f.subsets;
		if( a != subsets )
		{
			subsets = a;
			if( a != f.subsets )
				close();
		}
	}

	/**
	 * We assume that the two families are both union-closed.
	 * The join will be union closed.
	 */
	public static SmallFamily join(SmallFamily f, SmallFamily g)
	{
		SmallFamily h = union(f,g);

		if( h.subsets != f.subsets && h.subsets != g.subsets )
			h.close();

		return h;
	}

	/**
	 * Returns the number of sets in this family.
	 */
	public int size()
	{
		int c = 0;

		for(int a = 1; a != 0; a <<= 1)
			if( (subsets & a) != 0 )
				++c;

		return c;
	}

	public boolean isEmpty()
	{
		return subsets == 0;
	}

	/**
	 * Adds this set to the family, if it is not already there.
	 */
	public void add(Set s)
	{
		subsets |= 1 << s.elems;
	}

	/**
	 * Removes this set from the family, if it is there.
	 */
	public void remove(Set s)
	{
		subsets &= ~(1 << s.elems);
	}

	public boolean has(Set s)
	{
		return (subsets & (1 << s.elems)) != 0;
	}

	/**
	 * Returns the number of sets in this family that contains
	 * this element.
	 */
	public int occurences(int element)
	{
		element = 1 << element;
		int c = 0;

		for(int i = 0, a = 1; a != 0; ++i, a <<= 1)
			if( (i & element) != 0 && (subsets & a) != 0 )
				++c;

		return c;
	}

	public static void main(String[] _)
	{
		List<SmallFamily> families = new ArrayList<SmallFamily>();

		for(int i = 0; i < 256; ++i)
		{
			SmallFamily f = new SmallFamily(i);
			if( f.isClosed() && f.size() == 5 )
				families.add(f);
		}

		for(int i = 0; i < families.size(); ++i)
		{
			int a0 = ((SmallFamily)families.get(i)).occurences(0);
			int a1 = ((SmallFamily)families.get(i)).occurences(1);
			int a2 = ((SmallFamily)families.get(i)).occurences(2);

			for(int j = 0; j <= i; ++j)
			{
				int b0 = ((SmallFamily)families.get(j)).occurences(0);
				int b1 = ((SmallFamily)families.get(j)).occurences(1);
				int b2 = ((SmallFamily)families.get(j)).occurences(2);

				for(int k = 0; k <= j; ++k)
				{
					int c0 = ((SmallFamily)families.get(k)).occurences(0);
					int c1 = ((SmallFamily)families.get(k)).occurences(1);
					int c2 = ((SmallFamily)families.get(k)).occurences(2);

					if( (a0 <= 2 || b0 <= 2 || c0 <= 2)
						&& (a1 <= 2 || b1 <= 2 || c1 <= 2)
						&& (a2 <= 2 || b2 <= 2 || c2 <= 2) )
					{
						System.out.println("(" + a0 + "," + a1 + "," + a2 + ") " + families.get(i));
						System.out.println("(" + b0 + "," + b1 + "," + b2 + ") " + families.get(j));
						System.out.println("(" + c0 + "," + c1 + "," + c2 + ") " + families.get(k));
						System.out.println();
					}
				}
			}
		}
	}
}
