
package org.mmaroti.math.frankl;

import java.util.*;

public class Frankl
{
	static Random random = new Random();

	static class Family
	{
		int[] subsets;
		int size;

		Family()
		{
			subsets = new int[10];
			size = 0;
		}

		Family(Family f)
		{
			subsets = f.subsets.clone();
			size = f.size;
		}

		void add(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( subsets[i] == elem )
					return;

			if( size == subsets.length )
			{
				int[] s = new int[2*subsets.length];
				System.arraycopy(subsets, 0, s, 0, size);
				subsets = s;
			}

			subsets[size++] = elem;
		}

		void add(Family f)
		{
			for(int i = 0; i < f.size; ++i)
				add(f.subsets[i]);
		}

		static Family product(Family f, Family g)
		{
			Family h = new Family();

			for(int i = 0; i < f.size; ++i)
				for(int j = 0; j < g.size; ++j)
					h.add(f.subsets[i] | g.subsets[j]);

			return h;
		}

		boolean inFamily(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( subsets[i] == elem )
					return true;
		
			return false;
		}

		boolean isSubsetOf(Family f)
		{
			for(int i = 0; i < size; ++i)
				if( ! f.inFamily(subsets[i]) )
					return false;

			return true;
		}

		boolean isClosed()
		{
			for(int i = 1; i < size; ++i)
				for(int j = 0; j < i; ++j)
					if( ! inFamily(subsets[i] | subsets[j]) )
						return false;
			
			return true;
		}

		void close()
		{
			for(int i = 1; i < size; ++i)
				for(int j = 0; j < i; ++j)
					add(subsets[i] | subsets[j]);
		}

		void remove(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( subsets[i] == elem )
				{
					subsets[i] = subsets[--size];
					return;
				}
		}

		void remove(Family f)
		{
			for(int i = 0; i < f.size; ++i)
				remove(f.subsets[i]);
		}

		int topElement()
		{
			int a = 0;

			for(int i = 0; i < size; ++i)
				a |= subsets[i];
		
			return a;
		}

		boolean inQuotient(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( ! inFamily(elem | subsets[i]) )
					return false;

			return true;
		}

		Family quotient()
		{
			Family f = new Family();

			int top = topElement();
			for(int b = 0; b <= top; ++b)
			{
				if( (b | top) == top && inQuotient(b) )
					f.add(b);
			}

			return f;
		}

		boolean isMinimal(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( (elem | subsets[i]) == elem && elem != subsets[i] )
					return false;

			return true;
		}

		Family minimals()
		{
			Family f = new Family();

			for(int i = 0; i < size; ++i)
				if( isMinimal(subsets[i]) )
					f.add(subsets[i]);

			return f;
		}

		boolean isMaximal(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( (elem & subsets[i]) == elem && elem != subsets[i] )
					return false;

			return true;
		}

		Family maximals()
		{
			Family f = new Family();

			for(int i = 0; i < size; ++i)
				if( isMaximal(subsets[i]) )
					f.add(subsets[i]);

			return f;
		}

		boolean isIndecomposable(int elem)
		{
			for(int i = 0; i < size; ++i)
				if( (subsets[i] | elem) == elem && subsets[i] != elem )
					for(int j = 0; j < size; ++j)
						if( (subsets[i] | subsets[j]) == elem &&  subsets[j] != elem )
							return false;

			return true;
		}

		Family indecomposables()
		{
			Family f = new Family();

			for(int i = 0; i < size; ++i)
				if( isIndecomposable(subsets[i]) )
					f.add(subsets[i]);

			return f;
		}

		Family largerLowerCovers()
		{
			Family f = quotient();
			f.remove(this);
			return f.maximals();
		}

		Family largerLowerCover(int elem)
		{
			Family f = new Family(this);
			f.add(elem);
			return f;
		}

		Family smallerLowerCovers()
		{
			Family f = new Family();

			for(int i = 0; i < size; ++i)
				if( ! isMinimal(subsets[i]) && isIndecomposable(subsets[i]) )
					f.add(subsets[i]);

			return f;
		}

		Family smallerLowerCover(int elem)
		{
			Family f = new Family(this);
			f.remove(elem);
			return f;
		}

		boolean hasMajority(int mask)
		{
			int c = 0;

			for(int i = 0; i < size; ++i)
				if( (subsets[i] & mask) != 0 )
					++c;
			
			return 2*c >= size;
		}

		int majorities()
		{
			int top = topElement();
			int mask = 1;
			int result = 0;

			while( mask <= top )
			{
				if( hasMajority(mask) )
					result |= mask;
				
				mask <<= 1;
			}

			return result;
		}

		void print()
		{
			Arrays.sort(subsets, 0, size);

			for(int i = 0; i < size; ++i)
				System.out.print(Integer.toBinaryString(subsets[i]) + ' ');
		
			System.out.println();
		}

		public boolean equals(Object o)
		{
			if( !(o instanceof Family) )
				return false;

			Family f = (Family)o;
			
			if( size != f.size )
				return false;

			Arrays.sort(subsets, 0, size);
			Arrays.sort(f.subsets, 0, size);

			for(int i = 0; i < size; ++i)
				if( subsets[i] != f.subsets[i] )
					return false;
			
			return true;
		}

		float averageSize()
		{
			int s = 0;

			for(int i = 0; i < size; ++i)
			{
				int a = subsets[i];
				while( a != 0 )
				{
					s += (a & 1);
					a >>= 1;
				}
			}

			return ((float)s)/size;
		}

		static Family random(int max, int size)
		{
			Family f = new Family();

			for(int i = 0; i < size; ++i)
				f.add(random.nextInt(max));

			return f;
		}

		void info()
		{
			System.out.print("family: ");
			print();

			System.out.print("short form:");
			for(int i = 0; i < size; ++i)
				System.out.print(" " + subsets[i]);
			System.out.println();

			if( ! isClosed() )
			{
				System.out.println("it is not closed");
				return;
			}

			System.out.println("average size: " + averageSize());
			System.out.println("majorities: " + Integer.toBinaryString(majorities()));

			System.out.print("quotient: ");
			quotient().print();

			System.out.print("maximals: ");
			maximals().print();

			System.out.print("minimals: ");
			minimals().print();

			System.out.print("indecomposables: ");
			indecomposables().print();

			System.out.print("larger lower covers: ");
			largerLowerCovers().print();

			System.out.print("smaller lower covers: ");
			smallerLowerCovers().print();
		}

		// CONJECTURE: there are no such elements
		boolean isInteresting()
		{
			close();

			int m = majorities();
			if( m == 0 )			// maybe...
				return true;

			Family f = largerLowerCovers();
			for(int i = 0; i < f.size; ++i)
			{
				if( (~f.subsets[i] & m) == 0 )
					return true;
			}

			return false;
		}

		static boolean isInteresting(Family f, Family g)
		{
			f.close();
			g.close();

			Family ff = f.quotient();
			Family gg = g.quotient();

			if( ff.equals(gg) )
			{
				int fm = f.majorities();
				int gm = g.majorities();

				if( (fm|gm) != gm && (fm|gm) != fm )
					return true;
			}

			return false;
		}

		static boolean isInteresting2(Family f, Family g)
		{
			f.close();
			g.add(f);
			g.close();

			if( f.size == 1 && f.subsets[0] == 0 )
				return false;

//			if( ! product(f,g).isSubsetOf(f) )
//				return false;

			return (f.majorities() & g.majorities()) == 0;
		}
	}

	public static void main(String[] args)
	{
		for(int i = 0; i < 10000000; ++i)
		{
			Family f = Family.random(1<<5,3);
			Family g = Family.random(1<<9,3);
			if( Family.isInteresting2(f,g) )
			{
				f.info();
				System.out.println();
				g.info();
				System.exit(0);
			}
		}

		System.out.println("Not found");
	}
}
