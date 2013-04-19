/**
 *	Copyright (C) Miklos Maroti, 2002
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

import org.mmaroti.ua.set.*;

/**
 * An algebra whose operation tables are explicitly stored in tables.
 * Sometimes, it is advisable to copy small derived algebras to
 * AlgebraBuffers to speed up operations.
 *
 * @author mmaroti@math.u-szeged.hu
 */
public class AlgebraBuffer extends Algebra
{
	protected FiniteSet universe;
	
	public Set getUniverse()
	{
		return universe;
	}

	/**
	 * Equals universe.getSize()
	 */
	protected int size;
	
	/**
	 * Constructs an algebra with no operations and relations.
	 */
	public AlgebraBuffer(Signature signature, int size)
	{
		this.size = size;
		universe = new FiniteSet(size);
		
		if( size <= 0 )
			throw new IllegalArgumentException("the size of an algebra must be positive");

		Symbol[] ops = signature.getOperations();
		operations = new Op[ops.length];
		for(int i = 0; i < operations.length; ++i)
			operations[i] = new Op(ops[i]);
	}

	/**
	 * Creates a copy of an algebra.
	 */
	public AlgebraBuffer(Algebra algebra)
	{
		size = algebra.getSize();
		universe = new FiniteSet(size);
		
		Operation[] ops = algebra.getOperations();
		operations = new Op[ops.length];
		for(int i = 0; i < ops.length; ++i)
			operations[i] = new Op(ops[i]);
	}

	/**
	 * The list of fundamental operations of the algebra.
	 * @see #getOperations
	 * @see #getOperationTables
	 */
	protected Op[] operations;

	public final Op[] getOperations()
	{
		return operations;
	}

	/**
	 * Returns the operation table for the given operation
	 */
	public final AlgebraBuffer.Op getOperationTable(int index)
	{
		return operations[index];
	}

	/**
	 * An operation table that can be modified. The entries in the operation table
	 * are stored in an array.
	 */
	public class Op extends Operation
	{
		/**
		 * Calculates <code>base^exponent</code>. The exponent must be non-negative.
		 * @throws IllegalArgumentException if the exponent is negative, or the result does not
		 *	fit in an Integer.
		 */
		public int power(int base, int exponent)
		{
			if( exponent < 0 )
				throw new IllegalArgumentException("the exponent must be non-negative");

			long result = 1;
			while( --exponent >= 0 )
			{
				result *= base;
				if( result > Integer.MAX_VALUE || result < Integer.MIN_VALUE )
					throw new IllegalArgumentException("the power is too big (does not fit in an Integer)");
			}

			return (int)result;
		}

		/**
		 * The symbol of the relation.
		 */
		protected Symbol symbol;

		/**
		 * Returns the symbol of the operation or relation.
		 */
		public final Symbol getSymbol()
		{
			return symbol;
		}

		/**
		 * Returns the position of the specified entry in the operation table.
		 *
		 * @param args the list of arguments of the mapping.
		 * @return the position of the entry in the table that corresponds to <code>args</code>.
		 * @throws IllegalArgumentException if the number of arguments is not valid.
		 * @throws IndexOutOfBoundsException if one of the arguments is not in the 
		 *	interval <code>[0,size-1]</code>.
		 */
		public final int getPosition(int[] args)
		{
			if( args.length != symbol.arity )
				throw new IllegalArgumentException("the length of args must be the arity");

			int a = 0;
			for(int i = 0; i < symbol.arity; ++i)
			{
				if( args[i] < 0 || args[i] >= size )
					throw new IndexOutOfBoundsException("invalid argument at index " + i);
				
				a *= size;
				a += args[i];
			}
				
			return a;
		}

		/**
		 * Returns the position of the specified entry in the operation table.
		 */
		public final int getPosition(Object[] args)
		{
			if( args.length != symbol.arity )
				throw new IllegalArgumentException("the length of args must be the arity");

			int a = 0;
			for(int i = 0; i < symbol.arity; ++i)
			{
				int b = universe.getIndex(args[i]);
				if( b < 0 || b >= size )
					throw new IndexOutOfBoundsException("invalid argument at index " + i);
				
				a *= size;
				a += b;
			}
				
			return a;
		}
		
		/**
		 * Returns the position of an entry in a unary operation table.
		 */
		public final int getPosition(int arg0)
		{
			if( symbol.arity != 1 )
				throw new UnsupportedOperationException("this operation is not unary");

			if( arg0 < 0 || arg0 >= size )
				throw new IndexOutOfBoundsException("invalid argument");
				
			return arg0;
		}

		/**
		 * Returns the position of an entry in a binary operation table.
		 */
		public final int getPosition(int arg0, int arg1)
		{
			if( symbol.arity != 2 )
				throw new UnsupportedOperationException("this operation is not binary");

			if( arg0 < 0 || arg0 >= size || arg1 < 0 || arg1 >= size )
				throw new IndexOutOfBoundsException("invalid argument");
				
			return arg0 * size + arg1;
		}

		/**
		 * Creates an empty operation table.
		 */
		public Op(Symbol symbol)
		{
			this.symbol = symbol;

			table = new int[power(size, symbol.arity)];
		}

		/**
		 * Creates a copy of an operation.
		 */
		public Op(Operation operation)
		{
			symbol = operation.getSymbol();
			
			if( size != operation.getUniverse().getSize() )
				throw new IllegalArgumentException("the underlying set is not of the same size");
				
			table = new int[power(size, symbol.arity)];

			int[] args = new int[symbol.arity];
			int index = 0;
			for(;;)
			{
				table[index] = operation.getValue(args);
				if( ++index >= table.length )
					break;

				int i = symbol.arity;
				while( ++args[--i] >= size )
						args[i] = 0;
			};
		}

		/**
		 * An array holding the underlying operation table.
		 * @see #getTable
		 */
		protected int[] table;

		/**
		 * Returns an array holding the underlying operation table. 
		 * The number of entries in the table is <code>size^arity</code>.
		 */
		public final int[] getTable()
		{
			return table;
		}

		/**
		 * Returns the entry at the specified position.
		 *
		 * @see #getTable
		 * @see #getPosition(int[])
		 */
		public final int getValueByPosition(int position)
		{
			return table[position];
		}

		/**
		 * Sets the entry at the specified position.
		 *
		 * @see #getTable
		 * @see #getPosition(int[])
		 */
		public final void setValueByPosition(int position, int value)
		{
			table[position] = value;
		}

		/**
		 * Evaluates the mapping at the given arguments.
		 * This function is equivalent to <code>getValueByPosition(getPosition(args))</code>.
		 *
		 * @see #getPosition(int[])
		 */
		public final int getValue(int[] args)
		{
			return table[getPosition(args)];
		}

		/**
		 * Evaluates the unary mapping at the given argument. This is a short-hand
		 * notation for unary operations.
		 */
		public final int getValue(int arg0)
		{
			return table[getPosition(arg0)];
		}

		/**
		 * Evaluates the binary mapping at the given argument. This is a short-hand
		 * notation for binary operations.
		 */
		public final int getValue(int arg0, int arg1)
		{
			return table[getPosition(arg0, arg1)];
		}

		/**
		 * Sets the value of the mapping at the arguments.
		 * This operation is equivalent to <code>setValueByPosition(getPosition(args), value)</code>.
		 *
		 * @see #getPosition(int[])
		 */
		public final void setValue(int[] args, int value)
		{
			table[getPosition(args)] = value;
		}

		/**
		 * Sets the value of a unary mapping at the given arguments.
		 * This is a short-hand notation for unary operations.
		 */
		public void setValue(int arg0, int value)
		{
			table[getPosition(arg0)] = value;
		}

		/**
		 * Sets the value of a binary mapping at the given arguments.
		 * This is a short-hand notation for unary operations.
		 */
		public void setValue(int arg0, int arg1, int value)
		{
			table[getPosition(arg0, arg1)] = value;
		}

		/**
		 * Evaluates the mapping at the given arguments.
		 * This function is equivalent to 
		 * <code>algebra.getElement(getValue(getPosition(args)))</code>.
		 *
		 * @see #getPosition(Object[])
		 * @see #getValueByPosition
		 */
		public final Object getValue(Object[] args)
		{
			return universe.getElement(table[getPosition(args)]);
		}

		/**
		 * Sets the value of the mapping at the arguments.
		 * This operation is equivalent to 
		 * <code>setValueByPosition(getPosition(args), algebra.getIndex(value))</code>.
		 *
		 * @see #getPosition(Object[])
		 * @see #setValueByPosition
		 */
		public final void setValue(Object[] args, Object value)
		{
			table[getPosition(args)] = universe.getIndex(value);
		}
		
		public Set getUniverse()
		{
			return universe;
		}
	}
}
