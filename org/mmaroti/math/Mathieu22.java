/**
 *	Copyright (C) Miklos Maroti, 2008
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

package org.mmaroti.math;

import java.util.*;

public class Mathieu22 
{
	public static final int N = 22;
	public static final int ELEMENTS = 443520;
	public static final int MATCH_PREFIX = 5;

	public byte[][] permutations = new byte[ELEMENTS][];

	public int[] inverse = new int[ELEMENTS];
	public int[] lookup = new int[ N*N*N*N*N ];

	/**
	 * h = conjugacy[g] is the element for which product(inverse[h],g,h) is the
	 * first element of the conjugacy class of g. These first elements are
	 * 0, 1, 3, 52, 63, 65, 1017, 1023, 20209, 20214, 21178, 21179, and only
	 * the last four has an edge to 0.
	 */
	public int[] conjugacy = new int[ELEMENTS];

	public boolean edge(int first, int second)
	{
		byte[] p = permutations[first];
		byte[] q = permutations[second];

		for(int i = 0; i < N; ++i)
			if( p[i] == q[i] )
				return false;

		return true;
	}

	public void triangleCount(int second)
	{
		int count = 0;
		
		for(int i = 0; i < ELEMENTS; ++i)
			if( edge(0,i) && edge(second, i) )
				++count;
		
		System.out.println("# of triangles extending 0, " + second + " is "+ count);
	}
	
	public void squareCount(int second)
	{
		long count = 0;
		
		for(int i = 0; i < ELEMENTS; ++i)
		{
			if( i % 100 == 0 )
				System.out.println(i + " ");
			
			if( edge(0,i) && edge(second, i) )
				for(int j = 0; j < ELEMENTS; ++j)
					if( edge(0,j) && edge(second,j) && edge(i,j) )
						++count;
		}
		
		System.out.println("# of squares extending 0, " + second + " is " + count);
	}
	
	public void findMaxClique(int second)
	{
		int[] clique = new int[N];
		clique[0] = 0;
		clique[1] = second;

		int i;
		outer: for(i = 2; i < N; ++i)
		{
			trial: for(int t = 0; t < 5000; ++t)
			{
				int a = (int)(ELEMENTS * Math.random());

				for(int j = 0; j < i; ++j)
					if( ! edge(a,clique[j]) )
						continue trial;
				
				clique[i] = a;
				continue outer;
			}

			scan: for(int a = 0; a < ELEMENTS; ++a)
			{
				for(int j = 0; j < i; ++j)
					if( ! edge(a,clique[j]) )
						continue scan;
				
				clique[i] = a;
				continue outer;
			}
		
			break;
		}

		System.out.println("max clique of size " + i + ":");
		for(int j = 0; j < i; ++j)
			System.out.println(clique[j] + ":\t" + toString(permutations[clique[j]]));
		System.out.println();
		
/*
		System.out.print("max clique of size " + i + ":");
		for(int j = 0; j < i; ++j)
			System.out.print(" " + clique[j]);

		System.out.println();
*/
	}
	
	public static void main(String[] _)
	{
		long time = System.currentTimeMillis();

		Mathieu22 group = new Mathieu22();
		group.construct();
		// 20209, 20214, 21178, 21179		

		group.triangleCount(21179);
//		group.stabilizer(21179);
//		System.out.println(toCycle(group.permutations[21179]));
		
//		for(int i = 0; i < 1000; ++i)
//			group.findMaxClique(21178);
		
		time = System.currentTimeMillis() - time;
		System.out.println("elapsed time: " + time);
	}
	
	public int product(int first, int second)
	{
		byte[] p = permutations[first];
		byte[] q = permutations[second];
		
		return lookup[ p[q[0]] + p[q[1]]*N + p[q[2]]*N2 + p[q[3]]*N3 + p[q[4]]*N4 ];
	}
	
	public int product(int first, int second, int third)
	{
		byte[] p = permutations[first];
		byte[] q = permutations[second];
		byte[] r = permutations[third];
		
		return lookup[ p[q[r[0]]] + p[q[r[1]]]*N + p[q[r[2]]]*N2 + p[q[r[3]]]*N3 + p[q[r[4]]]*N4 ];
	}
	
	public static String toString(byte[] perm)
	{
		String s = Byte.toString(perm[0]);

		for(int i = 1; i < N; ++i)
			s += " " + Byte.toString(perm[i]);

		return s;
	}
	
	public static String toCycle(byte[] perm)
	{
		String s = new String();
		boolean[] visited = new boolean[24];
		
		for(int i = 0; i < N; ++i)
			if( perm[i] != i && ! visited[i] )
			{
				int a = i;
				
				visited[a] = true;
				s += "(" + a;
				a = perm[a];
				
				do
				{
					visited[a] = true;
					s += " " + a;
					a = perm[a];
				} while( a != i );
				
				s += ")";
			}
		
		if( s.isEmpty() )
			s = "id";
		
		return s;
	}
	
	public void conjugacyClasses()
	{
		Arrays.fill(conjugacy, -1);

		for(int elem = 0; elem < ELEMENTS; ++elem)
		{
			if( conjugacy[elem] >= 0 )
				continue;
			
			@SuppressWarnings("unused")
			int count = 0;
			
			for(int i = 0; i < ELEMENTS; ++i)
			{
				int a = product(i, elem, inverse[i]);
				if( conjugacy[a] < 0 )
				{
					conjugacy[a] = i;
					++count;
				}
			}

//			System.out.println("size of conjugacy class of " + elem + " is " + count 
//					+ ", edge from 0: " + edge(0, elem));
		}
	}

	public void stabilizer(int second)
	{
		int count = 0;
		
		for(int i = 0; i < ELEMENTS; ++i)
		{
			if( second == product(inverse[i], second, i) )
				++count;
		}
		
		System.out.println("size of stabilizer of " + second + " is " + count);
	}
	
	/**
	 * Constructs the permutations of the Mathieu group
	 */
	public void construct()
	{
		Generator generator = new Generator();
		generator.generate();
		conjugacyClasses();
	}

	static final int N2 = N * N;
	static final int N3 = N2 * N;
	static final int N4 = N3 * N;

	private class Generator
	{
		void generate()
		{
			construct();
			root = null;
			sort();
			createLookup();
			createInverse();
		}
		
		void product(byte[] first, byte[] second, byte[] result)
		{
			int i = N;
			while( --i >= 0 )
				result[i] = second[first[i]];
		}

		@SuppressWarnings("unused")
		void inverse(byte[] element, byte[] result)
		{
			byte i = N;
			while( --i >= 0 )
				result[element[i]] = i;
		}
	
		int elements;

		class Node
		{
			Node[] subnodes = new Node[N];
			int index = -1;
		}
	
		Node root = new Node();
		
		int getIndex(byte[] permutation)
		{
			Node node = root;
			
			for(int i = 0; i < MATCH_PREFIX; ++i)
			{
				Node subnode = node.subnodes[permutation[i]];
				if( subnode != null )
					node = subnode;
				else
					node = node.subnodes[permutation[i]] = new Node();
			}
			
			if( node.index < 0 )
			{
				permutations[elements] = permutation.clone();
				node.index = elements++;
			}
				
			return node.index;
		}
		
		void construct()
		{
			byte[] result = new byte[N];
	
			result = new byte[] { 12, 7, 15, 11, 4, 21, 16, 1, 9, 8, 13, 3, 0, 10, 14, 2, 6, 17, 18, 19, 20, 5 };
			getIndex(result);
			
			result = new byte[] { 21, 17, 20, 12, 11, 10, 14, 13, 8, 7, 6, 4, 1, 19, 5, 15, 18, 3, 16, 9, 0, 2 };
			getIndex(result);
			
			for(int i = 0; elements < ELEMENTS; ++i)
			{
				for(int j = 0; j <= i; ++j)
				{
					product(permutations[i], permutations[j], result);
					getIndex(result);
	
					// it is faster to finish the generation when these are omitted
					// product(permutations[j], permutations[i], result);
					// getIndex(result);
				}
			}
		}
	
		int prefix(byte[] a, byte[] b)
		{
			int i = 0;
	
			while( a[i] == b[i] && ++i < N )
				;
			
			return i;
		}
		
		void sort()
		{
			Arrays.sort(permutations, 0, ELEMENTS, new Comparator<byte[]>()
			{
				public int compare(byte[] a, byte[] b)
				{
					for(int i = 0; i < N; ++i)
						if( a[i] != b[i] )
							return a[i] - b[i];
					
					return 0;
				}
			});
			
			int max = -1;
			
			for(int i = 0; i < N-1; ++i)
			{
				int m = prefix(permutations[i], permutations[i+1]);
				if( m > max )
					max = m;
			}
			
			// System.out.println("max prefix: " + max);
		}
		
		void createLookup()
		{
			Arrays.fill(lookup, -1);
			
			for(int i = 0; i < ELEMENTS; ++i)
			{
				byte[] p = permutations[i];
				
				lookup[ p[0] + p[1]*N + p[2]*N2 + p[3]*N3 + p[4]*N4 ] = i;
			}
		}
		
		void createInverse()
		{
			byte[] q = new byte[N];
			
			for(int i = 0; i < ELEMENTS; ++i)
			{
				byte[] p = permutations[i];
				
				for(byte j = 0; j < N; ++j)
					q[p[j]] = j;
				
				inverse[i] = lookup[ q[0] + q[1]*N + q[2]*N2 + q[3]*N3 + q[4]*N4 ];
			}
		}
	}
}
