package org.mmaroti.ua.research;

import java.util.*;

public final class FranklIdeals2
{
	// the number of elements of the base set 
	static final int M = 4;
	
	// the maximum number of families
	static final int X = 1 << M;

	static int totalSemiIdeals = 0;
	
	static int[] firstBit = new int[X];
	static int[] firstMask = new int[X];
	static int[] bitCount = new int[X];

	static void initFirstMask()
	{
		firstBit[0] = 0;
		firstMask[0] = -1;
		
		for(int a = 1; a < X; ++a)
		{
			int b = 0;
			while( ((1<<b) & a) == 0 )
				++b;
			
			firstBit[a] = b;
			firstMask[a] = ~(1<<b);
			bitCount[a] = 1 + bitCount[a & ~(1<<b)]; 
		}
	}

	static class AtomPattern
	{
		int subsets = 0;
		int[] weights = new int[M];
		boolean[] subset = new boolean[X];
		
		AtomPattern(SemiIdeal ideal)
		{
			subsets = ideal.subsets;
			
			for(int a = 0; a < X; ++a)
			{
				subset[a] = ideal.intervals[a] != 0;
				if( subset[a] )
				{
					int b = a;
					while( b != 0 )
					{
						++weights[firstBit[b]];
						b &= firstMask[b];
					}
				}
			}
			
			Arrays.sort(weights);
		}
		
		public int hashCode()
		{
			int c = subsets;
			
			for(int i = 0; i < M; ++i)
				c += weights[i] << (i+i);
			
			return c;
		}
		
		public boolean equals(Object obj)
		{
			AtomPattern a = (AtomPattern)obj;
			
			if( subsets != a.subsets )
				return false;
			
			for(int i = 0; i < M; ++i)
				if( weights[i] != a.weights[i] )
					return false;
			
			return true;
		}
		
		void print()
		{
			System.out.print("" + subsets + ":");
			
			for(int i = 0; i < M; ++i)
				System.out.print(" " + weights[i]);
			
			System.out.print(" sample:");
			
			for(int i = 0; i < X; ++i)
				if( subset[i] )
					System.out.print(" " + subsetToString(i));
			
			System.out.println();
		}
	}

	static HashSet<AtomPattern> patterns = new HashSet<AtomPattern>();
	
	static void printPatterns()
	{
		Iterator<AtomPattern> iter = patterns.iterator();
		while( iter.hasNext() )
		{
			AtomPattern pattern = iter.next();
			pattern.print();
		}
	}
	
	static String subsetToString(int a)
	{
		String s = new String();
		
		int b = 1 << (M-1);
		while( b > 0 )
		{
			s += (a & b) != 0 ? '1' : '0';
			b >>= 1;
		}
		
		return s;
	}
	
	static class SemiIdeal
	{
		int subsets = 0;
		int[] intervals = new int[X];

		boolean[] getJustified()
		{
			boolean[] vector = new boolean[X];
			
			outer: for(int a = 0; a < X; ++a)
			{
				if( intervals[a] != 0 )
					for(int b = 0; b < X; ++b)
					{
						if( ! vector[b] && bitCount[a] == bitCount[b] )
						{
							vector[b] = true;
							continue outer;
						}
					}
			}
			
			return vector;
		}

		static boolean isSemiIdeal(boolean[] vector)
		{
			SemiIdeal ideal = new SemiIdeal();
			
			for(int a = 0; a < X; ++a)
				if( vector[a] )
				{
					int b = ideal.isNextSubset(a);
					if( b != 0 )
					{
						++ideal.subsets;
						ideal.intervals[a] = b;
					}
					else
						return false;
				}
			
			return true;
		}
		
		public String toString()
		{
			boolean first = true;
			String s = new String();
			
			for(int a = 0; a < X; ++a)
				if( intervals[a] != 0 )
				{
					if( first )
						first = false;
					else
						s += ' ';
					
					s += subsetToString(a);
				}
			
			return s;
		}
		
		public int isNextSubset(int a)
		{
			int b = a;
			int c = -1;
			
			while( b != 0 )
			{
				int d = firstMask[b];
				c &= (intervals[a & d] | ~d);
				b &= d;
			}
			
			return c;
		}
		
		void forEach()
		{
			++totalSemiIdeals;
			
			if( ! isSemiIdeal(getJustified()) )
				System.out.println("bad: " + toString());
			
//			patterns.add(new AtomPattern(this));
		}
		
		void search(int next)
		{
			forEach();
			
			while(++next < X)
			{
				int d = isNextSubset(next);
				if( d != 0 )
				{
					intervals[next] = d;
					++subsets;

					search(next);

					--subsets;
					intervals[next] = 0;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		initFirstMask();
		
		SemiIdeal semiIdeal = new SemiIdeal();
		long time = System.currentTimeMillis();
		semiIdeal.search(0);
		time = System.currentTimeMillis() - time;
		
		printPatterns();
		System.out.println("total semiideals: " + totalSemiIdeals);
		System.out.println("total patterns: " + patterns.size());
		System.out.println("time: " + time);
	}
}
