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

package org.mmaroti.ua.math;

import org.mmaroti.ua.set.*;

/**
 * This class represents finite fields.
 */
public class Graph extends FiniteSet
{
	public Graph(int size)
	{
		super(size);
		
		table = new boolean[size * size];
	}

	public Graph(Graph graph)
	{
		super(graph.size);
		
		table = graph.table.clone();
	}
	
	boolean[] table;
	
	public boolean hasEdge(int a, int b)
	{
		return table[a * size + b];
	}
	
	public void setEdge(int a, int b, boolean edge)
	{
		table[a * size + b] = edge;
	}

	public void preOrderJoin(int a, int b)
	{
		if( hasEdge(a,b) )
			return;
		
		for(int c = 0; c < size; ++c)
			if( c == a || hasEdge(c,a) )
				for(int d = 0; d < size; ++d)
					if( d == b || hasEdge(b,d) )
						setEdge(c,d,true);
	}
	
	public Graph transitiveColsure()
	{
		Graph closure = new Graph(size);
		
		for(int a = 0; a < size; ++a)
			closure.setEdge(a,a,true);

		for(int a = 0; a < size; ++a)
			for(int b = 0; b < size; ++b)
				if( hasEdge(a,b) )
					closure.preOrderJoin(a,b);
		
		if( ! closure.isTransitive() )
			throw new IllegalArgumentException();
		
		return closure;
	}
	
	public boolean isTransitive()
	{
		for(int a = 0; a < size; ++a)
			for(int b = 0; b < size; ++b)
				for(int c = 0; c < size; ++c)
					if( hasEdge(a,b) && hasEdge(b,c) && ! hasEdge(a,c) )
						return false;
		
		return true;
	}
	
	public boolean isSameBlock(int a, int b)
	{
		return hasEdge(a, b) && hasEdge(b, a);
	}
	
	public boolean isCoverBlock(int a, int b)
	{
		if( hasEdge(b,a) || ! hasEdge(a, b) )
			return false;
		
		for(int i = 0; i < size; ++i)
			if( hasEdge(a,i) && ! hasEdge(i,a) && hasEdge(i,b) && ! hasEdge(b,i) )
				return false;
		
		return true;
	}
}
