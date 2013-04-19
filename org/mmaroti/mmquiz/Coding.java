
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;

public class Coding 
{
	static Field field = new FiniteField(3);

	static Matrix verifier = new Matrix(field, 4, 2);
	static Matrix word = new Matrix(field, 1, 4);

	static void initVerifier()
	{
		verifier.set(0, 0, 1);
		verifier.set(0, 1, 1);
		verifier.set(1, 0, 1);
		verifier.set(1, 1, 2);
		verifier.set(2, 0, 1);
		verifier.set(2, 1, 0);
		verifier.set(3, 0, 0);
		verifier.set(3, 1, 1);
	}
	
	static void randomize(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				int d = 0;
				if( Math.random() < 0.6 )
					d = (int)(Math.random() * matrix.getField().character());
				
				matrix.set(i, j, d);
			}
	}

	public static String printVector(Matrix vector)
	{
		if( vector.getRows() != 1 )
			throw new IllegalArgumentException("Not a vector");
		
		String s = "(";
		for(int i = 0; i < vector.getColumns(); ++i)
		{
			if( i != 0 )
				s += "\\ ";
			
			s += vector.get(0, i);
		}
		s += ")";
		
		return s;
	}
	
	public static String printMatrix(Matrix matrix)
	{
		String s = "";
		
		for(int i = 0; i < matrix.getRows(); ++i)
		{
			if( i > 0 )
				s += " \\\\ ";
			
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				if( j > 0 )
					s += "\\ ";
				
				s += matrix.get(i, j);
			}
		}
			
		return s;
	}
	
	public static void problemOne(boolean good)
	{
		for(int i = 0; i < 80; )
		{
			randomize(word);

			Matrix error = word.product(verifier);

			int leading;
			for(leading = 0; leading < error.getColumns(); ++leading)
			{
				if( error.get(0, leading) != 0 )
					break;
			}

			int errorBit = word.getColumns();
			if( leading < error.getColumns() )
			{
				error.multiply(field.inverse(error.get(0, leading)));
				
				for(errorBit = 0; ; ++errorBit)
				{
					if( error.equals(verifier.subMatrix(errorBit, errorBit+1, 0, verifier.getColumns())) )
							break;
				}
			}

			int guess = (int)(Math.random() * (word.getColumns() + 1));
			
			if( (guess != errorBit) ^ good )
			{
				System.out.println("\\item{" + (good ? "t" : "f") + "} Az $u = " + printVector(word) + "$ szóban "
						+ (guess == word.getColumns() ? "nincs hiba" : "az $u_" + guess + "$ bet\\H u hibás")
						+ ". % product = " + printVector(word.product(verifier)));

				++i;
			}
		}
	}

	static int distance(Matrix matrix, int row)
	{
		int d = 0;
		
		for(int i = 0; i < matrix.getColumns(); ++i)
			if( field.zero() != matrix.get(row, i) )
				d += 1;
		
		return d;
	}
	
	static boolean hasZeroRow(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			if( distance(matrix, i) == 0 )
				return true;
		
		return false;
	}
	
	public static void problemTwo(boolean good)
	{
		Field field = new FiniteField(2);
		Matrix gen = new Matrix(field, 2, 6);

		Matrix ones = new Matrix(field, 1, 2);
		ones.set(0, 0, 1);
		ones.set(0, 1, 1);
		
		for(int i = 0; i < 60; )
		{
			randomize(gen);
			
			Matrix m = new Matrix(gen);
			m.doGaussElimination();
			if( hasZeroRow(m) )
				continue;

			int d0 = distance(gen, 0);
			int d1 = distance(gen, 1);
			int d2 = distance(ones.product(gen), 0);
			
			int d = d0;
			if( d1 < d )
				d = d1;
			if( d2 < d )
				d = d2;

			int guess = 1 + (int)(Math.random() * 2);
			if( (guess != d) ^ good )
			{
				System.out.println("\\item{" + (good ? "t" : "f") + "}  $G = \\begin{pmatrix} " + printMatrix(gen) + " \\end{pmatrix}$, " +
						" $d(C) = " + guess + "$.");

				++i;
			}
		}
	}
	
	
	public static void main(String[] args)
	{
		initVerifier();
//		problemOne(true);
//		problemOne(false);
		problemTwo(true);
		problemTwo(false);
	}
	
}
