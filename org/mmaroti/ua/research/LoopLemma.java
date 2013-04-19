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

package org.mmaroti.ua.research;

import java.util.*;

public class LoopLemma
{
	public static class Graph
	{
		public int size;
		public boolean[][] edges;
		
		public Graph(int size)
		{
			this.size = size;
			
			edges = new boolean[size][];
			for(int i = 0; i < size; ++i)
				edges[i] = new boolean[size];
		}

		public void clear()
		{
			for(int i = 0; i < size; ++i)
				Arrays.fill(edges[i], false);
		}
		
		public final boolean get(int a, int b)
		{
			return edges[a][b];
		}

		public final void set(int a, int b, boolean connected)
		{
			edges[a][b] = connected;
		}
		
		protected static char getVertexName(int index)
		{
			if( index < 0 )
				throw new IllegalArgumentException();
			else if( index < 10 )
				return (char)(index + '0');
			else if( index <= 'z' - 'a' + 10 )
				return (char)(index - 10 + 'a');
			else
				throw new IllegalArgumentException("too large graph");
		}
		
		protected static int getVertexIndex(char name)
		{
			if( '0' <= name && name <= '9' )
				return name - '0';
			else if( 'a' <= name && name <= 'z' )
				return name - 'a' + 10;
			else
				throw new IllegalArgumentException();
		}
		
		public void addEdges(String paths)
		{
			int last = -1;
			for(int i = 0; i < paths.length(); ++i)
			{
				char c = paths.charAt(i);
				if( Character.isWhitespace(c) )
				{
					last = -1;
					continue;
				}
				
				int next = getVertexIndex(c);
				if( last >= 0 )
					set(last, next, true);
				
				last = next;
			}
		}
		
		public void printEdges()
		{
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
					if( get(a,b) )
						System.out.print("" + getVertexName(a) + getVertexName(b) + " ");

			System.out.println();
		}

		public int[] getToDistances(int source)
		{
			int[] dist = new int[size];
			Arrays.fill(dist, -1);
			dist[source] = 0;
			
			int[] list = new int[size];
			list[0] = source;
			int last = 1;
			for(int next = 0; next < last; ++next)
			{
				source = list[next];
				for(int i = 0; i < size; ++i)
					if( get(source, i) && dist[i] < 0 )
					{
						dist[i] = dist[source] + 1;
						list[last++] = i;
					}
			}
			
			return dist;
		}
		
		public int[] getFromDistances(int source)
		{
			int[] dist = new int[size];
			Arrays.fill(dist, -1);
			dist[source] = 0;
			
			int[] list = new int[size];
			list[0] = source;
			int last = 1;
			for(int next = 0; next < last; ++next)
			{
				source = list[next];
				for(int i = 0; i < size; ++i)
					if( get(i, source) && dist[i] < 0 )
					{
						dist[i] = dist[source] + 1;
						list[last++] = i;
					}
			}
			
			return dist;
		}
		
		public boolean isConnected()
		{
			int[] dist = getToDistances(0);
			for(int i = 0; i < size; ++i)
				if( dist[i] < 0 )
					return false;
		
			dist = getFromDistances(0);
			for(int i = 0; i < size; ++i)
				if( dist[i] < 0 )
					return false;
			
			return true;
		}
		
		public static int getGCD(int a, int b)
		{
			if( a < 0 )
				a = -a;
			if( b < 0 )
				b = -b;
			
			if( a > b )
			{
				int t = b;
				b = a;
				a = t;
			}
			
			while( a != 0 )
			{
				int t = b % a;
				b = a;
				a = t;
			}
			
			return b;
		}
		
		public int getGCDofLoops(int source)
		{
			int gcd = 0;			

			int[] dist = getToDistances(source);
			for(int i = 0; i < size; ++i)
				if( dist[i] >= 0 && get(i, source) )
					gcd = getGCD(gcd, dist[i]+1);
			
			return gcd;
		}
		
		public int getGCDofLoops()
		{
			int gcd = 0;

			for(int i = 0; i < size; ++i)
				gcd = getGCD(gcd, getGCDofLoops(i));
			
			return gcd;
		}
		
		public void printStatistics()
		{
			System.out.println("vertices: " + size);
			System.out.print("edges: ");
			printEdges();
			System.out.println("connected: " + isConnected());
			System.out.println("gcd of loops: " + getGCDofLoops());
		}

		public boolean[] getToNeighbors(boolean[] nodes)
		{
			boolean[] neighbors = new boolean[size];
			
			for(int i = 0; i < size; ++i)
				if( nodes[i] )
					for(int j = 0; j < size; ++j)
						if( get(i,j) )
							neighbors[j] = true;
			
			return neighbors;
		}
		
		public static boolean isFull(boolean[] subset)
		{
			for(int i = 0; i < subset.length; ++i)
				if( ! subset[i] )
					return false;
			
			return true;
		}
		
		public boolean[] getLimitNeighbors(int node)
		{
			if( size <= 1 )
				throw new IllegalArgumentException();
			
			boolean[] nodes = new boolean[size];
			nodes[node] = true;
			
			for(;;)
			{
				boolean[] neighbors = getToNeighbors(nodes);
				if( isFull(neighbors) )
					break;
				
				nodes = neighbors;
			}
			
			return nodes;
		}

		public static void printSubset(boolean[] nodes)
		{
			for(int i = 0; i < nodes.length; ++i)
				if( nodes[i] )
					System.out.print(getVertexName(i));
			
			System.out.println();
		}
	}
	
	public static void main(String[] _)
	{
		Graph graph = new Graph(5);
		graph.addEdges("012341 40 132");
		graph.printStatistics();
		Graph.printSubset(graph.getLimitNeighbors(1));
	}
}
