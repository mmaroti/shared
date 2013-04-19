package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import mmaroti.ua.alg.*;

public class FilterNode extends Node
{
	private Node node;
	private Algebra algebra;
	private String filter;
	
	private static boolean isConservative(Algebra algebra)
	{
		int size = algebra.size();
		Function[] ops = algebra.operations();
		for(int i = 0; i < ops.length; ++i)
		{
			Function op = ops[i];
			int[] args = new int[op.arity()];
			
			outer: for(;;)
			{
				int value = op.value(args);
				for(int j = 0; j <= args.length; ++j)
				{
					if( j == args.length )
						return false;
					else if( args[j] == value )
						break;
				}
				
				for(int j = 0; j <= args.length; ++j)
				{
					if( j == args.length )
						break outer;
					if( ++args[j] >= size )
						args[j] = 0;
					else
						break;
				}
			}
		}
		
		return true;
	}
	
	private static boolean isSubdirectProductOfTournaments(Algebra algebra)
	{
		int size = algebra.size();
		Function ops = algebra.operations()[0];
		
		for(int a = 0; a < size-1; ++a)
			for(int b = a+1; b< size; ++b)
			{
				int c = ops.value(new int[] {a, b});
				if( c == a || c == b )
					continue;
				
				Equivalence ac = FactorAlgebra.congruence(algebra, a, c);
				Equivalence bc = FactorAlgebra.congruence(algebra, b, c);
				
				if( ! Equivalence.meet(ac, bc).isDiagonalRelation() )
					return false;
				
				if( ! isSubdirectProductOfTournaments(new AlgebraBuffer(new FactorAlgebra(algebra, ac))) )
					return false;

				if( ! isSubdirectProductOfTournaments(new AlgebraBuffer(new FactorAlgebra(algebra, bc))) )
					return false;
				
				return true;
			}
		
		return true;
	}
	
	private static boolean satisfiesThreeVariableTournamentEquations(Algebra algebra)
	{
		int size = algebra.size();
		
		for(int a = 0; a < size-2; ++a)
			for(int b = a+1; b < size-1; ++b)
				for(int c = b+1; c < size; ++c)
				{
					SubAlgebra subalgebra = new SubAlgebra(algebra);
					subalgebra.addGenerator(a);
					subalgebra.addGenerator(b);
					subalgebra.addGenerator(c);
					subalgebra.generate();
					
					if( ! isSubdirectProductOfTournaments(subalgebra) )
						return false;
				}
		
		return true;
	}
	
	public int evaluate()
	{
		int r = node.evaluate();

		if( r == 1 && ( 
				(filter.contains(" si ") && !FactorAlgebra.isSubdirectlyIrreducible(algebra))
				|| (filter.contains(" noncon ") && isConservative(algebra))
				|| (filter.contains(" toursp ") && ! isSubdirectProductOfTournaments(algebra))
				|| (filter.contains(" nontoursp ") && isSubdirectProductOfTournaments(algebra))
				|| (filter.contains(" intour3 ") && ! satisfiesThreeVariableTournamentEquations(algebra))
			))
			r = 0;
			
		return r;
	}

	public FilterNode(Node node, Algebra algebra, String filter)
	{
		this.node = node;
		this.algebra = algebra;
		this.filter = filter;
	}
}
