/**
 *	Copyright (C) Miklos Maroti, 2007-2008
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

import org.mmaroti.ua.csp.*;

import java.util.*;
import java.io.*;

public class StrategyForMaltsev
{
	static int MAX_GENERATORS = 3;
	static int POTATO_COUNT = 5;
	static int POTATO_LOG_SIZE = 2;
	static int FIX_COUNT = 3;
	
	static int[] generators;
	static Problem problem;
	
	static void createGenerators()
	{
		generators = new int[1 + (int)(Math.random() * MAX_GENERATORS)];
		
		for(int i = 0; i < generators.length; ++i)
			generators[i] = (int)(Math.random() * (1 << (POTATO_COUNT * POTATO_LOG_SIZE)));
	}

	static void printGenerators()
	{
		for(int i = 0; i < generators.length; ++i)
		{
			System.out.print("generator " + i + ": ");
			for(int j = 0; j < POTATO_COUNT; ++j)
			{
				if( j != 0 )
					System.out.print(",");
				
				int a = (generators[i] >> (j * POTATO_LOG_SIZE)) & ((1 << POTATO_LOG_SIZE) - 1);
				System.out.print(a);
			}
			System.out.println();
		}
	}
	
	static int[] getProjectionOfGenerators(int x, int y)
	{
		int[] result = new int[generators.length];
		for(int k = 0; k < result.length; ++k)
		{
			int a = (generators[k] >> (x * POTATO_LOG_SIZE)) & ((1 << POTATO_LOG_SIZE) - 1);
			int b = (generators[k] >> (y * POTATO_LOG_SIZE)) & ((1 << POTATO_LOG_SIZE) - 1);
			result[k] = (a << POTATO_LOG_SIZE) | b; 
		}
		return result;
	}

	static int[] getGeneratedSubspace(int[] vectors)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		
		for(int i = 0; i < vectors.length; ++i)
		{
			if( ! list.contains(vectors[i]) )
				list.add(vectors[i]);
		}

		int i = 0;
		while( i < list.size() )
		{
			int a = list.get(i);
			
			int j = i + 1;
			while( j < list.size() )
			{
				int b = list.get(j);
				
				int c = a ^ b;
				if( ! list.contains(c) )
					list.add(c);

				++j;
			}
			++i;
		}

		int[] result = new int[list.size()];
		for(i = 0; i < result.length; ++i)
			result[i] = list.get(i);

		return result;
	}
	
	static RelationTuples getProjectionRelation(int x, int y)
	{
		int[] v = getGeneratedSubspace(getProjectionOfGenerators(x,y));
		
		int[][] vectors = new int[v.length][];
		for(int i = 0; i < v.length; ++i)
		{
			int a = (v[i] >> POTATO_LOG_SIZE) & ((1 << POTATO_LOG_SIZE) - 1);
			int b = v[i] & ((1 << POTATO_LOG_SIZE) - 1);
			vectors[i] = new int[] { a, b }; 
		}

		return new RelationTuples(2, vectors);
	}
	
	static void createProblem()
	{
		problem = new Problem();
		
		for(int i = 0; i < POTATO_COUNT; ++i)
			problem.addVariable("v"+i, 1 << POTATO_LOG_SIZE);
		
		for(int i = 0; i < POTATO_COUNT; ++i)
			for(int j = i+1; j < POTATO_COUNT; ++j)
			{
				problem.addConstraint(new String[] { "v"+i, "v"+j }, 
						getProjectionRelation(i, j));
			}
	}
	
	static void printProblem()
	{
		for(int i = 0; i < POTATO_COUNT; ++i)
			for(int j = i+1; j < POTATO_COUNT; ++j)
			{
				System.out.print("relation " + i + " " + j + ": ");
				System.out.println(getProjectionRelation(i, j).printTuples());
			}
	}

	static int[] restriction;
	
	static void createRestriction()
	{
		restriction = new int[FIX_COUNT];

		for(int i = 0; i < restriction.length; ++i)
		{
			int[] values = problem.getValue("v"+i);
			restriction[i] = values[(int)(Math.random() * values.length)];
			problem.addUnaryConstraint("v"+i, new int[] { restriction[i] });
		}
	}

	static void printRestriction()
	{
		System.out.print("restriction: ");
		for(int i = 0; i < restriction.length; ++i)
		{
			if( i != 0 )
				System.out.print(",");
		
			System.out.print(restriction[i]);
		}
		System.out.println();
	}
	
	static void printSolution()
	{
		problem.printValues(new PrintWriter(System.out, true));
	}
	
	public static void main(String[] args)
	{
		for(int i = 0; i < 10000; ++i)
		{
			createGenerators();
			createProblem();
			problem.runOneConsistency();
			createRestriction();
			problem.runOneConsistency();
			
			if( ! problem.hasNoSolution() )
			{
				Problem problem2 = new Problem(problem);
				if( ! problem2.findOneSolution() )
				{
					printGenerators();
					printProblem();
					printRestriction();
					printSolution();
					break;
				}
			}
		}
	}
}
