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

package org.mmaroti.ua.alg;

import java.util.*;
import org.mmaroti.ua.util.*;

/**
 * This class can calculate the product of finitely many algebras. This class
 * can work for both enumerable and non-enumerable algebras. The elements of the
 * product are enumerated (whenever possible) such that
 * 
 * <pre>
 *      0 = &lt;0,...,0&gt;
 *      1 = &lt;0,...,1&gt;
 *       ...
 * size-1 = &lt;size_0-1,...,size_{k-1}-1&gt;
 * </pre>
 * 
 * where <code>size = size_0 * ... * size_{k-1}</code>. The elements are labeled
 * by arrays of labels of the factor algebras. The labels are canonicalized to
 * save memory and to be able to compare them by the == operator.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class ProductAlgebra extends Algebra {
	/**
	 * Holds the factor algebras of the product.
	 */
	protected Algebra[] factors;

	/**
	 * @return the list of factor universes that was originally passed to
	 *         {@link ProductUniverse#ProductSet}. The returned list should not
	 *         be modified.
	 */
	public Algebra[] getFactors() {
		return factors;
	}

	/**
	 * Holds the sizes of the factor algebras if the product is enumerable.
	 */
	protected int[] factorSizes;

	/**
	 * Holds the size of the product set. This value is 0 if one of the factors
	 * is not enumerable, -1 if the size would not be representable as an
	 * integer.
	 */
	protected int size;

	public int getSize() {
		if (size > 0)
			return size;

		throw new UnsupportedOperationException(
				size == 0 ? "One of the factor algebras is not enumerable"
						: "The size of the algebra cannot be represented in an Integer");
	}

	private void calculateSizes() {
		factorSizes = new int[factors.length];
		try {
			long s = 1;
			for (int i = 0; i < factors.length; ++i) {
				Algebra factor = factors[i];
				factorSizes[i] = factor.getSize();

				s *= factorSizes[i];
				if (s > Integer.MAX_VALUE) {
					size = -1;
					break;
				}
			}
			size = (int) s;
		} catch (UnsupportedOperationException e) {
			size = 0;
		}
	}

	/**
	 * Creates a product algebra of non-empty list of algebras.
	 * 
	 * @param algebras
	 *            The list of similar algebras.
	 * @throws IllegalArgumentException
	 *             if the list is empty or the algebras are not similar.
	 */
	public ProductAlgebra(List<Algebra> algebras) {
		if (!Algebra.areCompatible(algebras))
			throw new IllegalArgumentException("the provided list of algebras "
					+ "are not of the same signature");

		factors = new Algebra[algebras.size()];
		algebras.toArray(factors);

		calculateSizes();

		if (algebras.size() <= 0)
			throw new IllegalArgumentException(
					"At least one algebra must be specified");

		int ops = this.factors[0].getOperations().length;

		indexTuple = new int[this.factors.length];

		operations = new Op[ops];
		for (int i = 0; i < ops; ++i)
			operations[i] = new Op(i);
	}

	/**
	 * Creates a direct power of the given algebra.
	 * 
	 * @param algebra
	 *            the base algebra
	 * @param power
	 *            the exponent
	 */
	public ProductAlgebra(Algebra algebra, int power) {
		if (power <= 0)
			throw new IllegalArgumentException("the exponent must be positive");

		factors = new Algebra[power];
		for (int i = 0; i < power; ++i)
			factors[i] = algebra;

		calculateSizes();

		int ops = this.factors[0].getOperations().length;

		indexTuple = new int[this.factors.length];

		operations = new Op[ops];
		for (int i = 0; i < ops; ++i)
			operations[i] = new Op(i);
	}

	/**
	 * Calculates the index of an element.
	 * 
	 * @param coords
	 *            The indices of the coordinates of an element. The length of
	 *            this array must be equal to the number of factors of this
	 *            product and each coordinate must be of the proper size.
	 * @return The index of the element identified by the coordinates, or
	 *         <code>-1</code> if the object is <code>null</code>.
	 */
	public int getIndex(Object[] vector) {
		if (size <= 0)
			throw new UnsupportedOperationException();

		if (vector == null)
			return -1;

		int index = 0;

		for (int i = 0; i < factorSizes.length; ++i) {
			index *= factorSizes[i];
			index += factors[i].getIndex(vector[i]);
		}

		return index;
	}

	public int getIndex(Object element) {
		return getIndex((Object[]) element);
	}

	public Object getElement(int index) {
		if (size <= 0)
			throw new UnsupportedOperationException();

		if (index < 0)
			return null;

		Object[] vector = new Object[factors.length];

		int i = factors.length;
		while (--i >= 0) {
			vector[i] = factors[i].getElement(index % factorSizes[i]);
			index /= factorSizes[i];
		}

		return vector;
	}

	public boolean areEquals(Object[] vector1, Object[] vector2) {
		if (vector1.length != factors.length
				|| vector2.length != factors.length)
			throw new IllegalArgumentException("invaid size of arrays");

		int i = factors.length;
		while (--i >= 0)
			if (!factors[i].areEquals(vector1[i], vector2[i]))
				return false;

		return true;
	}

	public boolean areEquals(Object elem1, Object elem2) {
		return areEquals((Object[]) elem1, (Object[]) elem2);
	}

	/**
	 * Calculates the hash code from the list of objects. This method is based
	 * on the code of Daniel Phillips <phillips@innominate.de>
	 */
	public int hashCode(Object[] vector) {
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = vector.length;
		while (--i >= 0) {
			int hash = hash1
					+ (hash0 ^ (factors[i].hashCode(vector[i]) * 71523));
			if (hash < 0)
				hash -= 0x7fffffff;

			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}

	public int hashCode(Object elem) {
		return hashCode((Object[]) elem);
	}

	public String toString(Object[] vector) {
		String s = "(";

		for (int i = 0; i < vector.length; ++i) {
			if (i > 0)
				s += ",";
			s += factors[i].getIndex(vector[i]);
		}

		s += ")";
		return s;
	}

	public String toString(Object elem) {
		return toString((Object[]) elem);
	}

	public Object parse(String string) {
		Parser parser = new Parser();

		String[] substrings = parser.parseList(
				parser.parseEnclosingTokens(string.trim(), "(", ")"), ",");

		if (substrings == null || substrings.length != factors.length)
			return null;

		Object[] vector = new Object[substrings.length];
		for (int i = 0; i < vector.length; ++i)
			if ((vector[i] = factors[i].parse(substrings[i])) == null)
				return null;

		return vector;
	}

	protected int[] indexTuple;
	protected Op[] operations;

	public Operation[] getOperations() {
		return operations;
	}

	/**
	 * This class implements the product operation, which is calculated
	 * coordinate-wise. If one of the coordinates is undefined (<code>-1</code>
	 * or <code>null</code>) then the result is undefined.
	 */
	public class Op extends Operation {
		protected Operation[] operations;
		protected Symbol symbol;

		public Symbol getSymbol() {
			return symbol;
		}

		/**
		 * Constructs a product operation. The name of the operation will be the
		 * name of the first operation.
		 * 
		 * @throws IllegalArgumentException
		 *             if the list is empty or the arities of the operations are
		 *             not the same.
		 */
		protected Op(int opIndex) {
			operations = new Operation[factors.length];
			for (int i = 0; i < factors.length; ++i)
				operations[i] = factors[i].getOperations()[opIndex];

			symbol = operations[0].getSymbol();

			arg1 = new int[symbol.arity];
			arg2 = new int[symbol.arity];
			arg3 = new Object[symbol.arity];
		}

		/**
		 * Constructs a product operation of an empty product
		 * 
		 * @param name
		 *            The name of the operation
		 * @param arity
		 *            The arity of the operation
		 * 
		 * @throws IllegalArgumentException
		 *             if the arity is negative.
		 */
		protected Op(Symbol symbol) {
			this.symbol = symbol;
			operations = new Operation[0];

			// we will never use the other arrays
			arg1 = new int[symbol.arity];
		}

		protected int[] arg1;
		protected int[] arg2;

		public int getValue(int[] args) {
			if (getSize() <= 0)
				throw new UnsupportedOperationException(
						"The elements of the underlying set cannot be enumerated");

			for (int i = 0; i < symbol.arity; ++i) {
				// if undefined value somewhere
				if ((arg1[i] = args[i]) < 0)
					return -1;
			}

			int i = operations.length;
			while (--i >= 0) {
				int s = factors[i].getSize();
				for (int j = 0; j < symbol.arity; ++j) {
					arg2[j] = arg1[j] % s;
					arg1[j] /= s;
				}
				indexTuple[i] = operations[i].getValue(arg2);
			}

			return getIndex(indexTuple);
		}

		protected Object[] arg3;

		public Object getValue(Object[] args) {
			Object[] vector = new Object[operations.length];

			for (int i = 0; i < operations.length; ++i) {
				for (int j = 0; j < symbol.arity; ++j)
					arg3[j] = ((Object[]) args[j])[i];

				// if undefined somewhere
				if ((vector[i] = operations[i].getValue(arg3)) == null)
					return null;
			}

			return vector;
		}

		public int getSize() {
			return size;
		}
	}

	@Override
	public Relation[] getRelations() {
		// TODO Auto-generated method stub
		return null;
	}
}
