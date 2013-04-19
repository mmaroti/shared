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

package org.mmaroti.ua.test;

import org.mmaroti.ua.util.*;

public class TestArgument 
{
	public static void printVector(Argument arg)
	{
		System.out.println("" + arg.getIndex() + " : " + arg.toString());
	}
	
	public static void printVectors(Argument arg)
	{
		if( arg.reset() ) do
		{
			System.out.println("" + arg.getIndex() + " : " + arg.toString());
		} while( arg.next() );
		
		System.out.println("total: " + arg.getMaxIndex());
	}

	public static void test(Argument arg)
	{
		int arity = arg.vector.length;
		int size = arg.getMaxIndex();
		
		int[][] vectors = new int[size][arity];

		int i = 0;
		if( arg.reset() ) do
		{
			for(int j = 0; j < arity; ++j)
				vectors[i][j] = arg.vector[j];
			
			if( arg.getIndex() != i )
				throw new IllegalStateException("returned index does not match iteration");
				
			if( ++i > size )
				throw new IllegalStateException("more vectors than indices");
		} while( arg.next() );

		if( i != size )
			throw new IllegalStateException("less vectors than indices");

		for(i = 0; i < size; ++i)
		{
			arg.setIndex(i);
			
			if( arg.getIndex() != i )
				throw new IllegalStateException("set index does not work");
				
			for(int j = 0; j < arity; ++j)
				if( vectors[i][j] != arg.vector[j] )
					throw new IllegalStateException("incorrect vector after setting index");
		}
		
		System.out.println("test passed");
	}
	
	public static void main(String[] _)
	{
		Argument arg = new PermArgument(4);

		printVectors(arg);
		test(arg);
	}

}
