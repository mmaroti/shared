package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.util.*;

public class ComplexProdAlgebra2
{
	protected Algebra algebra;
	protected int algebraSize;
	public Algebra algebra() { return algebra; }

	protected ComplexProdAlgebra2 next;
	protected int productLength;
	
	protected Node emptySet;
	public Node emptySet() { return emptySet; }

	protected Node fullSet;
	public Node fullSet() { return fullSet; }

	public static class Node
	{
		public Node[] subNodes;

		public boolean equals(Object o)
		{
			return (o instanceof Node) &&
				Arrays2.shallowEquals(subNodes, ((Node)o).subNodes);
		}
		
		public int hashCode()
		{
			return Arrays2.shallowHashCode(subNodes);
		}

		protected int count;
		public int count()
		{
			if( count >= 0 )
				return count;
			
			count = 0;
			
			int i = subNodes.length;
			while( --i >= 0 )
				count += subNodes[i].count();
				
			return count;
		}

		public Node(int c)
		{
			subNodes = new Node[0];
			count = c;
		}

		public Node(Node[] subNodes)
		{
			this.subNodes = subNodes;
			count = -1;
		}
	}

	protected HashMap<Node,Node> representatives;
	protected Node getRepresentative(Node[] subNodes)
	{
		Node ret = representatives.get(new Node(subNodes));
		if( ret != null )
			return ret;
		
		ret = new Node(subNodes.clone());
		representatives.put(ret, ret);
		return ret;
	}

	protected Node[] subNodes;

	protected HashMap<ShallowPair,Node> unionCache;
	public Node union(Node a, Node b)
	{
		if( a == emptySet || a == b )
			return b;
		else if( b == emptySet )
			return a;
		else if( a == fullSet || b == fullSet )
			return fullSet;

		ShallowPair pair = new ShallowPair(a, b);
		Node ret = unionCache.get(pair);
		if( ret != null )
			return ret;
		
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.union(a.subNodes[i], b.subNodes[i]);
		
		ret = getRepresentative(subNodes);
		unionCache.put(pair, ret);
		return ret;
	}	

	protected HashMap<ShallowPair,Node> intersectionCache;
	public Node intersection(Node a, Node b)
	{
		if( a == fullSet || a == b )
			return b;
		else if( b == fullSet )
			return a;
		else if( a == emptySet || b == emptySet )
			return emptySet;
			
		ShallowPair pair = new ShallowPair(a, b);
		Node ret = intersectionCache.get(pair);
		if( ret != null )
			return ret;
		
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.intersection(a.subNodes[i], b.subNodes[i]);
		
		ret = getRepresentative(subNodes);
		intersectionCache.put(pair, ret);
		return ret;
	}	

	protected HashMap<Node,Node> complementCache;
	public Node complement(Node a)
	{
		if( a == emptySet )
			return fullSet;
		else if( a == fullSet )
			return emptySet;
			
		Node ret = complementCache.get(a);
		if( ret != null )
			return ret;
		
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.complement(a.subNodes[i]);
		
		ret = getRepresentative(subNodes);
		complementCache.put(a, ret);
		return ret;
	}	

	protected class Arg implements Argument
	{
		protected int[] intArgs;	// arity
		public int[] args() { return intArgs; }
	
		protected Node[][] matrix;	// arity * algebraSize
		protected Node[] nodeArgs;	// arity
	
		public Arg(int arity)
		{
			intArgs = new int[arity];
			nodeArgs = new Node[arity];
			matrix = new Node[arity][];
		}

		public boolean first()
		{
			Node nullEntry = next.emptySet;
		
			int i = intArgs.length;
			while( --i >= 0 )
			{
				Node[] row = matrix[i];
		
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
			Node nullEntry = next.emptySet;

			int i = intArgs.length;
			while( --i >= 0 )
			{
				Node[] row = matrix[i];

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

	protected Op[] operations;
	public Node operationValue(int opIndex, 
		Node[] args)
	{
		return operations[opIndex].value(args);
	}
	
	protected class Op extends Arg
	{
		protected Function operation;
		protected Op nextOp;

		public Op() { super(0); }
		
		public Op(int opIndex)
		{
			super(algebra.operations()[opIndex].arity());
		
			operation = algebra.operations()[opIndex];
			nextOp = next.operations[opIndex];
			valueCache = new HashMap<ShallowArray,Node>();
		}
	
		protected HashMap<ShallowArray,Node> valueCache;
		public Node value(Node[] args)
		{
			if( next == null )
			{
				int i = args.length;
				while( --i >= 0 )
					if( args[i] == emptySet )
						return emptySet;
			
				return fullSet;
			}

			Node ret = valueCache.get(new ShallowArray(args));
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
				int value = operation.value(intArgs);
				subNodes[value] = next.union(subNodes[value], 
					nextOp.value(nodeArgs));
			}
			while( next() );
			
			ret = getRepresentative(subNodes);
			valueCache.put(new ShallowArray(args.clone()), ret);
		
			return ret;
		}
	}

	protected ComplexProdAlgebra2[] productLevels()
	{
		ComplexProdAlgebra2[] levels = new ComplexProdAlgebra2[productLength+1];

		ComplexProdAlgebra2 p = this;
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

		ComplexProdAlgebra2 p = this;
		for(int i = 0; i < productLength; ++i)
		{
			algebras[i] = p.algebra;
			p = p.next;
		}
		
		return algebras;
	}

	protected final Node spike(int a,
		Node rest)
	{
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;
			
		subNodes[a] = rest;
		
		return getRepresentative(subNodes);
	}

	public Node spike(int[] coords)
	{
		if( coords.length != productLength )
			throw new IllegalArgumentException();

		ComplexProdAlgebra2[] levels = productLevels();
			
		Node node = levels[productLength].fullSet;
		int i = productLength;
		while( --i >= 0 )
			node = levels[i].spike(coords[i], node);
			
		return node;
	}

	public Node closure2(Node a)
	{
		if( next == null )
			return a;

		Node old;
		do
		{
			old = a;

			for(int i = 0; i < operations.length; ++i)
			{
				Node[] args = new Node[operations[i].operation.arity()];
				for(int j = 0; j < args.length; ++j)
					args[j] = a;

				a = union(a, operations[i].value(args));
			}
		} while( old != a );

		return a;
	}

	public Node closure(Node a)
	{
		if( next == null )
			return a;

		int index = 0;
		int completed = 0;
		while( completed < operations.length )
		{
			for(;;)
			{
				Node[] args = new Node[
					operations[index].operation.arity()];
					
				for(int j = 0; j < args.length; ++j)
					args[j] = a;

				Node old = a;
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

	protected void InitBoolean(int operationCount, int relationCount)
	{
		algebraSize = 0;
		productLength = 0;

		emptySet = new Node(0);
		fullSet = new Node(1);
		
		Op op = new Op();
		operations = new Op[operationCount];
		for(int i = 0; i < operationCount; ++i)
			operations[i] = op;
			
//		Rel rel = new Rel();
//		relations = new Rel[relationCount];
//		for(int i = 0; i < relationCount; ++i)
//			relations[i] = rel;
	}

	protected void InitAlgebra(Algebra algebra)
	{
		if( next == null )
			throw new IllegalStateException();
	
		this.algebra = algebra;
		algebraSize = algebra.size();

		productLength = next.productLength + 1;
		representatives = new HashMap<Node,Node>();
		subNodes = new Node[algebraSize];
		
		unionCache = new HashMap<ShallowPair,Node>();
		intersectionCache = new HashMap<ShallowPair,Node>();
		complementCache = new HashMap<Node,Node>();
		
		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;
		emptySet = getRepresentative(subNodes);

		for(int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.fullSet;
		fullSet = getRepresentative(subNodes);
			
		Function[] funcs = algebra.operations();
		operations = new Op[funcs.length];
		for(int i = 0; i < funcs.length; ++i)
			operations[i] = new Op(i);

//		funcs = algebra.relations();
//		relations = new Rel[funcs.length];
//		for(int i = 0; i < funcs.length; ++i)
//			relations[i] = new Rel(i);		
	}

	public ComplexProdAlgebra2(int operationCount, int relationCount)
	{
		InitBoolean(operationCount, relationCount);
	}

	public ComplexProdAlgebra2(Algebra algebra, ComplexProdAlgebra2 next)
	{
		this.next = next;
		InitAlgebra(algebra);
	}

	public ComplexProdAlgebra2(Algebra algebra)
	{
		next = new ComplexProdAlgebra2(algebra.operations().length,
			algebra.relations().length);
		InitAlgebra(algebra);
	}

	public ComplexProdAlgebra2(List<Algebra> algebras)
	{
		if( algebras.isEmpty() )
			throw new IllegalArgumentException();
		
		Algebra a = (Algebra)algebras.get(0);
		next = new ComplexProdAlgebra2(a.operations().length, 
			a.relations().length);
			
		int i = algebras.size();
		while( --i >= 1 )
			next = new ComplexProdAlgebra2((Algebra)algebras.get(i), next);
		
		InitAlgebra((Algebra)algebras.get(0));
	}
}
