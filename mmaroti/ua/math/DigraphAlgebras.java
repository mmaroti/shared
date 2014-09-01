package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 *	Copyright (C) 2001 Jarda Jezek
 */

import java.util.*;
import mmaroti.ua.alg.*;

public class DigraphAlgebras
{
	public static void main(String[] args)
	{
		int[] partial = {
			 0,-1,-1,
			-1, 0,-1,
			-1,-1, 0 };

		List<Algebra> algebras = createAllCompletions(new FunctionBuffer(3, 2, partial));
		List<Algebra> simples = new ArrayList<Algebra>();

		int c = 0;
		Iterator<Algebra> iter = algebras.iterator();
		while( iter.hasNext() )
		{
			Algebra alg = iter.next();

			if( isSimple(alg) )
			{
				if( c++ < 22 )
					simples.add(alg);
			}
		}

		List<List<Integer>> generators = new ArrayList<List<Integer>>();
		for(int i = 0; i < 3; ++i)
		{
			List<Integer> function = new ArrayList<Integer>();
			Integer n = new Integer(i);

			int j = simples.size();
			while( --j >= 0 )
				function.add(n);

			generators.add(function);
		}

//		FreeAlgebra free = new FreeAlgebra(simples, generators);
	}

	public static DecisionDiagram.Node prod(ComplexProdAlgebra complex, DecisionDiagram.Node a, DecisionDiagram.Node b)
	{
		DecisionDiagram.Node[] args = { a, b };
		return complex.operationValue(0, args);
	}

	public static DecisionDiagram.Node spike(ComplexProdAlgebra complex, int a)
	{
		int b[] = new int[complex.algebras().length];
		Arrays.fill(b, a);

		return complex.spike(b);
	}

	public static void checkCommutativity()
	{
		int[] partial = {
			 0,-1,
			-1, 0 };
		
		List<Algebra> algebras = createAllCompletions(new FunctionBuffer(2, 2, partial));
		ComplexProdAlgebra complex = new ComplexProdAlgebra(algebras);
		
		DecisionDiagram.Node full = complex.emptySet();
		for(int i = 0; i < 2; ++i)
			full = complex.union(full, spike(complex, i));

		full = prod(complex, full, full);
		full = prod(complex, full, full);

		DecisionDiagram.Node x = spike(complex, 0);
		DecisionDiagram.Node y = spike(complex, 1);
		DecisionDiagram.Node xy = prod(complex, x, y);
		DecisionDiagram.Node yx = prod(complex, y, x);

		for(int i = 0; i < 10; ++i)
		{
			if( complex.union(xy, yx) == xy )
				break;

			System.out.println(i);

			xy = prod(complex, xy, full);
			xy = prod(complex, full, xy);
		}
	}

	public static void checkCompability1()
	{
		int[] partial = {
			 0,-1,-1,
			-1, 0,-1,
			-1,-1, 0 };

		List<Algebra> algebras = createAllCompletions(new FunctionBuffer(3, 2, partial));
		ComplexProdAlgebra complex = new ComplexProdAlgebra(algebras);
		
		DecisionDiagram.Node full = complex.emptySet();
		for(int i = 0; i < 3; ++i)
			full = complex.union(full, spike(complex, i));

		full = prod(complex, full, full);
		full = prod(complex, full, full);
//		full = prod(complex, full, full);
		System.out.println("full: " + full.count);

		DecisionDiagram.Node x = spike(complex, 0);
		DecisionDiagram.Node y = spike(complex, 1);
		DecisionDiagram.Node z = spike(complex, 2);
		DecisionDiagram.Node xyz = prod(complex, prod(complex, x, y), z);
		DecisionDiagram.Node yz = prod(complex, y, z);

		int i;
		for(i = 0; i < 5; ++i)
		{
			if( complex.union(xyz, yz) == yz )
				break;

			yz = prod(complex, yz, full);
			System.out.println("yz = yz * full: " + yz.count);

			if( complex.union(xyz, yz) == yz )
				break;
			
			yz = prod(complex, full, yz);
			System.out.println("yz = full * yz: " + yz.count);
		}
		if( i < 5 )
			System.out.println("found");
	}

	public static void checkCompability2()
	{
		int[] partial = {
			 0,-1,-1,
			-1, 0,-1,
			-1,-1, 0 };

		List<Algebra> algebras = createAllCompletions(new FunctionBuffer(3, 2, partial));
		ComplexProdAlgebra complex = new ComplexProdAlgebra(algebras);
		
		DecisionDiagram.Node full = complex.emptySet();
		for(int i = 0; i < 3; ++i)
			full = complex.union(full, spike(complex, i));

		full = prod(complex, full, full);
		full = prod(complex, full, full);
//		full = prod(complex, full, full);
		System.out.println("full: " + full.count);

		DecisionDiagram.Node x = spike(complex, 0);
		DecisionDiagram.Node y = spike(complex, 1);
		DecisionDiagram.Node z = spike(complex, 2);
		DecisionDiagram.Node zxy = prod(complex, z, prod(complex, x, y));
		DecisionDiagram.Node zy = prod(complex, z, y);

		int i;
		for(i = 0; i < 5; ++i)
		{
			if( complex.union(zxy, zy) == zy )
				break;
			
			zy = prod(complex, full, zy);
			System.out.println("zy = full * zy: " + zy.count);

			if( complex.union(zxy, zy) == zy )
				break;

			zy = prod(complex, zy, full);
			System.out.println("zy = zy * full: " + zy.count);
		}
		if( i < 5 )
			System.out.println("found");
	}

	public static void checkAssociativity()
	{
		int[] partial = {
			 0,-1,-1,
			-1, 0,-1,
			-1,-1, 0 };

		List<Algebra> algebras = createAllCompletions(new FunctionBuffer(3, 2, partial));
		ComplexProdAlgebra complex = new ComplexProdAlgebra(algebras);
		
		DecisionDiagram.Node full = complex.emptySet();
		for(int i = 0; i < 3; ++i)
			full = complex.union(full, spike(complex, i));

		full = prod(complex, full, full);
		full = prod(complex, full, full);
//		full = prod(complex, full, full);
		System.out.println("full: " + full.count);

		DecisionDiagram.Node x = spike(complex, 0);
		DecisionDiagram.Node y = spike(complex, 1);
		DecisionDiagram.Node z = spike(complex, 2);
		DecisionDiagram.Node x_yz = prod(complex, x, prod(complex, y, z));
		DecisionDiagram.Node xy_z = prod(complex, prod(complex, x, y), z);

		int i;
		for(i = 0; i < 5; ++i)
		{
			if( complex.union(x_yz, xy_z) == xy_z )
				break;
			
			xy_z = prod(complex, full, xy_z);
			System.out.println("xy_z = full * xy_z: " + xy_z.count);

			if( complex.union(x_yz, xy_z) == xy_z )
				break;

			xy_z = prod(complex, xy_z, full);
			System.out.println("xy_z = xy_z * full: " + xy_z.count);
		}
		if( i < 5 )
			System.out.println("found");
	}

	public static boolean isSimple(Algebra algebra)
	{
		if( algebra.size() != 3 )
			throw new IllegalArgumentException();

		Functions.Binary prod = new Functions.Binary(algebra.operations()[0]);

		for(int i = 0; i < 2; ++i)
			for(int j = i + 1; j < 3; ++j)
			{
				int k = 3 - i - j;

				int a = prod.value(i,k);
				int b = prod.value(j,k);
				if( a != b && (a == k || b == k) )
					continue;
				
				a = prod.value(k,i);
				b = prod.value(k,j);
				if( a != b && (a == k || b == k) )
					continue;
				
				return false;
			}
		
		return true;
	}
	
	public static AlgebraBuffer createAlgebra(FunctionBuffer relation)
	{
		int size = relation.size();
	
		FunctionBuffer op = new FunctionBuffer(size, 2);

		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
			{
				int a = relation.value(i,j) != 0 ? i : j;
				op.set(i,j,a);
			}

		AlgebraBuffer alg = new AlgebraBuffer(size);
		alg.addOperation(op);
		
		return alg;
	}
	
	public static List<Algebra> createAllCompletions(FunctionBuffer relation)
	{
		int size = relation.size();
		int[] indices = new int[size * size];
		
		int indexCount = 0;
		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
				if( i != j && relation.value(i,j) < 0 )
				{
					indices[indexCount++] = relation.index(i,j);
					relation.set(i,j,0);
				}
				
		int[] buffer = relation.buffer();
		List<Algebra> algebras = new ArrayList<Algebra>();

		int i;
		do
		{
			algebras.add(createAlgebra(relation));
			
			for(i = 0; i < indexCount && ++buffer[indices[i]] > 1 ; ++i)
				buffer[indices[i]] = 0;

		} while( i < indexCount );
		
		return algebras;
	}
	
	public static FreeAlgebra freeAlgebraOver(int[] partial)
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

		return new FreeAlgebra(algebras, generators);
	}
}
