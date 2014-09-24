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

import org.mmaroti.ua.alg.Symbol;

/**
 * This is the general interface for rings. The additive structure of a ring is
 * an Abelian group, the multiplicative structure is a semigroup or monoid, and
 * multiplication distributes over addition.
 */
public interface Ring {
	public static Symbol PLUS = new Symbol("+", 2, 0, Symbol.INFIX
			| Symbol.LEFT_ASSOCIATIVE);

	/**
	 * Returns the sum of two elements.
	 */
	public abstract Object sum(Object a, Object b);

	/**
	 * Returns the sum of two elements.
	 */
	public abstract int sum(int a, int b);

	public static Symbol NEG = new Symbol("-", 1, 1, Symbol.INFIX);

	/**
	 * Returns the additive inverse of an element.
	 */
	public abstract Object negative(Object a);

	/**
	 * Returns the additive inverse of an element.
	 */
	public abstract int negative(int a);

	public static Symbol ZERO = new Symbol("0", 0, 0, 0);

	/**
	 * Returns the unit element of the field as an object.
	 */
	public abstract Object zeroElement();

	/**
	 * Returns the unit element of the field as an object.
	 */
	public abstract int zero();

	public static Symbol PROD = new Symbol("*", 2, 10, Symbol.INFIX
			| Symbol.LEFT_ASSOCIATIVE);

	/**
	 * Returns the product of two elements.
	 */
	public abstract Object product(Object a, Object b);

	/**
	 * Returns the product of two elements.
	 */
	public abstract int product(int a, int b);

	public static Symbol UNIT = new Symbol("1", 0, 0, Symbol.INFIX
			| Symbol.LEFT_ASSOCIATIVE);

	/**
	 * Returns the multiplicative unit element if there is one.
	 */
	public abstract Object unitElement();

	/**
	 * Returns the multiplicative unit element if there is one.
	 */
	public abstract int unit();
}
