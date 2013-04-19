package org.mmaroti.math;

import java.util.*;

public class KneserPseudoBoolean
{
	public static int N = 3;
	
	public static int DOMAIN = 2*N + 2;
	
	public static String getPoint(boolean[] vector)
	{
		String s = "";
		for(int i = 0; i < vector.length; ++i)
			s += vector[i] ? '1' : '0';
		
		return s;
	}
	
	public static String getUnion(String a, String b)
	{
		if( a.length() != b.length() )
			throw new IllegalArgumentException();
		
		String s = "";
		for(int i = 0; i < a.length(); ++i)
		{
			char c = a.charAt(i);
			char d = b.charAt(i);
			char e;
			
			if( c == '0' && d == '0' )
				e = '0';
			else
				e = '1';
			
			s += e;
		}
		
		return s;
	}
	
	public static int getLevel(String point)
	{
		int a = 0;
		
		for(int i = 0; i < point.length(); ++i)
			if( point.charAt(i) == '1' )
				++a;
		
		return a;
	}
	
	public List<String> getBlackPoints()
	{
		boolean[] vector = new boolean[DOMAIN];
		for(int i = 0; i < DOMAIN; i += 2)
			vector[i] = true;
		
		List<String> points = new ArrayList<String>();

		for(int i = 0; i < DOMAIN; ++i)
		{
			if( ! vector[i] )
				continue;

			for(int j = 0; j < DOMAIN; ++j)
			{
				if( ! vector[j] )
					continue;

				int k = i-1;
				boolean[] v = vector.clone();
				do
				{
					k = (k+1) % DOMAIN;
					v[k] = ! v[k];
				} while( k != j );
				
				String s = getPoint(v);
				if( ! points.contains(s) )
				{
					System.out.println("" + i + " " + j + " : " + s);
					points.add(s);
				}
			}
		}
		
		return points;
	}
	
	public static void main(String[] args)
	{
		KneserPseudoBoolean problem = new KneserPseudoBoolean();
		
		System.out.println(problem.getBlackPoints().size());
			
	}
}
