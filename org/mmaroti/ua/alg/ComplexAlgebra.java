/**
 *	Copyright (C) Miklos Maroti, 2001-2005
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

public class ComplexAlgebra implements Algebra
{
	protected Algebra algebra;
	protected int algebraSize;
	public Algebra algebra() { return algebra; }

	protected ComplexAlgebra next;
	
	protected Object emptySet;
	public Object getEmptySet() { return emptySet; }

	protected Object fullSet;
	public Object getFullSet() { return fullSet; }

	protected CanonicalSet subsets = new CanonicalSet(Unifiers.identityArray);
	protected CacheMap unionCache = new CacheMap(Unifiers.identityArray);

	public Object union(Object a, Object b)
	{
		if( a == emptySet || a == b )
			return b;
		else if( b == emptySet )
			return a;
		else if( a == fullSet || b == fullSet )
			return fullSet;

		Object[] pair = new Object[] { a, b };
		Object ret = unionCache.get(pair);

		if( ret == null )
		{
			Object[] as = (Object[])a;
			Object[] bs = (Object[])b;
			Object[] union = new Object[algebraSize];
			
			for(int i = 0; i < algebraSize; ++i)
				union[i] = next.union(as[i], bs[i]);
		
			ret = subsets.canonicalize(union);
			unionCache.put(pair, ret);
		}
		
		return ret;
	}	

	protected class Arg
	{
		protected int[] intArgs;	// arity
		public int[] args() { return intArgs; }
	
		protected Object[][] matrix;	// arity * algebraSize
		protected Object[] nodeArgs;	// arity
	
		public Arg(int arity)
		{
			intArgs = new int[arity];
			nodeArgs = new Object[arity];
			matrix = new Object[arity][];
		}

		public boolean first()
		{
			Object nullEntry = next.emptySet;
		
			int i = intArgs.length;
			while( --i >= 0 )
			{
				Object[] row = matrix[i];
		
				int j = 0;
				while( j < algebraSize && row[j] == nullEntry )
					++j;
			
				if( j >= algebraSize )
					return false;
			
				intArgs[i] = j;
				nodeArgs[i] = row[j];
			}
		
			return true;
		}
	
		public boolean next()
		{
			Object nullEntry = next.emptySet;

			int i = intArgs.length;
			while( --i >= 0 )
			{
				Object[] row = matrix[i];

				int j = intArgs[i];
				while( ++j < algebraSize && row[j] == nullEntry )
					;

				if( j < algebraSize )
				{
					intArgs[i] = j;
					nodeArgs[i] = row[j];
					return true;
				}

				j = 0;
				while( row[j] == nullEntry )
					++j;

				intArgs[i] = j;
				nodeArgs[i] = row[j];
			}
		
			return false;
		}
		
	}

	protected class Op extends Arg
	{
		protected Operation operation;
		protected Op nextOp;

		public Op() { super(0); }
		
		public int getArity()
		{
			return operation.getSymbol().getArity();
		}
		
		public Op(int opIndex)
		{
			super(algebra.getOperations()[opIndex].getSymbol().getArity());
		
			operation = algebra.getOperations()[opIndex];
			nextOp = (ComplexAlgebra.Op)next.getOperations()[opIndex];
			valueCache = new HashMap();
		}
	
		protected HashMap valueCache;
		public Subset value(Subset[] args)
		{
			if( next == null )
			{
				int i = args.length;
				while( --i >= 0 )
					if( args[i] == emptySet )
						return emptySet;
			
				return fullSet;
			}

			Subset ret = (Subset)valueCache.get(new ShallowArray(args));
			if( ret != null )
				return ret;

			int i = args.length;
			while( --i >= 0 )
				matrix[i] = args[i].subNodes;

			i = algebraSize;
			while( --i >= 0 )				
				subNodes[i] = next.emptySet;
			
			if( first() )
			do
			{
				int value = operation.getValue(intArgs);
				subNodes[value] = next.union(subNodes[value], 
					nextOp.value(nodeArgs));
			}
			while( next() );
			
			ret = getRepresentative(subNodes);
			valueCache.put(new ShallowArray((Subset[])args.clone()), ret);
		
			return ret;
		}
	}

	protected ComplexAlgebra[] productLevels()
	{
		ComplexAlgebra[] levels = new ComplexAlgebra[productLength+1];

		ComplexAlgebra p = this;
		for(int i = 0; i <= productLength; ++i)
		{
			levels[i] = p;
			p = p.next;
		}
		
		return levels;
	}

	public Algebra[] algebras()
	{
		Algebra[] algebras = new Algebra[productLength];

		ComplexAlgebra p = this;
		for(int i = 0; i < productLength; ++i)
		{
			algebras[i] = p.algebra;
			p = p.next;
		}
		
		return algebras;
	}

	protected final Subset spike(int a,
		Subset rest)
	{
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;
			
		subNodes[a] = rest;
		
		return getRepresentative(subNodes);
	}

	public Subset spike(int[] coords)
	{
		if( coords.length != productLength )
			throw new IllegalArgumentException();

		ComplexAlgebra[] levels = productLevels();
			
		Subset node = levels[productLength].fullSet;
		int i = productLength;
		while( --i >= 0 )
			node = levels[i].spike(coords[i], node);
			
		return node;
	}

	public Subset closure2(Subset a)
	{
		if( next == null )
			return a;

		Subset old;
		do
		{
			old = a;

			for(int i = 0; i < operations.length; ++i)
			{
				Subset[] args = new Subset[operations[i].getArity()];
				for(int j = 0; j < args.length; ++j)
					args[j] = a;

				a = union(a, operations[i].value(args));
			}
		} while( old != a );

		return a;
	}

	public Subset closure(Subset a)
	{
		if( next == null )
			return a;

		int index = 0;
		int completed = 0;
		while( completed < operations.length )
		{
			for(;;)
			{
				Subset[] args = new Subset[
					operations[index].getArity()];
					
				for(int j = 0; j < args.length; ++j)
					args[j] = a;

				Subset old = a;
				a = union(a, operations[index].value(args));

				if( old == a )
					break;
				
				completed = 0;
			};
			
			++completed;
			if( ++index >= operations.length )
				index = 0;
		};

		return a;
	}

	protected void InitBoolean(int operationCount)
	{
		algebraSize = 0;
		productLength = 0;

		emptySet = new Subset(0);
		fullSet = new Subset(1);
		
		Op op = new Op();
		operations = new Op[operationCount];
		for(int i = 0; i < operationCount; ++i)
			operations[i] = op;
	}

	protected void InitAlgebra(Algebra algebra)
	{
		if( next == null )
			throw new IllegalStateException();
	
		this.algebra = algebra;
		algebraSize = algebra.getSize();

		productLength = next.productLength + 1;
		representatives = new HashMap();
		subNodes = new Subset[algebraSize];
		
		unionCache = new HashMap();
		intersectionCache = new HashMap();
		complementCache = new HashMap();
		
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;
		emptySet = getRepresentative(subNodes);

		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.fullSet;
		fullSet = getRepresentative(subNodes);
			
		Operation[] ops = algebra.getOperations();
		operations = new Op[ops.length];
		for(int i = 0; i < ops.length; ++i)
			operations[i] = new Op(i);
	}

	public ComplexAlgebra(int operationCount)
	{
		InitBoolean(operationCount);
	}

	public ComplexAlgebra(Algebra algebra, ComplexAlgebra next)
	{
		this.next = next;
		InitAlgebra(algebra);
	}

	public ComplexAlgebra(Algebra algebra)
	{
		next = new ComplexAlgebra(algebra.getOperations().length);
		InitAlgebra(algebra);
	}

	public ComplexAlgebra(List algebras)
	{
		if( algebras.isEmpty() )
			throw new IllegalArgumentException();
		
		Algebra a = (Algebra)algebras.get(0);
		next = new ComplexAlgebra(a.getOperations().length);
			
		int i = algebras.size();
		while( --i >= 1 )
			next = new ComplexAlgebra((Algebra)algebras.get(i), next);
		
		InitAlgebra((Algebra)algebras.get(0));
	}
}
