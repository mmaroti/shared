package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.util.*;

public class SubProdAlgebra implements Algebra
{
	protected DecisionDiagram.Node universe;
	protected Algebra[] algebras;

	public int size() { return universe.count; }
	public Algebra[] algebras() { return algebras; }

	public void toConcreteElem(int index, int[] func)
	{
		if( index < 0 || index >= universe.count ||
			func.length != algebras.length )
			throw new IllegalArgumentException();
			
		DecisionDiagram.Node[] subNodes = universe.subNodes;
		for(int i = 0; i < func.length; ++i)
		{
			int j = 0;
			while( (index -= subNodes[j].count) >= 0 )
				++j;
			index += subNodes[j].count;

			func[i] = j;
			subNodes = subNodes[j].subNodes;
		}
	}

	public int[] toConcreteElem(int index)
	{
		int[] func = new int[algebras.length];
		toConcreteElem(index, func);
		return func;
	}

	public int toAbstractElem(int[] func)
	{
		if( func.length != algebras.length )
			throw new IllegalArgumentException();

		int index = 0;
		DecisionDiagram.Node[] subNodes = universe.subNodes;
		for(int i = 0; i < func.length; ++i)
		{
			int j = func[i];
			while( --j >= 0 )
				index += subNodes[j].count;
			
			subNodes = subNodes[func[i]].subNodes;
		}

		return index;
	}

	public int toAbstractElem(List func)
	{
		if( func.size() != algebras.length )
			throw new IllegalArgumentException();
		
		int index = 0;
		DecisionDiagram.Node[] subNodes = universe.subNodes;
		Iterator iter = func.iterator();
		while( iter.hasNext() )
		{
			int a = ((Integer)iter.next()).intValue();
			
			int j = a;
			while( --j >= 0 )
				index += subNodes[j].count;
			
			subNodes = subNodes[a].subNodes;
		}
		
		return index;
	}

	protected Op[] operations;
	public Function[] operations() { return operations; }

	protected class Op implements Function
	{
		protected Function[] functions;
		protected int arity;
			
		public int size() { return universe.count; }
		public int arity() { return arity; }

		protected int[] funcArgs;					// arity
		protected int[] indices;					// arity
		protected DecisionDiagram.Node[] nodes;		// arity
			
		public int value(int[] args)
		{
			if( args.length != arity )
				throw new IllegalArgumentException();
			
			for(int k = 0; k < arity; ++k)
			{
				if( args[k] < 0 || args[k] >= universe.count )
					throw new IllegalArgumentException();

				nodes[k] = universe;
				indices[k] = args[k];
			}

			int resultIndex = 0;
			DecisionDiagram.Node resultNode = universe;
				
			for(int i = 0; i < functions.length; ++i)
			{
				for(int k = 0; k < arity; ++k)
				{
					DecisionDiagram.Node[] subNodes = nodes[k].subNodes;
					int index = indices[k];

					int j = 0;
					while( (index -= subNodes[j].count) >= 0 )
						++j;
					index += subNodes[j].count;

					funcArgs[k] = j;
					nodes[k] = subNodes[j];
					indices[k] = index;
				}

				DecisionDiagram.Node[] subNodes = resultNode.subNodes;
				int j = functions[i].value(funcArgs);
				resultNode = subNodes[j];
				
				while( --j >= 0 )
					resultIndex += subNodes[j].count;
			}
				
			return resultIndex;
		}
		
		public Op(int opIndex)
		{
			functions = new Function[algebras.length];
			for(int i = 0; i < algebras.length; ++i)
				functions[i] = algebras[i].operations()[opIndex];
				
			arity = functions[0].arity();

			funcArgs = new int[arity];
			indices = new int[arity];
			nodes = new DecisionDiagram.Node[arity];
		}
	}

	protected Rel[] relations;
	public Function[] relations() { return relations; }

	protected class Rel implements Function
	{
		protected Function[] functions;
		protected int arity;
			
		public int size() { return universe.count; }
		public int arity() { return arity; }

		protected int[] funcArgs;					// arity
		protected int[] indices;					// arity
		protected DecisionDiagram.Node[] nodes;		// arity
			
		public int value(int[] args)
		{
			if( args.length != arity )
				throw new IllegalArgumentException();
			
			for(int k = 0; k < arity; ++k)
			{
				if( args[k] < 0 || args[k] >= universe.count )
					throw new IllegalArgumentException();

				nodes[k] = universe;
				indices[k] = args[k];
			}

			for(int i = 0; i < functions.length; ++i)
			{
				for(int k = 0; k < arity; ++k)
				{
					DecisionDiagram.Node[] subNodes = nodes[k].subNodes;
					int index = indices[k];

					int j = 0;
					while( (index -= subNodes[j].count) >= 0 )
						++j;
					index += subNodes[j].count;

					funcArgs[k] = j;
					nodes[k] = subNodes[j];
					indices[k] = index;
				}

				int a = functions[i].value(funcArgs);
				if( a != 1 )
					return a;
			}
				
			return 1;
		}
		
		public Rel(int relIndex)
		{
			functions = new Function[algebras.length];
			for(int i = 0; i < algebras.length; ++i)
				functions[i] = algebras[i].relations()[relIndex];
				
			arity = functions[0].arity();

			funcArgs = new int[arity];
			indices = new int[arity];
			nodes = new DecisionDiagram.Node[arity];
		}
	}

	protected void InitOpRel()
	{
		int index = algebras[0].operations().length;
		operations = new Op[index];
		while( --index >= 0 )
			operations[index] = new Op(index);
		
		index = algebras[0].relations().length;
		relations = new Rel[index];
		while( --index >= 0 )
			relations[index] = new Rel(index);
	}
	
	public SubProdAlgebra(Algebra[] algebras, DecisionDiagram.Node universe)
	{
		this.algebras = algebras;
		this.universe = universe;
		InitOpRel();
	}
	
	public SubProdAlgebra(List algebras, List generators)
	{
		this.algebras = (Algebra[])algebras.toArray(new Algebra[0]);
		
		ComplexProdAlgebra complex = new ComplexProdAlgebra(algebras);
		universe = complex.emptySet();
		
		Iterator iter = generators.iterator();
		while( iter.hasNext() )
		{
			DecisionDiagram.Node spike = complex.spike(
				Arrays2.toIntArray((List)iter.next()));
			universe = complex.union(universe, spike);
		}
		
		universe = complex.closure(universe);
		InitOpRel();
	}
}
