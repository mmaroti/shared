/**
 *	Copyright (C) Miklos Maroti, 2007
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

package org.mmaroti.ua.csp;

import java.io.*;
import java.applet.Applet;
import java.util.*;

public class GraphPolyApplet extends Applet
{
	public static class Solver
	{
		Problem problem;

		public Solver(String[] variables, int size)
		{
			problem = new Problem();
			for( String variable : variables )
				problem.addVariable(variable, size);
		}
		
		public int getVariable(String name)
		{
			return problem.getVariable(name);
		}
		
		public void addConstraint(int variable, int[] values)
		{
			int[][] tuples = new int[values.length][];
			for(int i = 0; i < values.length; ++i)
				tuples[i] = new int[] { values[i] };
			
			Relation relation = new RelationTuples(1, tuples);
			String[] names = new String[] { problem.variables[variable].name };
			
			problem.addConstraint(names, relation);
		}

		public void addConstraint(int variable0, int variable1, int[][] tuples)
		{
			Relation relation = new RelationTuples(2, tuples);
			String[] names = new String[] { problem.variables[variable0].name, problem.variables[variable1].name };
			
			problem.addConstraint(names, relation);
		}
		
		public void runOneConsistency()
		{
			problem.runOneConsistency();
		}
		
		public void solve()
		{
			problem.findOneSolution();
		}

		public void printValues(PrintWriter writer)
		{
			problem.printValues(writer);
		}
	}
	
	private static final long serialVersionUID = 1L;

	int readElement(char c)
	{
		if( '0' <= c && c <= '9' )
			return c - '0';
		else if( 'a' <= c && c <= 'z' )
			return c - 'a' + 10;
		else
			throw new IllegalArgumentException("invalid element: '" + c+"'");
	}
	
	int[] readTuple(String tuple)
	{
		tuple = tuple.trim();
		
		int[] values = new int[tuple.length()];
		for(int i = 0; i < values.length; ++i)
			values[i] = readElement(tuple.charAt(i));
		
		return values;
	}
	
	String writeTuple(int[] tuple)
	{
		String s = new String();
		
		for(int i = 0; i < tuple.length; ++i)
		{
			int a = tuple[i];
			if( 0 <= a && a <= 9 )
				s += (char)('0' + a);
			else if( 10 <= a && a <= 10 + 'z' - 'a' )
				s += (char)('a' + a - 10);
			else
				throw new IllegalArgumentException("invalid integer in tuple");
		}
		
		return s;
	}

	int[][] readGraph(String graph)
	{
		ArrayList<int[]> list = new ArrayList<int[]>();
		
		StringTokenizer tokenizer = new StringTokenizer(graph, " \t");
		while( tokenizer.hasMoreTokens() )
		{
			int[] edge = readTuple(tokenizer.nextToken());
			if( edge.length != 2 )
				throw new IllegalArgumentException("edge must be a two-element tuple");
			
			list.add(edge);
		}

		// TODO: check for multiple edges
		
		int[][] edges = new int[list.size()][];
		edges = list.toArray(edges);
		
		return edges; 
	}
	
	int getMaxVertex(int[][] graph)
	{
		int max = 0;
		for(int i = 0; i < graph.length; ++i)
		{
			if( graph[i][0] > max )
				max = graph[i][0];
			
			if( graph[i][1] > max )
				max = graph[i][1];
		}
		return max;
	}
	
	int[] toIntArray(List<Integer> list)
	{
		int[] values = new int[list.size()];
		
		for(int i = 0; i < values.length; ++i)
			values[i] = list.get(i).intValue();
		
		return values;
	}
	
	int[] findForwardNeighbors(int[][] graph, int vertex)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for(int i = 0; i < graph.length; ++i)
			if( graph[i][0] == vertex )
				list.add(new Integer(graph[i][1]));
		
		return toIntArray(list);
	}
	
	int[] findBackwardNeighbors(int[][] graph, int vertex)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for(int i = 0; i < graph.length; ++i)
			if( graph[i][1] == vertex )
				list.add(new Integer(graph[i][0]));
		
		return toIntArray(list);
	}

	List<int[]> createCartesianProduct(int[][] neighbors)
	{
		int c = 1;
		for(int i = 0; i < neighbors.length; ++i)
			c = c * neighbors[i].length;
		
		ArrayList<int[]> list = new ArrayList<int[]>();
		
		for(int i = 0; i < c; ++i)
		{
			int v = i;
			int[] tuple = new int[neighbors.length];
			
			int j = neighbors.length;
			while( --j >= 0 )
			{
				tuple[j] = neighbors[j][v % neighbors[j].length];
				v = v / neighbors[j].length;
			}
			
			list.add(tuple);
		}
		
		return list;
	}
	
	List<int[]> findForwardNeighbors(int[][] graph, int[] tuple)
	{
		int[][] neighbors = new int[tuple.length][];
		for(int i = 0; i < tuple.length; ++i)
			neighbors[i] = findForwardNeighbors(graph, tuple[i]);

		return createCartesianProduct(neighbors);
	}
	
	List<int[]> findBackwardNeighbors(int[][] graph, int[] tuple)
	{
		int[][] neighbors = new int[tuple.length][];
		for(int i = 0; i < tuple.length; ++i)
			neighbors[i] = findBackwardNeighbors(graph, tuple[i]);

		return createCartesianProduct(neighbors);
	}

	List<int[]> findComponent(int[][] graph, int[] tuple)
	{
		HashSet<String> names = new HashSet<String>();
		ArrayList<int[]> todo = new ArrayList<int[]>();
		ArrayList<int[]> list = new ArrayList<int[]>();
		
		names.add(writeTuple(tuple));
		todo.add(tuple);
		
		while( ! todo.isEmpty() )
		{
			tuple = todo.remove(todo.size() - 1);
			list.add(tuple);
			
			List<int[]> neighbors = findForwardNeighbors(graph, tuple);
			Iterator<int[]> iter = neighbors.iterator();
			while( iter.hasNext() )
			{
				int[] t = iter.next();
				if( names.add(writeTuple(t)) )
					todo.add(t);
			}
			
			neighbors = findBackwardNeighbors(graph, tuple);
			iter = neighbors.iterator();
			while( iter.hasNext() )
			{
				int[] t = iter.next();
				if( names.add(writeTuple(t)) )
					todo.add(t);
			}
		}
		
		return list;
	}
	
	PrintWriter output;

	Solver solver;
	
	void parse(String input)
	{
		int arity;
		int size;
		String[] variables;
		List<int[]> variableTuples;
		
		StringTokenizer tokenizer = new StringTokenizer(input, "\n\r");
		
		String s;
		if( ! tokenizer.hasMoreTokens() || ! (s = tokenizer.nextToken()).startsWith("arity ") )
			throw new IllegalArgumentException("first line must be \"arity <number>\"");
		
		arity = Integer.parseInt(s.substring(6));
		if( arity <= 0 )
			throw new IllegalArgumentException("invalid arity value");

		if( ! tokenizer.hasMoreTokens() || ! (s = tokenizer.nextToken()).startsWith("size" ) )
			throw new IllegalArgumentException("the second line must be \"size <number>\"");
		else
		{
			size = Integer.parseInt(s.substring(5));
			if( size <= 0 )
				throw new IllegalArgumentException("invalid size value");
		
			int[][] values = new int[arity][];
			for(int i = 0; i < arity; ++i)
			{
				values[i] = new int[size];
				for(int j = 0; j < size; ++j)
					values[i][j] = j;
			}
		
			variableTuples = createCartesianProduct(values);
		}

		s = "";
		if( tokenizer.hasMoreTokens() && (s = tokenizer.nextToken()).startsWith("component ") )
		{
			StringTokenizer tok = new StringTokenizer(s, " \t");
			
			if( tok.countTokens() <= 3 )
				throw new IllegalArgumentException("too few arguments for \"component\"");
			
			tok.nextToken();

			int[] start = readTuple(tok.nextToken());
			if( start.length != arity )
				throw new IllegalArgumentException("incorrect length of <tuple> for \"component\"");
			
			if( ! tok.nextToken().equals("of") )
				throw new IllegalArgumentException("missing \"of\" keyword for \"component\"");
			
			int[][] graph = readGraph(s.substring(s.indexOf("of") + 2));

			if( getMaxVertex(graph) + 1 > size )
				throw new IllegalArgumentException("component graph has more vertices than size");
			
			variableTuples = findComponent(graph, start);
			
			if( tokenizer.hasMoreTokens() )
				s = tokenizer.nextToken();
			else
				s = "";
		}
		
		ArrayList<String> variableNames = new ArrayList<String>();
		Iterator<int[]> iter = variableTuples.iterator();
		while( iter.hasNext() )
		{
			int[] tuple = iter.next();
			variableNames.add(writeTuple(tuple));
		}

		Collections.sort(variableNames);
		variables = new String[variableNames.size()];
		variables = variableNames.toArray(variables);

		solver = new Solver(variables, size);
		
		while( ! s.isEmpty() || tokenizer.hasMoreTokens() )
		{
			StringTokenizer tok = new StringTokenizer(s, " \t=,");

			if( s.isEmpty() )
				;
			else if( s.startsWith("value ") )
			{
				tok.nextToken();
				
				String t = tok.nextToken();
				int variable = solver.getVariable(t);
				if( variable < 0 )
					throw new IllegalArgumentException("tuple \"" + t + "\" is not in the domain");

				ArrayList<Integer> values = new ArrayList<Integer>();
				while( tok.hasMoreTokens() )
				{
					String v = tok.nextToken();
					if( v.length() != 1 )
						throw new IllegalArgumentException("incorrect value '" + v +"'");
					
					int value = readElement(v.charAt(0));
					values.add(new Integer(value));
				}
				
				solver.addConstraint(variable, toIntArray(values));
			}
			else if( s.startsWith("weak-nu") )
			{
				int[][] equality = new int[size][];
				for(int i = 0; i < size; ++i)
					equality[i] = new int[] {i,i};

				int[] tuple = new int[arity]; 
					
				for(int i = 0; i < size; ++i)
				{
					for(int j = 0; j < arity; ++j)
						tuple[j] = i;
					
					int v = solver.getVariable(writeTuple(tuple));
					if( v >= 0 )
						solver.addConstraint(v, new int[] {i});
				}
			
				for(int m = 0; m < size; ++m)
					for(int n = 0; n < size; ++n)
					{
						if( n == m )
							continue;
						
						int first = -1;
						for(int i = 0; i < arity; ++i)
						{
							for(int j = 0; j < arity; ++j)
								tuple[j] = m;
							
							tuple[i] = n;
							
							int v = solver.getVariable(writeTuple(tuple));
							
							if( v < 0 )
								continue;

							if( first < 0 )
								first = v;
							else
								solver.addConstraint(first, v, equality);
						}
					}
			}
			else if( s.startsWith("nu") )
			{
				int[] tuple = new int[arity]; 
			
				for(int m = 0; m < size; ++m)
					for(int n = 0; n < size; ++n)
						for(int i = 0; i < arity; ++i)
						{
							for(int j = 0; j < arity; ++j)
								tuple[j] = m;
							
							tuple[i] = n;
							
							int v = solver.getVariable(writeTuple(tuple));
							if( v >= 0 )
								solver.addConstraint(v, new int[] {m});
						}
			}
			else if( s.startsWith("idempotent") )
			{
				int[] tuple = new int[arity]; 
			
				for(int m = 0; m < size; ++m)
				{
					for(int j = 0; j < arity; ++j)
						tuple[j] = m;
							
					int v = solver.getVariable(writeTuple(tuple));
					if( v >= 0 )
						solver.addConstraint(v, new int[] {m});
				}
			}
			else if( s.startsWith("conservative") )
			{
				int[] tuple = new int[arity];
				int[] range = new int[arity];
				
				outer: for(;;)
				{
					int rangeLength = 0;
					middle: for(int i = 0; i < arity; ++i)
					{
						for(int j = 0; j < rangeLength; ++j)
						{
							if( tuple[i] == range[j] )
								continue middle;
						}

						range[rangeLength++] = tuple[i];
					}

					int[] realRange = new int[rangeLength];
					System.arraycopy(range, 0, realRange, 0, rangeLength);

					int v = solver.getVariable(writeTuple(tuple));
					if( v >= 0 )
						solver.addConstraint(v, realRange);

					for(int i = 0; ; ++i)
					{
						if( i >= arity )
							break outer;
						if( ++tuple[i] >= size )
							tuple[i] = 0;
						else
							break;
					}
				}
			}
			else if( s.startsWith("preserves ") )
			{
				int graph[][] = readGraph(s.substring(10));
				int[] start = new int[arity];
				
				if( getMaxVertex(graph) >= size )
					throw new IllegalArgumentException("preserves graph has more vertices than size");
				
				int counter = 1;
				for(int i = 0; i < arity; ++i)
					counter = counter * size;
				
				while( --counter >= 0 )
				{
					int a = counter;
					for(int j = 0; j < arity; ++j)
					{
						start[j] = a % size;
						a /= size;
					}

					int startVar = solver.getVariable(writeTuple(start));
					if( startVar < 0 )
						continue;
					
					List<int[]> neighbors = findForwardNeighbors(graph, start);
					iter = neighbors.iterator();
					while( iter.hasNext() )
					{
						int[] neighbor = iter.next();
						int neighborVar = solver.getVariable(writeTuple(neighbor));
						if( neighborVar >= 0 )
						{
//							System.out.println("conn " + writeTuple(start) + " " + writeTuple(neighbor));
							solver.addConstraint(startVar, neighborVar, graph);
						}
					}
				}
			}
			else if( s.startsWith("cyclic") )
			{
				int[][] equality = new int[size][];
				for (int i = 0; i < size; ++i) {
					equality[i] = new int[] { i, i };
				}

				int[] A = new int[arity];
				outer: for(;;) {
					// shift array A to the left and store in B
					int[] B = new int[arity];
					System.arraycopy(A, 1, B, 0, arity - 1);
					B[arity - 1] = A[0];

					// if not a constant tuple
					if (!Arrays.equals(A, B)) {
						int v = solver.getVariable(writeTuple(A));
						int w = solver.getVariable(writeTuple(B));
						solver.addConstraint(v, w, equality);
					}
					
					// cycle through all tuples
					for (int i = 0; i < A.length; ++i) {
						if (++A[i] < size)
							continue outer;
						A[i] = 0;
					}
					
					break;
				}
			}
			else if( s.startsWith("symmetric") )
			{
				int[][] equality = new int[size][];
				for (int i = 0; i < size; ++i) {
					equality[i] = new int[] { i, i };
				}

				int[] A = new int[arity];
				outer: for(;;) {
					// shift array A to the left and store in B
					int[] B = new int[arity];
					System.arraycopy(A, 1, B, 0, arity - 1);
					B[arity - 1] = A[0];

					// if not a constant tuple
					if (!Arrays.equals(A, B)) {
						int v = solver.getVariable(writeTuple(A));
						int w = solver.getVariable(writeTuple(B));
						solver.addConstraint(v, w, equality);
						
						// swap the first two entries of A and copy all to C if different
						if (A[0] != A[1]) {
				            int[] C = new int[arity];
		                    System.arraycopy(A, 2, C, 2, arity - 2);
		                    C[0] = A[1];
		                    C[1] = A[0];
		                    int u = solver.getVariable(writeTuple(C));
		                    solver.addConstraint(v, u, equality);
		    			}
					}
					
					// cycle through all tuples
					for (int i = 0; i < A.length; ++i) {
						if (++A[i] < size)
							continue outer;
						A[i] = 0;
					}
					
					break;
				}
			}
			else if( s.startsWith("#") )
				;
			else
				throw new IllegalArgumentException("illegal line: " + s);
			
			if( tokenizer.hasMoreTokens() )
				s = tokenizer.nextToken();
			else
				s = "";
		}
	}
	
	public String consistency(String parameters)
	{
		try
		{
			long time = System.currentTimeMillis();
			StringWriter buffer = new StringWriter();
			output = new PrintWriter(buffer);
			output.println("running one consistency...");
			
			parse(parameters);

			solver.runOneConsistency();
			solver.printValues(output);

			time = System.currentTimeMillis() - time;
			output.println("execution time: " + (0.001 * time) + " seconds");
			
			return buffer.toString();
		}
		catch(Throwable e)
		{
			return e.toString();
		}
	}

	public String solve(String parameters)
	{
		try
		{
			long time = System.currentTimeMillis();
			StringWriter buffer = new StringWriter();
			output = new PrintWriter(buffer);
			output.println("finding a solution...");

			parse(parameters);

			solver.solve();
			solver.printValues(output);

			time = System.currentTimeMillis() - time;
			output.println("execution time: " + (0.0001 * time) + " seconds");
			
			return buffer.toString();
		}
		catch(Throwable e)
		{
			return e.toString();
		}
	}

	public static void main(String[] args) throws IOException
	{
		if( args.length == 0 || args.length > 2 || (!args[0].equals("-solve") && !args[0].equals("-consistency")) )
		{
			System.out.println("Usage: java -jar GraphPoly.jar [-solve | -consistency] [filename]");
			System.out.println("  if you omit the filename, then the standard input is read");
			System.out.println("  till you enter an empty line.");
			return;
		}

		InputStream input = System.in;
		if( args.length == 2 )
			input = new FileInputStream(args[1]);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	
		String parameters = "";
		for(;;)
		{
			String line = reader.readLine();
			if( line == null || line.length() == 0 )
				break;
			
			parameters += line + "\n";
		}
		
		GraphPolyApplet applet = new GraphPolyApplet();

		if( args[0].equals("-solve") )
			System.out.print(applet.solve(parameters));
		else
			System.out.print(applet.consistency(parameters));
	}
}
