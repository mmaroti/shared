/**
 * Copyright (C) Miklos Maroti, 2010
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

package org.mmaroti.ua.math;

public class MaltsevStrategy
{
	static final int POTATO_SIZE = 4;
	static final int POTATO_COUNT = 4;

	static class Subset
	{
		boolean[] values = new boolean[POTATO_SIZE];
		
		static Subset full()
		{
			Subset s = new Subset();

			for(int i = 0; i < POTATO_SIZE; ++i)
				s.values[i] = true;

			return s;
		}
		
		static Subset singleton(int i)
		{
			Subset s = new Subset();
			
			s.values[i] = true;
			
			return s;
		}
		
		Subset intersection(Subset s)
		{
			Subset t = new Subset();

			for(int i = 0; i < POTATO_SIZE; ++i)
				t.values[i] = values[i] && s.values[i];
			
			return t;
		}
		
		Subset union(Subset s)
		{
			Subset t = new Subset();

			for(int i = 0; i < POTATO_SIZE; ++i)
				t.values[i] = values[i] || s.values[i];
			
			return t;
		}
		
		boolean equals(Subset s)
		{
			for(int i = 0; i < POTATO_SIZE; ++i)
				if( values[i] != s.values[i] )
					return false;
	
			return true;
		}
	}
	
	/**
	 * Binary relation
	 */
	static class Relation
	{
		boolean[][] tuples;

		Relation()
		{
			tuples = new boolean[POTATO_SIZE][];
			for(int i = 0; i < POTATO_SIZE; ++i)
				tuples[i] = new boolean[POTATO_SIZE];
		}

		void clear()
		{
			for(int i = 0; i < POTATO_SIZE; ++i)
				for(int j = 0; j < POTATO_SIZE; ++j)
					tuples[i][j] = false;
		}
		
		Relation inverse()
		{
			Relation rel = new Relation();
			
			for(int i = 0; i < POTATO_SIZE; ++i)
				for(int j = 0; j < POTATO_SIZE; ++j)
					rel.tuples[j][i] = tuples[i][j];

			return rel;
		}
		
		Relation composedWith(Relation r)
		{
			Relation rel = new Relation();
			
			for(int i = 0; i < POTATO_SIZE; ++i)
				for(int j = 0; j < POTATO_SIZE; ++j)
					for(int k = 0; k < POTATO_SIZE; ++k)
					{
						if( tuples[i][k] && r.tuples[k][j] )
							rel.tuples[i][j] = true;
					}
			
			return rel;
		}
		
		boolean isSubsetOf(Relation r)
		{
			for(int i = 0; i < POTATO_SIZE; ++i)
				for(int j = 0; j < POTATO_SIZE; ++j)
					if( tuples[i][j] && ! r.tuples[i][j] )
						return false;
			
			return true;
		}
		
		int getTupleCount()
		{
			int a = 0;
			
			for(int i = 0; i < POTATO_SIZE; ++i)
				for(int j = 0; j < POTATO_SIZE; ++j)
					if( tuples[i][j] )
						a++;
			
			return a;
		}
		
		Subset imageOf(Subset s)
		{
			Subset t = new Subset();
			
			for(int i = 0; i < POTATO_SIZE; ++i)
				if( s.values[i] )
					for(int j = 0; j < POTATO_SIZE; ++j)
						if( tuples[i][j] )
							t.values[j] = true;
			
			return t;
		}
	}

	Relation[][] relations;
	
	MaltsevStrategy()
	{
		relations = new Relation[POTATO_COUNT][];
		for(int i = 0; i < POTATO_COUNT; ++i)
		{
			relations[i] = new Relation[POTATO_COUNT];
			for(int j = 0; j < POTATO_COUNT; ++j)
				relations[i][j] = new Relation();
		}
	}
	
	void setRelation(int i, int j, Relation r)
	{
		Relation s = relations[i][j];
		Relation t = relations[j][i];
		
		for(int k = 0; k < POTATO_SIZE; ++k)
			for(int l = 0; l < POTATO_SIZE; ++l)
			{
				s.tuples[k][l] = r.tuples[k][l];
				t.tuples[l][k] = r.tuples[k][l];
			}
	}

	void setSubset(int i, Subset s)
	{
		Relation r = relations[i][i];

		r.clear();
		for(int j = 0; j < POTATO_SIZE; ++j)
			r.tuples[j][j] = s.values[j];
	}
	
	boolean isTwoThree()
	{
		for(int i = 0; i < POTATO_COUNT; ++i)
			for(int j = 0; j < POTATO_COUNT; ++j)
				for(int k = 0; k < POTATO_COUNT; ++k)
					if( ! relations[i][k].isSubsetOf(relations[i][j].composedWith(relations[j][k])) )
						return false;
		
		return true;
	}

	boolean isOneTwo()
	{
		for(int i = 0; i < POTATO_COUNT; ++i)
			for(int j = 0; j < POTATO_COUNT; ++j)
				if( ! relations[i][j].isSubsetOf(relations[i][i].composedWith(relations[i][j])) )
					return false;
		
		return true;
	}

	void makeOneTwo()
	{
		outer: for(;;)
		{
			for(int i = 0; i < POTATO_COUNT; ++i)
				for(int j = 0; j < POTATO_COUNT; ++j)
				{
					Relation r = relations[i][i].composedWith(relations[i][j]);
					if( ! relations[i][j].isSubsetOf(r) )
					{
						setRelation(i,j,r);
						continue outer;
					}
				}
			
			return;
		}
	}
	
	int getElementCount()
	{
		int a = 0;
		
		for(int i = 0; i < POTATO_COUNT; ++i)
			a += relations[i][i].getTupleCount();
		
		return a;
	}
	
	int getEdgeCount()
	{
		int a = 0;

		for(int i = 1; i < POTATO_COUNT; ++i)
			for(int j = 0; j < i; ++j)
				a += relations[i][j].getTupleCount();

		return a;
	}

	Subset imageOf(Subset s, int[] path)
	{
		for(int i = 0; i < path.length-1; ++i)
		{
			Relation r = relations[path[i]][path[i+1]];
			s = r.imageOf(s);
		}
		
		return s;
	}

	Subset closureOf(Subset s, int[] closedPath)
	{
		Subset t = s;
		
		do
		{
			s = t;
			
			for(int i = 0; i < closedPath.length; ++i)
			{
				int a = closedPath[i];
				int b = i+1 < closedPath.length ? i+1 : 0;
			
				Relation r = relations[a][b];
				t = r.imageOf(t);
			}

			t = t.union(s);
		} while( ! t.equals(s) );
		
		return t;
	}
	
	void printStats()
	{
		System.out.println("Element count: " + getElementCount());
		System.out.println("Edge count: " + getEdgeCount());
		System.out.println("(1,2)-strategy: " + isOneTwo());
		System.out.println("(2,3)-strategy: " + isTwoThree());
	}
	
	public static void main(String[] args)
	{
		MaltsevStrategy strategy = new MaltsevStrategy();
		
		Relation identity = new Relation();
		Relation alpha = new Relation();
		Relation beta = new Relation();
		Relation gamma = new Relation();
		Relation delta = new Relation();
		
		for(int i = 0; i < POTATO_SIZE; ++i)
			for(int j = 0; j < POTATO_SIZE; ++j)
			{
				int a = (i & 0x1) >> 0;
				int b = (i & 0x2) >> 1;

				int c = (j & 0x1) >> 0;
				int d = (j & 0x2) >> 1;
				
				identity.tuples[i][j] = (i == j);
				alpha.tuples[i][j] = (a == c);
				beta.tuples[i][j] = (b == d);
				gamma.tuples[i][j] = ((a ^ b) == (c ^ d));
				delta.tuples[i][j] = !gamma.tuples[i][j];
			}
		
		strategy.setRelation(0, 0, identity);
		strategy.setRelation(1, 1, identity);
		strategy.setRelation(2, 2, identity);
		strategy.setRelation(3, 3, identity);
		
		strategy.setRelation(0, 1, alpha);
		strategy.setRelation(2, 3, alpha);
		strategy.setRelation(0, 2, beta);
		strategy.setRelation(1, 3, beta);
		strategy.setRelation(0, 3, gamma);
		strategy.setRelation(1, 2, delta);

		Subset s = new Subset();
		s.values[0] = true;
		s.values[1] = true;
		strategy.setSubset(0, s);
		strategy.makeOneTwo();
		
		strategy.printStats();
	}
}
