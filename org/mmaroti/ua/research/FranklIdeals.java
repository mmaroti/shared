package org.mmaroti.ua.research;

import java.util.*;

public class FranklIdeals
{
	static final int MAX_LIST = 2;
	
	// the number of elements of the base set 
	static final int M = 5;
	
	// the maximum number of familys
	static final int X = 1 << M;

	static String printElement(int a)
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
	
	static String printFamily(boolean[] family)
	{
		boolean first = true;
		String s = new String();
		
		for(int a = 0; a < X; ++a)
			if( family[a] )
			{
				if( first )
					first = false;
				else
					s += ' ';
				
				s += printElement(a);
			}
		
		return s;
	}

	static boolean equalsFamily(boolean[] f, boolean[] g)
	{
		for(int i = 0; i < X; ++i)
			if( f[i] != g[i] )
				return false;
		
		return true;
	}

	static boolean containsFamily(ArrayList<boolean[]> list, boolean[] family)
	{
		for(int i = 0; i < list.size(); ++i)
			if( equalsFamily(list.get(i), family) )
				return true;
		
		return false;
	}
	
	static boolean containsSubFamily(ArrayList<boolean[]> list, boolean[] family)
	{
		for(int a = 0; a < X; ++a)
			if( family[a] )
			{
				family[a] = false;
				
				if( containsFamily(list, family) )
				{
					family[a] = true;
					return true;
				}
				
				family[a] = true;
			}
		
		return false;
	}
	
	static String printSequence(int[] count)
	{
		String s = new String();
		
		for(int i = 0; i < count.length; ++i)
		{
			if( i > 0 )
				s += ' ';
			
			s += count[i];
		}
		
		return s;
	}
	
	static int lexCompare(int[] a, int[] b)
	{
		for(int i = 0; i < a.length; ++i)
		{
			int c = a[i] - b[i];
			if( c != 0 )
				return c;
		}
		
		return 0;
	}

	static int sumCompare(int[] a, int[] b)
	{
		int c = 0;
		
		for(int i = 0; i < a.length; ++i)
			c += a[i] - b[i];
		
		return c;
	}

	static int testCompare(int[] a, int[] b)
	{
		double c = 0;
		double d = 0;
		
		for(int i = 0; i < a.length; ++i)
		{
			c += a[i] * a[i];
			d += b[i] * b[i];
		}

		c = d - c;
		if( c > 0.001 )
			return 1;
		else if( c < -0.001 )
			return -1;
		else
			return 0;

//		if( a[0] != b[0] )
//			return a[0] - b[0];
//		else
//			return sumCompare(a, b);
	}
	
	static class Maximum
	{
		ArrayList<boolean[]> families = new ArrayList<boolean[]>();
		int[] count;
		int subsetCount;

		Maximum(boolean[] f)
		{
			families.add(f.clone());
			count = getAtomCounts(f);
			subsetCount = getSubsetCount(f);
			Arrays.sort(count);
		}

		void compare(boolean[] f)
		{
			int[] c = getAtomCounts(f);
			Arrays.sort(c);
	
			int d = sumCompare(count, c);
			if( d < 0 )
			{
				families.clear();
				families.add(f.clone());
				count = c;
			}
			else if( d == 0 )
				families.add(f.clone());
		}
		
		static Maximum compare(Maximum m, boolean[] f)
		{
			if( m == null )
				return new Maximum(f);
			else
			{
				m.compare(f);
				return m;
			}
		}
		
		void trimFamilies(ArrayList<boolean[]> prev)
		{
			int i = families.size();
			while( --i >= 0 )
			{
				boolean[] family = families.get(i);
				if( ! containsSubFamily(prev, family) )
					families.remove(i);
			}
		}
		
		void print(ArrayList<boolean[]> prev)
		{
			int c = 0;
			for(int i = 0; i < M; ++i)
				c += count[i];
		
			if( subsetCount == 0 )
				subsetCount = 1;
			
			System.out.println(printSequence(count) + " (sum: " + c + " average: " 
					+ (double)c / subsetCount + ")");

			int i = families.size();
			if( i > MAX_LIST )
				i = MAX_LIST;
			while( --i >= 0 )
			{
				boolean[] family = families.get(i);
				System.out.println("\t" + printFamily(family)
						+ " " + containsSubFamily(prev, family));
			}
			
			if( families.size() > MAX_LIST )
				System.out.println("\t...");
		}
	}
	
	static Maximum[] maximums = new Maximum[X+1];

	static void trimMaximums()
	{
		for(int i = 2; i < maximums.length; ++i)
			if( maximums[i] != null )
			{
				ArrayList<boolean[]> prev = new ArrayList<boolean[]>();
				
				if( i > 0 && maximums[i-1] != null )
					prev.addAll(maximums[i-1].families);
				
				maximums[i].trimFamilies(prev);
			}
	}
	
	static void printMaximums()
	{
		for(int i = 0; i < maximums.length; ++i)
			if( maximums[i] != null )
			{
				System.out.print("" + i + ": ");
				ArrayList<boolean[]> prev = (i > 0 && maximums[i-1] != null) ? maximums[i-1].families : new ArrayList<boolean[]>();
				maximums[i].print(prev);
			}
	}

	static int getSubsetCount(boolean[] family)
	{
		int total = 0;

		for(int a = 0; a < X; ++a)
			if( family[a] )
				++total;

		return total;
	}
	
	static int[] getAtomCounts(boolean[] family)
	{
		int[] atoms = new int[M];
		
		for(int a = 0; a < X; ++a)
			if( family[a] )
			{
				int i = 0;
				int b = a;
				while( b > 0 )
				{
					if( (b & 1) != 0 )
						++atoms[i];
					
					++i;
					b >>= 1;
				}
			}

		return atoms;
	}
	
	static void forEach(boolean[] family)
	{
		int total = getSubsetCount(family);
		maximums[total] = Maximum.compare(maximums[total], family);
	}
	
	static boolean isIdealElement(boolean[] family, int a)
	{
		int b = 1;
		while( b <= a )
		{
			if( (a & b) != 0 && ! family[a & ~b] )
				return false;
		
			b <<= 1;
		}
		return true;
	}
	
	static boolean isIdeal(boolean[] family)
	{
		for(int a = 0; a < X; ++a)
			if( family[a] && ! isIdealElement(family, a) )
				return false;
		
		return true;
	}
	
	static void searchIdeals(int next, boolean[] family)
	{
		forEach(family);
		
		for(int a = next; a < X; ++a)
		{
			if( ! family[a] && isIdealElement(family, a) )
			{
				family[a] = true;
				searchIdeals(a + 1, family);
				family[a] = false;
			}
		}
	}
	
	static void searchIdeals()
	{
		boolean[] family = new boolean[X];
		searchIdeals(0, family);
	}

	static boolean containsClosedOpenInterval(boolean[] family, int a, int b)
	{
		for(int c = 0; c < b; ++c)
			if( (c & a) == a && (c & b) == c && ! family[c] )
				return false;
		
		return true;
	}
	
	static boolean isSemiIdealElement(boolean[] family, int a)
	{
		int b = 1;
		while( b <= a )
		{
			if( a == b )
				return true;
			
			if( (a & b) != 0 && family[b] && containsClosedOpenInterval(family, b, a) )
				return true;
		
			b <<= 1;
		}
		return false;
	}
	
	static boolean isSemiIdeal(boolean[] family)
	{
		for(int a = 0; a < X; ++a)
			if( family[a] && ! isSemiIdealElement(family, a) )
				return false;
		
		return true;
	}
	
	static void searchSemiIdeals(int next, boolean[] family)
	{
		forEach(family);
		
		for(int a = next; a < X; ++a)
		{
			if( ! family[a] && isSemiIdealElement(family, a) )
			{
				family[a] = true;
				searchSemiIdeals(a + 1, family);
				family[a] = false;
			}
		}
	}
	
	static void searchSemiIdeals()
	{
		boolean[] family = new boolean[X];
		searchSemiIdeals(1, family);
	}
	
	public static void main(String[] args)
	{
		searchSemiIdeals();
		trimMaximums();
		printMaximums();
	}
}
