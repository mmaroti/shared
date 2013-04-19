package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import mmaroti.ua.alg.*;
import mmaroti.ua.io.*;

public class Jonsson
{
	public static int getValue(int depth, int[] args)
	{
		int z = 0;
		for(int i = 0; i < args.length; ++i)
			if( args[i] == 0 )
				++z;
				
		if( z >= 2 + depth )
			return 0;
		else if( z >= 2 )
			return depth + 2 - z;
		
		int m;
		if( args[0] == args[1] )
			m = args[0];
		else if( args[0] == args[2] )
			m = args[0];
		else if( args[1] == args[2] )
			m = args[1];
		else
			return args[0];
			
		z = 0;
		for(int i = 0; i < args.length; ++i)
		if( args[i] != m )
			++z;
		
		if( z <= 1 )
			return m;
		
		return args[0];
	}
	
	public static AlgebraBuffer createNuAlgebra(int size, int arity, int depth)
	{
		AlgebraBuffer alg = new AlgebraBuffer(size);
		
		for(int i = 1; i < arity-1; ++i)
		{
			FunctionBuffer op = new FunctionBuffer(size, 3);
			int[] args = new int[arity];
			
			for(int x = 0; x < size; ++x)
				for(int y = 0; y < size; ++y)
					for(int z = 0; z < size; ++z)
					{
						for(int j = 0; j < arity; ++j)
							if( j < i )	
								args[j] = x;
							else if( j == i )
								args[j] = y;
							else
								args[j] = z;

						op.set(x,y,z, getValue(depth, args));
					}
			
			alg.addOperation(op);
		}
		
		return alg;
	}

	public static void main(String[] _)
	{
		Algebra alg = createNuAlgebra(4, 8, 1);
		UaWriter.out.print(alg);
		
		FreeAlgebra free = new FreeAlgebra(alg, 2);
		System.out.println(free.size());
		
		UaWriter.out.print(free);
	}

/*
	public static Tournament freeAlgebraOver(int[] partial)
	{
		int size = (int)Math.round(Math.sqrt(partial.length));
		if( size * size != partial.length )
			throw new IllegalArgumentException();

		ArrayList algebras = createAllCompletions(
			new FunctionBuffer(size, 2, partial));

		ArrayList generators = new ArrayList();
		for(int i = 0; i < size; ++i)
		{
			ArrayList function = new ArrayList();
			Integer n = new Integer(i);

			int j = algebras.size();
			while( --j >= 0 )
				function.add(n);

			generators.add(function);
		}

		return new Tournament(new FreeAlgebra(algebras, generators));
	}
*/
}
