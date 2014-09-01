package org.mmaroti.ua.alg;

import org.mmaroti.ua.set.Universe;

/**
 * Represents a relation of a {@link Structure}. If the elements of the
 * {@link Structure} are enumerated, then this is an <code>n</code>-ary
 * relation on the set <code>[0,size-1]</code> of integers. 
 * 
 * @see Structure#getRelations
 */
public abstract class Relation
{
	/**
	 * Returns the symbol representing the operation, which includes
	 * information such as the arity.
	 */
	public abstract Symbol getSymbol();

	/**
	 * Checks if the given tuple is in the relation. Note:
	 * Implementations should not store the <code>args</code> argument
	 * array because the caller might modify it after the return of this
	 * method.
	 * 
	 * @param tuple the list of indices of the argument elements. It must be
	 *		the case that <code>args.length == getArity()</code>.
	 *		After the return of this method, this array is unchanged
	 *		and can be freely modified.
	 * @return <code>1</code> if the tuple is in the relation,
	 * 		<code>0</code> if it is not in the relation, and 
	 *		<code>-1</code> denotes an "undefined" entry, if that is allowed.
	 * @throws UnsupportedOperationException if the elements of the algebra
	 *		are not (or cannot be) enumerated.
	 */
	public abstract byte getValue(int[] tuple);

	/**
	 * Checks if the given tuple is in the relation. Note:
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
	public abstract byte getValue(Object[] tuple);

	/**
	 * Returns the universe of this operation.
	 */
	public abstract Universe getUniverse();

	/**
	 * Evaluates a constant relational value.
	 */
	public byte getConstant()
	{
		return getValue(new int[] { });
	}

	/**
	 * Evaluates a unary relation with the given argument.
	 */
	public byte getValue(int a0)
	{
		return getValue(new int[] { a0 });
	}

	/**
	 * Evaluates a unary relation with the given argument.
	 */
	public byte getValue(Object a0)
	{
		return getValue(new Object[] { a0 });
	}

	/**
	 * Evaluates a binary relation with the given argument.
	 */
	public byte getValue(int a0, int a1)
	{
		return getValue(new int[] { a0, a1 });
	}

	/**
	 * Evaluates a ternary relation with the given argument.
	 */
	public byte getValue(int a0, int a1, int a2)
	{
		return getValue(new int[] { a0, a1, a2 });
	}

	/**
	 * Evaluates a ternary relation with the given argument.
	 */
	public byte getValue(Object a0, Object a1, Object a2)
	{
		return getValue(new Object[] { a0, a1, a2 });
	}

	/**
	 * Returns <code>true</code> if this relation has the
	 * same arity as the one in the argument,
	 * <code>false</code> otherwise.
	 */
	public boolean isCompatible(Relation other)
	{
		return getSymbol().getArity() == other.getSymbol().getArity();
	}
}
