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
import org.mmaroti.ua.util.*;

public class ZNine
{
	static int N = 12;
	static int A = 4;
	
	HashSet<String> definables = new HashSet<String>();
	
	int getValue(int[] polynom, int a)
	{
		int v = 0;
		int i = polynom.length;
		while( --i >= 0 )
		{
			v *= a;
			v = (v + polynom[i]) % N;
		}

		return v;
	}
	
	char getDigit(int a)
	{
		if( a <= 9 )
			return (char)('0'+ a);
		else
			return (char)('a' + a - 10);
	}
	
	String printPolynom(int[] polynom)
	{
		String s = "";
		for(int i = 0; i < polynom.length; ++i)
		{
			if( i > 0 )
				s += ' ';
			
			s += polynom[i];
		}
		
		return s;
	}
	
	void genDefinables()
	{
		CubeArgument argument = new CubeArgument(A,N);
		
		if( argument.reset() )
		do
		{
			String s = "";
			for(int i = 0; i < N; ++i)
			{
				if( getValue(argument.vector, i) == 0 )
					s += getDigit(i);
			}
			
			if( ! definables.contains(s) )
			{
				definables.add(s);
				System.out.println(s + " : " + printPolynom(argument.vector));
			}
			
		} while( argument.next() );
	}
	
	HashSet<String> ranges = new HashSet<String>();
	
	void genRanges()
	{
		CubeArgument argument = new CubeArgument(A,N);

		if( argument.reset() )
			do
			{
				boolean[] v = new boolean[N]; 
				for(int i = 0; i < N; ++i)
					v[getValue(argument.vector,i)] = true;

				String s = "";
				for(int i = 0; i < N; ++i)
				{
					if( v[i] )
						s += getDigit(i);
				}
				
				if( ! ranges.contains(s) )
				{
					ranges.add(s);
					System.out.println(s + " : " + printPolynom(argument.vector));
				}
			} while( argument.next() );
	}
	
	public static void main(String[] _)
	{
		ZNine app = new ZNine();
		app.genDefinables();
//		app.genRanges();
	}
}
