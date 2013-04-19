package mmaroti.ua.alg;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.Arrays;

public class AlgebraBuffer implements Algebra
{
	private int size;
	private Function operations[];
	private Function relations[];

	public Function[] operations() { return operations; }
	public Function[] relations() { return relations; }
	public int size() { return size; }

	public AlgebraBuffer(int size)
	{
		if( size <= 0 )
			throw new IllegalArgumentException();
		
		this.size = size;
		this.operations = new Function[0];
		this.relations = new Function[0];
	}	

	public AlgebraBuffer(int size, Function operations[], Function relations[])
	{
		if( size <= 0 )
			throw new IllegalArgumentException();
		
		for(int i = 0; i < operations.length; ++i)
			if( operations[i].size() != size )
				throw new IllegalArgumentException();
		
		for(int i = 0; i < relations.length; ++i)
			if( relations[i].size() != size )
				throw new IllegalArgumentException();
		
		this.size = size;
		this.operations = operations;
		this.relations = relations;
	}
	
	public AlgebraBuffer(Algebra algebra)
	{
		if( algebra.size() <= 0 )
			throw new IllegalArgumentException();
	
		size = algebra.size();
		
		Function[] fs = algebra.operations();
		operations = new Function[fs.length];
		for(int i = 0; i < fs.length; ++i)
			operations[i] = new FunctionBuffer(fs[i]);
	
		fs = algebra.relations();
		relations = new Function[fs.length];
		for(int i = 0; i < fs.length; ++i)
			relations[i] = new FunctionBuffer(fs[i]);
	}

	public Object clone()
	{
		return new AlgebraBuffer(this);
	}

	public boolean equals(Object o)
	{
		if( !(o instanceof AlgebraBuffer) )
			return false;
		
		AlgebraBuffer a = (AlgebraBuffer)o;

		return size == a.size &&
			Arrays.equals(operations, a.operations) &&
			Arrays.equals(relations, a.relations);
	}

	public int hashCode()
	{
		int a = size;
		
		for(int i = 0; i < operations.length; ++i)
			a += operations[i].hashCode();
			
		for(int i = 0; i < relations.length; ++i)
			a += relations[i].hashCode();
		
		return a;
	}
	
	public void addOperation(Function op)
	{
		if( op.size() != size )
			throw new IllegalArgumentException();
	
		Function ops[] = new Function[operations.length + 1];
		
		for(int i = 0; i < operations.length; ++i)
			ops[i] = operations[i];
		
		ops[operations.length] = op;
		
		operations = ops;
	}
	
	public void addRelation(Function rel)
	{
		if( rel.size() != size )
			throw new IllegalArgumentException();
	
		Function rels[] = new Function[relations.length + 1];
		
		for(int i = 0; i < relations.length; ++i)
			rels[i] = relations[i];
		
		rels[relations.length] = rel;
		
		relations = rels;
	}
}
