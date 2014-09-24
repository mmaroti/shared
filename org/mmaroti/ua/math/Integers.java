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

import org.mmaroti.ua.alg.*;

import java.math.BigInteger;

/**
 * This class represents the ring of integers.
 */
public class Integers extends Algebra implements Ring {
	/**
	 * This method always throws an exception, because the integers cannot be
	 * enumerated.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public int getSize() {
		throw new UnsupportedOperationException(
				"the integers are not enumerable");
	}

	/**
	 * This method always throws an {@link UnsupportedOperationException}
	 * exception.
	 * 
	 * @see #getSize()
	 */
	public int getIndex(Object element) {
		throw new UnsupportedOperationException(
				"the integers are not enumerable");
	}

	/**
	 * This method always throws an {@link UnsupportedOperationException}
	 * exception, because the elements of term algebras are not enumerated.
	 * 
	 * @see #getSize()
	 */
	public Object getElement(int index) {
		throw new UnsupportedOperationException(
				"the integers are not enumerable");
	}

	public boolean areEquals(Object a, Object b) {
		return a.equals(b);
	}

	public int hashCode(Object element) {
		return element.hashCode();
	}

	public String toString(Object element) {
		return element.toString();
	}

	public Integers() {
	}

	public int sum(int a, int b) {
		return getIndex(sum(getElement(a), getElement(b)));
	}

	public int negative(int a) {
		return getIndex(negative(getElement(a)));
	}

	public int zero() {
		return getIndex(zeroElement());
	}

	public int product(int a, int b) {
		return getIndex(product(getElement(a), getElement(b)));
	}

	public int unit() {
		return getIndex(unitElement());
	}

	public Object sum(Object a, Object b) {
		return ((BigInteger) a).add((BigInteger) b);
	}

	public Object negative(Object a) {
		return ((BigInteger) a).negate();
	}

	public Object zeroElement() {
		return BigInteger.ZERO;
	}

	public Object product(Object a, Object b) {
		return ((BigInteger) a).multiply((BigInteger) b);
	}

	public Object unitElement() {
		return BigInteger.ONE;
	}

	public Object parse(String string) {
		string = string.trim();
		BigInteger a = new BigInteger(string);

		if (a.toString().equals(string))
			return a;
		else
			throw new IllegalArgumentException("this is not a valid integer");
	}

	Operation[] operations = new Operation[] { new Operation() {
		public Symbol getSymbol() {
			return PLUS;
		}

		public int getSize() {
			return getSize();
		}

		public int getValue(int[] args) {
			assert (args.length == 2);
			return sum(args[0], args[1]);
		}

		public Object getValue(Object[] args) {
			assert (args.length == 2);
			return sum(args[0], args[1]);
		}
	}, new Operation() {
		public Symbol getSymbol() {
			return NEG;
		}

		public int getSize() {
			return getSize();
		}

		public int getValue(int[] args) {
			assert (args.length == 1);
			return negative(args[0]);
		}

		public Object getValue(Object[] args) {
			assert (args.length == 1);
			return negative(args[0]);
		}
	}, new Operation() {
		public Symbol getSymbol() {
			return ZERO;
		}

		public int getSize() {
			return getSize();
		}

		public int getValue(int[] args) {
			assert (args.length == 0);
			return zero();
		}

		public Object getValue(Object[] args) {
			assert (args.length == 0);
			return zeroElement();
		}
	}, new Operation() {
		public Symbol getSymbol() {
			return PROD;
		}

		public int getSize() {
			return getSize();
		}

		public int getValue(int[] args) {
			assert (args.length == 2);
			return product(args[0], args[1]);
		}

		public Object getValue(Object[] args) {
			assert (args.length == 2);
			return product(args[0], args[1]);
		}
	}, new Operation() {
		public Symbol getSymbol() {
			return UNIT;
		}

		public int getSize() {
			return getSize();
		}

		public int getValue(int[] args) {
			assert (args.length == 0);
			return unit();
		}

		public Object getValue(Object[] args) {
			assert (args.length == 0);
			return unitElement();
		}
	} };

	public Operation[] getOperations() {
		return operations;
	}

	Relation[] relations = new Relation[] {};

	public Relation[] getRelations() {
		return relations;
	}
}
