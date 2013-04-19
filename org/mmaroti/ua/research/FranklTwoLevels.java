package org.mmaroti.ua.research;

public class FranklTwoLevels
{
	public static int level(long a)
	{
		if( a < 0 )
			throw new IllegalArgumentException();
		
		int c = 0;
		while( a != 0 )
		{
			c += a & 1;
			a = a >> 1;
		}
		return c;
	}
	
	public static int leastBit(long a)
	{
		if( a <= 0 )
			throw new IllegalArgumentException();

		int c = 0;
		while( (a & 1) == 0 )
		{
			++c;
			a = a >> 1;
		}
		return c;
	}
	
	public static long next(long a)
	{
		if( a <= 0 )
			throw new IllegalArgumentException();
		
		long b = 1;
		while( (a & b) == 0 )
			b = b << 1;

		a -= b;
		b = b << 1;
		
		long c = 1;
		while( (a & b) != 0 )
		{
			a = a - b + c;
			b = b << 1;
			c = c << 1;
		};

		a += b;
		
		return a;
	}
	
	public static String print(long a)
	{
		if( a < 0 )
			throw new IllegalArgumentException();

		if( a == 0 )
			return "0";
		
		String s = new String();
		
		while( a != 0 )
		{
			s += ((a & 1) != 0) ? '1' : '0';
			a = a >> 1;
		}
		
		return s;
	}

	public static int subsLessThan(long a, long b)
	{
		if( a <= 0 || (a << 1) <= 0 || b < 0 )
			throw new IllegalArgumentException();
		
		int c = 0;
		long d = 1;
		while( d <= a )
		{
			if( (a & d) != 0 && a - d <= b )
				++c;
			
			d = d << 1;
		}
		
		return c;
	}
	
	public static void main(String[] args)
	{
		long a = 7;
		for(int j = 0; j < 20; ++j)
		{
			int c = subsLessThan(next(a), a);
			
			System.out.print(print(a) + ": ");

			long b = a;
			for(int i = 0; i < 10; ++i)
			{
				b = next(b);
				int d = subsLessThan(b, a);
				System.out.print("" + d + " ");
				
				if( d > c )
					throw new IllegalStateException("wow: " + print(a) + " " + print(b));
			}
			
			System.out.println();
			a = next(a);
		}

		System.out.println("last: " + print(a));

/*
		long a = 7;
		long b = 15;
		for(int i = 0; i < 4000; ++i)
		{
			while( a < b )
			{
				System.out.print(print(a) + " ");
				a = next(a);
			}
			
			System.out.println(print(b));
			b = next(b);
		}
*/
	}
}
