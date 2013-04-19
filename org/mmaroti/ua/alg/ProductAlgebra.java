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

import java.util.List;
import java.util.ArrayList;
import org.mmaroti.ua.set.*;

/**
 * This class can calculate the product of finitely many algebras. This class
 * can work for both enumerable and non-enumerable algebras. The elements
 * of the product are enumerated (whenever possible) such that 
 * <pre>
 *      0 = &lt;0,...,0&gt;
 *      1 = &lt;0,...,1&gt;
 *       ...
 * size-1 = &lt;size_0-1,...,size_{k-1}-1&gt;
 * </pre>
 * where <code>size = size_0 * ... * size_{k-1}</code>. The elements are labeled
 * by arrays of labels of the factor algebras. The labels are canonicalized to
 * save memory and to be able to compare them by the == operator.
 *  
 * @author mmaroti@math.u-szeged.hu
 */
public class ProductAlgebra extends Algebra
{
	protected ProductSet universe;
	
	public Set getUniverse()
	{
		return universe;
	}

	protected Algebra[] factors;
	
	/**
	 * Creates a product algebra of non-empty list of algebras.
	 * @param algebras The list of similar algebras.
	 * @throws IllegalArgumentException if the list is empty or the algebras
	 * are not similar.
	 */
	public ProductAlgebra(List<Algebra> algebras)
	{
		factors = new Algebra[algebras.size()];
		ArrayList<Set> universes = new ArrayList<Set>(factors.length);
		
		for(int i = 0; i < algebras.size(); ++i)
		{
			factors[i] = algebras.get(i);
			universes.add(factors[i].getUniverse());
		}
		
		universe = new ProductSet(universes);

		if( ! Algebra.areCompatible(algebras) )
			throw new IllegalArgumentException("the provided list of algebras " +
					"are not of the same signature");
		
		if( algebras.size() <= 0 )
			throw new IllegalArgumentException("At least one algebra must be specified");

		int ops = this.factors[0].getOperations().length;

		indexTuple = new int[this.factors.length];

		operations = new Op[ops];
		for(int i = 0; i < ops; ++i)
			operations[i] = new Op(i);
	}
	
	protected int[] indexTuple;
	protected Op[] operations;
	
	public Operation[] getOperations()
	{
		return operations;
	}
	
	/**
	 * This class implements the product operation, which is
	 * calculated coordinate-wise. If one of the coordinates 
	 * is undefined (<code>-1</code> or <code>null</code>)
	 * then the result is undefined.
	 */
	public class Op extends Operation
	{
		public Set getUniverse()
		{
			return universe;
		}

		protected Operation[] operations;
		protected Symbol symbol;

		public Symbol getSymbol()
		{
			return symbol;
		}
		
		/**
		 * Constructs a product operation. The name of the operation
		 * will be the name of the first operation.
		 * @throws IllegalArgumentException if the list is empty
		 * or the arities of the operations are not the same.
		 */
		protected Op(int opIndex)
		{
			operations = new Operation[factors.length];
			for(int i = 0; i < factors.length; ++i)
				operations[i] = factors[i].getOperations()[opIndex];
			
			symbol = operations[0].getSymbol();

			arg1 = new int[symbol.arity];
			arg2 = new int[symbol.arity];
			arg3 = new Object[symbol.arity];
		}
		
		/**
		 * Constructs a product operation of an empty product
		 * @param name The name of the operation
		 * @param arity The arity of the operation
		 * 
		 * @throws IllegalArgumentException if the arity is negative.
		 */
		protected Op(Symbol symbol)
		{
			this.symbol = symbol;
			operations = new Operation[0];

			// we will never use the other arrays
			arg1 = new int[symbol.arity];
		}
		
		protected int[] arg1;
		protected int[] arg2;
		
		public int getValue(int[] args)
		{
			if( getSize() <= 0 )
				throw new UnsupportedOperationException(
					"The elements of the underlying set cannot be enumerated");
			
			for(int i = 0; i < symbol.arity; ++i)
			{
				// if undefined value somewhere
				if( (arg1[i] = args[i]) < 0 )
					return -1;
			}

			int i = operations.length;
			while( --i >= 0 )
			{
				int s = factors[i].getSize();
				for(int j = 0; j < symbol.arity; ++j)
				{
					arg2[j] = arg1[j] % s;
					arg1[j] /= s;
				}
				indexTuple[i] = operations[i].getValue(arg2);
			}
			
			return universe.getIndex(indexTuple);
		}

		protected Object[] arg3;
		
		public Object getValue(Object[] args)
		{
			Object[] vector = new Object[operations.length];
			
			for(int i = 0; i < operations.length; ++i)
			{
				for(int j = 0; j < symbol.arity; ++j)
					arg3[j] = ((Object[])args[j])[i];

				// if undefined somewhere
				if( (vector[i] = operations[i].getValue(arg3)) == null )
					return null;
			}
			
			return vector;
		}
	}
}
