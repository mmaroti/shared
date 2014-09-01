package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.alg.*;

public class Tournaments
{
	public static AlgebraBuffer createTournament(FunctionBuffer relation)
	{
		int size = relation.size();
	
		FunctionBuffer op = new FunctionBuffer(size, 2);
		
		for(int i = 0; i < size; ++i)
			op.set(i,i,i);
			
		for(int i = 0; i < size-1; ++i)
			for(int j = i+1; j < size; ++j)
			{
				int a = relation.value(i,j) != 0 ? i : j;
				op.set(i,j,a);
				op.set(j,i,a);
			}

		AlgebraBuffer alg = new AlgebraBuffer(size);
		alg.addOperation(op);
		
		return alg;
	}
	
	public static List<Algebra> createAllCompletions(FunctionBuffer relation)
	{
		int size = relation.size();
		int[] indices = new int[(size * (size-1)) / 2];
		
		int indexCount = 0;
		for(int i = 0; i < size-1; ++i)
			for(int j = i+1; j < size; ++j)
				if( relation.value(i,j) < 0 )
				{
					indices[indexCount++] = relation.index(i,j);
					relation.set(i,j,0);
				}
				
		int[] buffer = relation.buffer();
		List<Algebra> algebras = new ArrayList<Algebra>();

		int i;
		do
		{
			algebras.add(createTournament(relation));
			
			for(i = 0; i < indexCount && ++buffer[indices[i]] > 1 ; ++i)
				buffer[indices[i]] = 0;

		} while( i < indexCount );
		
		return algebras;
	}
	
	public static Tournament freeAlgebraOver(int[] partial)
	{
		int size = (int)Math.round(Math.sqrt(partial.length));
		if( size * size != partial.length )
			throw new IllegalArgumentException();

		List<Algebra> algebras = createAllCompletions(
			new FunctionBuffer(size, 2, partial));

		List<List<Integer>> generators = new ArrayList<List<Integer>>();
		for(int i = 0; i < size; ++i)
		{
			List<Integer> function = new ArrayList<Integer>();
			Integer n = new Integer(i);

			int j = algebras.size();
			while( --j >= 0 )
				function.add(n);

			generators.add(function);
		}

		return new Tournament(new FreeAlgebra(algebras, generators));
	}
}
