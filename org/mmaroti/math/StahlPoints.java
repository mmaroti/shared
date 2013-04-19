package org.mmaroti.math;

import java.util.*;

public class StahlPoints
{
	public int DOMAIN_SIZE = 8;
	public int BLACK_SIZE = 3;
	public int WHITE_SIZE = 5;
	
	public List<Long> BLACK;
	public List<Long> WHITE;

	public int CODOMAIN_SIZE = 11;
	public int COBLACK_SIZE = 4;
	public int COWHITE_SIZE = 6;

	public void checkStahlStabil(int N)
	{
		DOMAIN_SIZE = 2*N + 2;
		BLACK_SIZE = N;
		WHITE_SIZE = N + 2;
		
		BLACK = filterStable(getLevel(BLACK_SIZE));
		WHITE = filterAbove(getLevel(WHITE_SIZE), BLACK, 3);
		
		CODOMAIN_SIZE = 2*(N+1) + 3;
		COBLACK_SIZE = N + 1;
		COWHITE_SIZE = N + 4;
		
		printData();
	}
	
	public void checkStahlFull(int N)
	{
		DOMAIN_SIZE = 2*N + 2;
		BLACK_SIZE = N;
		WHITE_SIZE = N + 2;
		
		BLACK = getLevel(BLACK_SIZE);
		WHITE = new ArrayList<Long>();
		
		CODOMAIN_SIZE = 2*(N+1) + 3;
		COBLACK_SIZE = N + 1;
		COWHITE_SIZE = N + 4;
		
		printData();
	}
	
	public static void main(String[] args)
	{
		StahlPoints problem = new StahlPoints();
		problem.checkStahlFull(6);
	}
	
	public void printList(List<Long> list, String name)
	{
		System.out.print("param " + name + " :\t");
		for(int i = 1; i <= DOMAIN_SIZE; ++i)
			System.out.print("" + i + " ");
		System.out.println(":=");
		for(int i = 1; i <= list.size(); ++i)
		{
			long value = list.get(i-1); 
			
			System.out.print("\t" + i);
			for(int j = 1; j <= DOMAIN_SIZE; ++j)
			{
				System.out.print(j == 1 ? '\t' : ' ');
				System.out.print((value & 1) != 0 ? '1' : '0');
				value >>= 1;
			}

			System.out.println();
		}
		System.out.println("\t;");
	}
	
	public void printData()
	{
		System.out.println("param DOMAIN_SIZE := " + DOMAIN_SIZE + ";");
		System.out.println("param BLACK_SIZE := " + BLACK_SIZE + ";");
		System.out.println("param WHITE_SIZE := " + WHITE_SIZE + ";");
		System.out.println();

		System.out.println("param CODOMAIN_SIZE := " + CODOMAIN_SIZE + ";");
		System.out.println("param COBLACK_SIZE := " + COBLACK_SIZE + ";");
		System.out.println("param COWHITE_SIZE := " + COWHITE_SIZE + ";");
		System.out.println();

		System.out.println("param BLACK_COUNT := " + BLACK.size() + ";");
		printList(BLACK, "B");
		System.out.println();

		System.out.println("param WHITE_COUNT := " + WHITE.size() + ";");
		printList(WHITE, "W");
		System.out.println();

		System.out.println("end;");
	}

	public int getBitCount(long value)
	{
		int c = 0;
		while( value != 0 )
		{
			c += 1;
			value &= value-1;
		}
		return c;
	}
	
	public List<Long> getLevel(int level)
	{
		List<Long> list = new ArrayList<Long>();

		long value = (long)1 << DOMAIN_SIZE;
		do
		{
			--value;
			if( getBitCount(value) == level )
				list.add(value);
			
		} while( value != 0 );

		return list;
	}

	public boolean isStable(long value)
	{
		boolean zero = (value & 1) == 0;
		int a = DOMAIN_SIZE;
		
		while( --a >= 1 )
		{
			if( (value & 3) == 3 )
				return false;
			
			value >>= 1;
		}

		return zero || (value & 1) == 0;
	}
	
	public List<Long> filterStable(List<Long> list)
	{
		List<Long> result = new ArrayList<Long>();
		
		for(long value : list)
		{
			if( isStable(value) )
				result.add(value); 
		}
		
		return result;
	}

	public int getAboveCount(long value, List<Long> blacks)
	{
		int c = 0;
		for(long black : blacks)
		{
			if( (black & value) == black )
				c += 1;
		}
		return c;
	}
	
	public List<Long> filterAbove(List<Long> list, List<Long> blacks, int minCount)
	{
		List<Long> result = new ArrayList<Long>();

		for(long value : list)
		{
			if( getAboveCount(value, blacks) >= minCount )
				result.add(value); 
		}
		
		return result;
	}
	
	public String toString(Long value)
	{
		String s = "";
		for(int i = 0; i < DOMAIN_SIZE; ++i)
		{
			s += (value & 1) != 0 ? '1' : '0';
			value >>= 1;
		}
		
		return s;
	}
	
	public void print(List<Long> list)
	{
		int i = 0;
		for(Long value : list)
		{
			i += 1;
			System.out.println("" + i + "\t" + toString(value));
		}
	}
}
