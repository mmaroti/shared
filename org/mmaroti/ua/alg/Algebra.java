/**
 *	Copyright (C) Miklos Maroti, 2002-2006
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

package org.mmaroti.ua.alg;

import java.util.Iterator;
import java.util.List;

/**
 * The principal interface to manipulate finite or infinite algebraic
 * structures. The elements of a set are optionally enumerated (if that is
 * possible and reasonably efficient). In this case the elements are indexed
 * from <code>0</code> to <code>getSize()-1</code>. Two elements of the same
 * universe are equal if they have the same index, or equal as determined by the
 * {@link #areEquals} method. If the set is not enumerated then some methods of
 * this interface will throw an {@link UnsupportedOperationException}. The
 * elements have a well defined internal structure that depends on the
 * particular class implementing this interface.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public abstract class Algebra {
	/**
	 * Returns the size of the underlying set. For the empty set this method
	 * returns <code>0</code>. For algebras it always returns a positive number.
	 * 
	 * @return the size of the set
	 * @throws UnsupportedOperationException
	 *             if the set is infinite, too large, or for some other reason
	 *             cannot be enumerated
	 */
	public abstract int getSize();

	/**
	 * Returns the index of the element if the elements are enumerated. Equal
	 * elements have the same index.
	 * 
	 * @return the index of the element which is an integer between
	 *         <code>0</code> and <code>size()-1</code>
	 * @throws UnsupportedOperationException
	 *             if the set is infinite, too large, or for some other reason
	 *             cannot be enumerated
	 * @throws IllegalArgumentException
	 *             if <code>element</code> is not a valid element
	 */
	public abstract int getIndex(Object element);

	/**
	 * Transforms an index to an element.
	 * 
	 * @param index
	 *            the index of an element. It must be in the range from
	 *            <code>0</code> to <code>getSize()-1</code>
	 * @return the element whose index is <code>index</code>
	 * @throws UnsupportedOperationException
	 *             if the set is infinite, too large, or for some other reason
	 *             cannot be enumerated
	 * @throws IllegalArgumentException
	 *             if <code>index</code> is not a valid index
	 */
	public abstract Object getElement(int index);

	/**
	 * Returns if the two object represents the same element. Sometimes, the
	 * universe is the set of equivalence classes of an equivalence relation,
	 * and the elements are representative elements of the classes.
	 * 
	 * @param elem1
	 *            the first element
	 * @param elem2
	 *            the second element
	 * @return <code>true</code> if the two elements are equal,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean areEquals(Object elem1, Object elem2);

	/**
	 * Returns a hash code for the given element. Equal elements must return the
	 * same hash code.
	 * 
	 * @param element
	 *            the element
	 * @return the hash code of the element
	 */
	public abstract int hashCode(Object element);

	/**
	 * Formats the element as a string to be displayed on screen.
	 * 
	 * @param element
	 *            the element to be formatted
	 * @return the string representation of the element
	 */
	public abstract String toString(Object element);

	/**
	 * Converts the string representation of an element into an element of the
	 * set. Leading and trailing spaces are always disregarded.
	 * 
	 * @param string
	 *            the string representation of an element object
	 * @return the element object if the string could be parsed, or
	 *         <code>null</code> if the string could not be fully parsed.
	 */
	public abstract Object parse(String string);

	/**
	 * Lists all elements and their indices of this set.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the set is infinite, too large, or for some other reason
	 *             cannot be enumerated
	 */
	public void dumpElements() {
		int size = getSize();

		for (int i = 0; i < size; ++i)
			System.out.println(i + ": " + toString(getElement(i)));
	}

	/**
	 * Returns the list of operations of the algebra.
	 */
	public abstract Operation[] getOperations();

	/**
	 * Returns the list of relations of the algebra.
	 */
	public abstract Relation[] getRelations();

	/**
	 * Returns <code>true</code> if this algebra is compatible with the other
	 * algebra, i.e. have the same number of operations and relations with the
	 * same arities.
	 */
	public boolean isCompatible(Algebra other) {
		Operation[] as = getOperations();
		Operation[] bs = other.getOperations();

		if (as.length != bs.length)
			return false;

		for (int i = 0; i < as.length; ++i)
			if (!as[i].isCompatible(bs[i]))
				return false;

		Relation[] cs = getRelations();
		Relation[] ds = other.getRelations();

		if (cs.length != ds.length)
			return false;

		for (int i = 0; i < cs.length; ++i)
			if (!cs[i].isCompatible(ds[i]))
				return false;

		return true;
	}

	/**
	 * Returns <code>true</code> if the algebras in the list are pairwise
	 * compatible, <code>false</code> otherwise.
	 */
	public static boolean areCompatible(List<Algebra> algebras) {
		if (algebras.size() <= 1)
			return true;

		Iterator<Algebra> iter = algebras.iterator();
		Algebra a = iter.next();

		while (iter.hasNext())
			if (!a.isCompatible(iter.next()))
				return false;

		return true;
	}
}
