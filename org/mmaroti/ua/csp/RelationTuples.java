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

package org.mmaroti.ua.csp;

/**
 * Classes implementing this interface capture fixed relations
 * over at most 32-element sets of arbitrary arity.
 */
public class RelationTuples extends Relation
{
	int[][] tuples;

	public String printTuples()
	{
		String s = "";
		for(int i = 0; i < tuples.length; ++i)
		{
			if( i != 0 )
				s += " ";

			s += "(";
			for(int j = 0; j < arity; ++j)
			{
				if( j != 0 )
					s += ",";
				
				int k = 0;
				while( (1 << k) != tuples[i][j] )
					++k;
				
				s += k;
			}
			s += ")";
		}
		return s;
	}

	public RelationTuples(int arity, int[][] tuples)
	{
		super(arity);
		
		this.tuples = new int[tuples.length][];
		for(int i = 0; i < tuples.length; ++i)
		{
			if( tuples[i].length != arity )
				throw new IllegalArgumentException("Incorrect tuple size");
			
			int[] a = new int[arity];
			for(int j = 0; j < arity; ++j)
			{
				if( tuples[i][j] < 0 || tuples[i][j] >= 32 )
					throw new IllegalArgumentException("Illegal value");
				
				a[j] = (1 << tuples[i][j]);
			}
				
			this.tuples[i] = a;
		}
	}
	
	public void contains(int[] input, int[] output)
	{
		for(int i = 0; i < arity; ++i)
			output[i] = 0;
		
		outer: for(int[] tuple : tuples)
		{
			for(int i = 0; i < arity; ++i)
				if( (tuple[i] & input[i]) == 0 )
					continue outer;
			
			for(int i = 0; i < arity; ++i)
				output[i] |= tuple[i];
		}
	}
	
	public String toString()
	{
		return "tuples " + printTuples(); 
	}
}
