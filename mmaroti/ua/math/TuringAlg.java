package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.alg.*;
import mmaroti.ua.io.*;

public class TuringAlg
{
	public static Algebra createTuringAlgebra()
	{
		AlgebraBuffer alg = new AlgebraBuffer(6);

		int[] product = {
			0, 0, 0, 0, 0, 0,
			0, 0, 0, 1, 0, 0,
			0, 0, 0, 0, 2, 1, 
			0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };
		alg.addOperation(new FunctionBuffer(6, 2, product));

		int[] meet = {
			0, 0, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0,
			0, 0, 2, 0, 0, 0,
			0, 0, 0, 3, 0, 0,
			0, 0, 0, 0, 4, 0,
			0, 0, 0, 0, 0, 5 };
		alg.addOperation(new FunctionBuffer(6, 2, meet));

		return alg;
	}

	public static void createGeneratingVectors(int edges, 
		ArrayList algebras, ArrayList generators)
	{
		algebras.clear();
		generators.clear();

		Algebra alg = createTuringAlgebra();

		for(int i = 0; i <= edges; ++i)
			generators.add(new ArrayList());

		for(int i = 0; i < 3; ++i)
		{
			Argument args = new SquareArgument(edges, 3);
			if( args.first() )
			do
			{
				algebras.add(alg);
				
				((List)generators.get(0)).add(new Integer(i));
				
				for(int j = 0; j < edges; ++j)
					((List)generators.get(1+j)).
						add(new Integer(args.args()[j] + 3));
			} while( args.next() );
		}
	}

	static void printNonzeroEntries(Function func, int zero, char symbol)
	{
		Functions.Binary op = new Functions.Binary(func);
		int size = func.size();
		
		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
				if( op.value(i,j) != zero )
					System.out.println("" + i + " " + symbol + " " + j +
						" = " +  op.value(i,j));
	}

	static void main(String[] args)
	{
		Algebra alg = createTuringAlgebra();
		FreeAlgebra free = new FreeAlgebra(alg, 3);
		
		System.out.println(free.size());
		int zero = new Functions.Binary(free.operations()[0]).value(0,0);
		UaWriter.out.print(free.generators());
		UaWriter.out.print(free);
		printNonzeroEntries(free.operations()[0], zero, '*');
		UaWriter.out.print(SemiLattices.covers(free.operations()[1]));
	}

	static void main2(String[] args)
	{
		ArrayList algebras = new ArrayList();
		ArrayList generators = new ArrayList();

		createGeneratingVectors(3, algebras, generators);
/*
		FreeAlgebra.removeFactor(algebras, generators, 26);
		FreeAlgebra.removeFactor(algebras, generators, 17);
		FreeAlgebra.removeFactor(algebras, generators, 8);
		
		FreeAlgebra.removeFactor(algebras, generators, 21);
		FreeAlgebra.removeFactor(algebras, generators, 18);
*/	
		SubProdAlgebra B = new SubProdAlgebra(algebras, generators);
		Functions.Binary op = new Functions.Binary(B.operations()[0]);

		for(int i = 0; i < B.size(); ++i)
		{
//			int f[] = B.toConcreteElem(i);
			System.out.print("" + i + "\t" + 
				op.value(i, 66) + "\t" + 
				op.value(i, 67) + "\t" + 
				op.value(i, 68) + "\t");

//			for(int j = 0; j < f.length; ++j)
//				System.out.print(f[j]);
			
			System.out.println();
		}
	}
}
