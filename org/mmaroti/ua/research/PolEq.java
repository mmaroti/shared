/**
 *	Copyright (C) Miklos Maroti, 2005
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

package org.mmaroti.ua.research;

import java.util.*;
import org.mmaroti.ua.math.*;

/**
 * 
 * @author mmaroti
 */
public class PolEq
{
	/**
	 * The underlying field of the matrices
	 */
//	protected Field field = new Reals();
	protected Field field = new FiniteField(3);
	
	/*
	 * The number of variables in the polynom.
	 * The number of p_ij variables then n squared.
	 */
	public int N = 2;

	/*
	 * Contains the set of all possible indices.
	 */
	public int INDEX_SET = (1 << N) - 1;

	/**
	 * The monom that contains all p_ij variables.
	 */
	public int MONOM_SET = (1 << (N*N)) - 1;

	/**
	 * Returns a rectangular block of ones.
	 * @param a the set of first indices (<= INDEX_SET)
	 * @param b the set of second indices (<= INDEX_SET)
	 * @return the monom consisting of a block of ones (<= MONOM_SET)
	 */
	public int block(int a, int b)
	{
		int r = 0;
		
		int i = N*N;
		while( --i >= 0 )
		{
			if( ((1 << (i/N)) & a) != 0 && ((1 << (i%N)) & b) != 0 )
				r += (1 << i);
		}
		
		return r;
	}
	
	/**
	 * Returns the value array of integers sorted
	 * by the order array.
	 */
	public static void sort(int[] order, int[] value)
	{
		long[] d = new long[value.length];

		for(int i = 0; i < value.length; ++i)
			d[i] = (((long)order[i]) << 32) + value[i];
			
		Arrays.sort(d);
		
		for(int i = 0; i < value.length; ++i)
		{
			value[i] = (int)d[i];
			order[i] = (int)(d[i] >> 32);
		}
	}

	/**
	 * Returns the list of all monoms in a good order,
	 * where blocks come first by the number of ones,
	 * then come the others.
	 */
	public int[] getAllMonoms()
	{
		int[] monoms = new int[MONOM_SET+1];
		int[] order = new int[MONOM_SET+1];
		
		for(int i = 0; i < monoms.length; ++i)
		{
			monoms[i] = i;
			order[i] = 1000 + weight(i); 
		}
		
		order[block(0,0)] = 0;
		for(int a = 1; a <= INDEX_SET; ++a)
			for(int b = 1; b <= INDEX_SET; ++b)
			{
				int c = block(a,b);
				order[c] = weight(c);
			}
		
		sort(order, monoms);
		
		return monoms;
	}
	
	/**
	 * Returns the number of 1 bits in the given integer
	 * (at most 32).
	 */
	public static int weight(int a)
	{
		int c = 0;

		if( a < 0 )
		{
			++c;
			a &= Integer.MAX_VALUE;
		}
		
		while( a != 0 )
		{
			c += (a & 1);
			a >>= 1;
		}
		
		return c;
	}

	/**
	 * Returns the value of the monom evaluated at the given block
	 */
	public int evaluate(int monom, int block)
	{
		if( (weight(monom & block) % 2) == 0 )
			return field.unit();
		else
			return field.negative(field.unit());
	}

	/**
	 * Contains the set of all blocks in a good order
	 */
	protected int[] blocks;

	/**
	 * Contains the set of all monoms in a good order
	 */
	protected int[] monoms;
	
	/**
	 * Contains the evaluation matrix
	 */
	protected Matrix original;

	public PolEq()
	{
		monoms = getAllMonoms();
		blocks = new int[1 + INDEX_SET*INDEX_SET];
		System.arraycopy(monoms, 0, blocks, 0, blocks.length);
		
		original = new Matrix(field, blocks.length, monoms.length + blocks.length);
		
		for(int i = 0; i < blocks.length; ++i)
		{
			for(int j = 0; j < monoms.length; ++j)
				original.set(i,j, evaluate(monoms[j], blocks[i]));
			
			original.set(i, monoms.length + i, 1);
		}
		
		solved = new Matrix(original);
		
		for(int i = 0; i < blocks.length; ++i)
			solved.normalizeColumn(i,i); 
	}

	/**
	 * Contains the sloved matrix with inverses
	 */
	protected Matrix solved;

	public void printMonom(int a)
	{
		for(int i = 0; i < N; ++i)
		{
			for(int j = 0; j < N; ++j)
				System.out.print((a & (1 << (i*N+j))) != 0 ? "1" : "0");
			System.out.println();
		}
	}
	
	public void printMonoms(int a, int b)
	{
		for(int i = 0; i < N; ++i)
		{
			for(int j = 0; j < N; ++j)
				System.out.print((a & (1 << (i*N+j))) != 0 ? "1" : "0");
			System.out.print("  ");

			for(int j = 0; j < N; ++j)
				System.out.print((b & (1 << (i*N+j))) != 0 ? "1" : "0");
			System.out.println();
		}
	}
	
	public String monomToString(int a)
	{
		String s = new String();
		
		for(int i = 0; i < N; ++i)
		{
			for(int j = 0; j < N; ++j)
			if( (a & (1<< (i*N+j))) != 0 )
				s += "p_{" + i + "" + j + "}";
		}
		
		if( s.length() == 0 )
			s = "1";
		
		return s;
	}

	public int monomToColumn(int monom)
	{
		for(int i = 0; i < monoms.length; ++i)
			if( monoms[i] == monom )
				return i;
		
		throw new IllegalArgumentException();
	}

	public String identityToString(int column)
	{
		String s = monomToString(monoms[column]) + " = ";
		
		int z = field.zero();
		int p1 = field.unit();
		int n1 = field.negative(p1);
		
		boolean first = true;
		for(int i = 0; i < blocks.length; ++i)
		{
			int a = solved.get(i, column); 

			if( a == z )
				continue;
			else if( a == p1 )
			{
				if( !first )
					s += "+";
			}
			else if( a == n1 )
				s += "-";
			else
				s += "+" + field.getElement(a).toString() +"*";
			
			first = false;
			s += monomToString(blocks[i]);
		}
		
		return s;
	}

	public void printIdentity(int monom)
	{
		System.out.println(identityToString(monomToColumn(monom)));
	}
	
	public void print(Matrix matrix)
	{
		System.out.print("blocks:");
		for(int i = 0; i < blocks.length; ++i)
			System.out.print(" " + blocks[i]);
		System.out.println();

		System.out.print("monoms:");
		for(int i = 0; i < monoms.length; ++i)
			System.out.print(" " + monoms[i]);
		System.out.println();
		
		System.out.println("matrix:");
		matrix.print();
	}

	protected Matrix sum;

	public void analize()
	{
		print(original);
		print(solved);

		Matrix m = solved.transpose();
		m = m.product(original);

		System.out.println();
		m.print();

/*
		sum = new Matrix(new FiniteField(3), blocks.length, blocks.length);
		for(int i = 0; i < blocks.length; ++i)
			for(int j = 0; j < blocks.length; ++j)
				sum.set(i,j,original.get(i,j));

		normalize();
		
		for(int i = 0; i < blocks.length; ++i)
			for(int j = 0; j < blocks.length; ++j)
			{
				int a = sum.get(i,j);
				a = sum.getField().sum(a, matrix.get(i, monoms.length + j));
				sum.set(i,j,sum.getField().negative(a));
			}

*/		
/*		
		System.out.println();
		sum.print();
		System.out.println();

		for(int i = 0; i < blocks.length; ++i)
			for(int j = 0; j < blocks.length; ++j)
				if( sum.get(i,j) != 0 )
				{
					printMonoms(monoms[i], monoms[j]);
					System.out.println();
				}
*/
//		printIdentity(1+256);
//		printIdentity(1+2+4+8+32+64+128+256);
	}
	
	public static void main(String[] _)
	{
		PolEq poleq = new PolEq();
		poleq.analize();
	}
}
