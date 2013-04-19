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

public class BoundedWidthProver
{
	static String variables = "xyzuvw";
	
	static String variable(int coord)
	{
		return variables.substring(coord, coord+1);
	}

	static String variable(int coord, int index)
	{
		return variable(coord) + index;
	}

	static int bitCount(int mask)
	{
		int c = 0;
		
		while( mask > 0 )
		{
			c += mask & 1;
			mask >>= 1;
		}
		
		return c;
	}

	static String component(int coord)
	{
		return variable(coord).toUpperCase();
	}
	
	static String relation(int mask)
	{
		String s = "";
		
		int i = 0;
		while( mask > 0 )
		{
			if( (mask & 1) == 1 )
				s += component(i);
			
			mask >>= 1;
			++i;
		}
		
		return s;
	}

	static String arguments(int mask)
	{
		String s = "";
		
		int i = 0;
		while( mask > 0 )
		{
			if( (mask & 1) == 1 )
			{
				if( s.length() > 0 )
					s += ",";

				s += variable(i);
			}
			
			mask >>= 1;
			++i;
		}
		
		return s;
	}
	
	static String arguments(int mask, int index)
	{
		String s = "";
		
		int i = 0;
		while( mask > 0 )
		{
			if( (mask & 1) == 1 )
			{
				if( s.length() > 0 )
					s += ",";

				s += variable(i, index);
			}
			
			mask >>= 1;
			++i;
		}
		
		return s;
	}
	
	static String exists(int mask)
	{
		String s = "";
		
		int i = 0;
		while( mask > 0 )
		{
			if( (mask & 1) == 1 )
				s += "exists " + variable(i);
			
			mask >>= 1;
			++i;
		}
		
		return s;
	}

	static void extensions(int sub, int ext)
	{
		for(int i = 0; i < (1 << variables.length()); ++i)
		{
			if( bitCount(i) != ext )
				continue;

			String s = "";
			s += relation(i) + "(" + arguments(i) + ") <-> (";
			
			boolean first = true;
			for(int j = 0; j <= i; ++j )
			{
				if( bitCount(j) != sub || (i & j) != j )
					continue;
				
				if( ! first )
					s += " & ";
				else
					first = false;
				
				s += relation(j) + "(" + arguments(j) + ")";
			}
			s += ").";
			
			System.out.println(s);
		}
	}
	
	static void subpowers(int sub, String operation, int arity)
	{
		for(int r = 0; r < (1 << variables.length()); ++r)
		{
			if( bitCount(r) != sub )
				continue;

			String s = "(";
			for(int i = 0; i < arity; ++i)
			{
				if( i != 0 )
					s += " & ";
				
				s += relation(r) + "(" + arguments(r,i) + ")";
			}

			s += ") -> " + relation(r) + "(";
			
			boolean first = true;
			for(int j = 0; j < variables.length(); ++j)
			{
				if( ((1<<j) & r) == 0 )
					continue;
				
				if( ! first )
					s += ", ";
				else
					first = false;
				
				s += operation + "(";
				for(int i = 0; i < arity; ++i)
				{
					if( i != 0 )
						s += ",";
					
					s += variable(j,i);
				}
				s += ")";
			}
			s += ").";
			
			System.out.println(s);
		}
	}

	static void consistencyUp(int sub, int ext)
	{
		for(int i = 0; i < (1 << variables.length()); ++i)
		{
			if( bitCount(i) != ext )
				continue;

			for(int j = 0; j <= i; ++j )
			{
				if( bitCount(j) != sub || (i & j) != j )
					continue;

				String s = relation(j) + "(" + arguments(j) + ") -> (" + exists(i & ~j) + " "
					+ relation(i) + "(" + arguments(i) + ")).";
				System.out.println(s);
			}
		}
	}
	
	static void consistencyUpDown(int sub, int ext)
	{
		int full = 1 << variables.length();
		
		for(int i = 0; i < full; ++i)
		{
			if( bitCount(i) != sub )
				continue;

			for(int j = 0; j < full; ++j )
			{
				if( bitCount(j) != ext || (i & j) != i )
					continue;

				String s = relation(i) + "(" + arguments(i) + ") -> (" + exists(j & ~i) + " (";
				boolean first = true;
				
				for(int k = 0; k < full; ++k)
				{
					if( bitCount(k) != sub || (k & j) != k || i == k )
						continue;
					
					if( first )
						first = false;
					else
						s += " & ";
					
					s += relation(k) + "(" + arguments(k) + ")";
				}
				
				s += ")).";
				System.out.println(s);
			}
		}
	}
	
	static void consistencyDown(int sub, int ext)
	{
		for(int i = 0; i < (1 << variables.length()); ++i)
		{
			if( bitCount(i) != ext )
				continue;

			String s = relation(i) + "(" + arguments(i) + ") -> (";
			
			boolean first = true;
			for(int j = 0; j <= i; ++j )
			{
				if( bitCount(j) != sub || (i & j) != j )
					continue;

				if( ! first )
					s += " & ";
				else
					first = false;
				
				s += relation(j) + "(" + arguments(j) + ")";
			}
			s += ").";
			
			System.out.println(s);
		}
	}
	
	public static void main(String[] args)
	{
		variables = "xyzu";
//		extensions(2, 4);
//		subpowers(2, "f", 3);
//		subpowers(1, "g", 3);
//		consistencyUp(2,3);
//		consistencyDown(2,3);
//		consistencyDown(2,4);
//		consistencyDown(1,2);
		consistencyUpDown(2,3);
	}
}
