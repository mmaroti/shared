/**
 *	Copyright (C) Miklos Maroti, 2002-2008
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

import org.mmaroti.ua.set.Universe;

/**
 * Represents an operation of an {@link Algebra}. If the elements of the
 * {@link Algebra} are enumerated, then this is an <code>n</code>-ary
 * mapping of the set <code>[0,size-1]</code> of integers into the set of
 * integers. If the elements of the {@link Algebra} are labelled, then this
 * is an <code>n</code>-ary mapping of the set of labels into the set of
 * labels (or <code>null</code>).
 * 
 * @see Algebra#getOperations
 */
public abstract class Operation
{
	/**
	 * Returns the symbol representing the operation, which includes
	 * information such as the arity.
	 */
	public abstract Symbol getSymbol();

	/**
	 * Evaluates the operation with the given arguments. Note:
	 * Implementations should not store the <code>args</code> argument
	 * array because the caller might modify it after the return of this
	 * method.
	 * 
	 * @param args the list of indices of the argument elements. It must be
	 *		the case that <code>args.length == getArity()</code>.
	 *		After the return of this method, this array is unchanged
	 *		and can be freely modified.
	 * @return the index of the resulting element. The range of valid values
	 *		is <code>[0,getSize()-1]</code>, while the value
	 *		<code>-1</code> usually denotes an "undefined" entry, if
	 *		that is allowed.
	 * @throws UnsupportedOperationException if the elements of the algebra
	 *		are not (or cannot be) enumerated.
	 */
	public abstract int getValue(int[] args);

	/**
	 * Evaluates the operation with the given arguments. Note:
	 * Implementations should not store the <code>args</code> argument
	 * array because the caller might modify it after the return of this
	 * method.
	 * 
	 * @param args the list of labels of the argument elements. It must be
	 *		the case that <code>args.length == getArity()</code>.
	 *		After the return of this method, this array is unchanged
	 *		and can be freely modified.
	 * @return the label of the resulting element, or <code>null</code>
	 *		for an "undefined" entry, if that is allowed.
	 */
	public abstract Object getValue(Object[] args);

	/**
	 * Returns the universe of this operation.
	 */
	public abstract Universe getUniverse();
	
	/**
	 * Evaluates a constant operation.
	 */
	public int getConstantIndex()
	{
		return getValue(new int[] { });
	}

	/**
	 * Evaluates a constant operation.
	 */
	public Object getConstantElement()
	{
		return getValue(new Object[] { });
	}

	/**
	 * Evaluates a unary operation with the given argument.
	 */
	public int getValue(int a0)
	{
		return getValue(new int[] { a0 });
	}

	/**
	 * Evaluates a unary operation with the given argument.
	 */
	public Object getValue(Object a0)
	{
		return getValue(new Object[] { a0 });
	}

	/**
	 * Evaluates a binary operation with the given argument.
	 */
	public int getValue(int a0, int a1)
	{
		return getValue(new int[] { a0, a1 });
	}

	/**
	 * Evaluates a binary operation with the given argument.
	 */
	public Object getValue(Object a0, Object a1)
	{
		return getValue(new Object[] { a0, a1 });
	}

	/**
	 * Evaluates a ternary operation with the given argument.
	 */
	public int getValue(int a0, int a1, int a2)
	{
		return getValue(new int[] { a0, a1, a2 });
	}

	/**
	 * Evaluates a ternary operation with the given argument.
	 */
	public Object getValue(Object a0, Object a1, Object a2)
	{
		return getValue(new Object[] { a0, a1, a2 });
	}

	/**
	 * Returns <code>true</code> if this operation has the
	 * same arity as the one in the argument,
	 * <code>false</code> otherwise.
	 */
	public boolean isCompatible(Operation other)
	{
		return getSymbol().getArity() == other.getSymbol().getArity();
	}
}
