/**
 *	Copyright (C) Miklos Maroti, 2009
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
import java.io.*;
import org.mmaroti.ua.util.*;

public class WeakNU
{
	int size;

	static int squareRoot(int a)
	{
		int b = 0;
		while( b * b < a )
			++b;
		
		if( b * b != a )
			throw new IllegalArgumentException();
		
		return b;
	}
	
	static class Term
	{
		final int size;
		String comment;
		
		int index;
		static int totalIndices = 0;
		
		static void resetIndices()
		{
			totalIndices = 0;
		}
		
		Term(int size, String comment)
		{
			if( size < 0 )
				throw new IllegalArgumentException();
			
			this.comment = comment;
			this.index = ++totalIndices;
			
			this.size = size;
			majority = new int[size*size];
			minority = new int[size*size];
		}
		
		int[] majority;
		int[] minority;

		int getMajority(int a, int b)
		{
			return majority[a*size + b];
		}

		int getMinority(int a, int b)
		{
			return minority[a*size + b];
		}

		void setValue(int a, int b, int c, int d)
		{
			int i = a*size + b;
			majority[i] = c;
			minority[i] = d;
		}

		boolean isWeakNU()
		{
			int i = size * size;
			while( --i >= 0 )
				if( majority[i] != minority[i] )
					return false;
			
			return true;
		}
		
		public boolean equals(Object a)
		{
			Term term = (Term)a;
			
			int i = size * size;
			while( --i >= 0 )
				if( majority[i] != term.majority[i] || minority[i] != term.minority[i] )
					return false;
			
			return true;
		}

		static Term createVariable(int size)
		{
			Term term = new Term(size, "var");
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
					term.setValue(a, b, a, b);
			
			return term;
		}
		
		static Term createWeakNU(int[] product)
		{
			int size = squareRoot(product.length);

			Term term = new Term(size, "wnu");
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
				{
					int c = product[a * size + b];
					
					if( a == b && a != c )
						throw new IllegalArgumentException();
					
					if( c < 0 || c >= size )
						throw new IllegalArgumentException();
					
					term.setValue(a, b, c, c);
				}
			
			return term;
		}

		static Term WeakNUCompose(Term t1, Term t2)
		{
			int size = t1.size;
			if( size != t2.size )
				throw new IllegalArgumentException();
			
			Term term = new Term(size, "c-wnu " + t1.index + " " + t2.index);
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
					term.setValue(a, b, 
							t1.getMajority(t2.getMajority(a, b), t2.getMinority(a, b)), 
							t1.getMinority(t2.getMajority(a, b), t2.getMinority(a, b)));
			
			return term;
		}

		static Term ProductCompose(Term t1, Term t2, Term t3)
		{
			int size = t1.size;
			if( size != t2.size || size != t3.size )
				throw new IllegalArgumentException();
			
			Term term = new Term(size, "c-prod " + t1.index + " " + t2.index + " " + t3.index);
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
					term.setValue(a, b, 
							t1.getMinority(t2.getMajority(a, b), t3.getMajority(a, b)), 
							t1.getMinority(t2.getMinority(a, b), t3.getMinority(a, b)));
			
			return term;
		}

		static Term ProductCompose2(Term t1, Term t2, Term t3)
		{
			int size = t1.size;
			if( size != t2.size || size != t3.size )
				throw new IllegalArgumentException();
			
			Term term = new Term(size, "c-prod " + t1.index + " " + t2.index + " " + t3.index);
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
					term.setValue(a, b, 
							t1.getMajority(t2.getMajority(a, b), t3.getMajority(a, b)), 
							t1.getMajority(t2.getMinority(a, b), t3.getMinority(a, b)));
			
			return term;
		}

		static Term MaltsevCompose(Term t1, Term t2, Term t3, Term t4)
		{
			int size = t1.size;
			if( size != t2.size || size != t3.size || size != t4.size )
				throw new IllegalArgumentException();
			
			Term term = new Term(size, "c-maltsev " + t1.index + " " + t2.index + " " + t3.index + " " + t4.index);
			
			for(int a = 0; a < size; ++a)
				for(int b = 0; b < size; ++b)
				{
					int x, y;
					
					int c = t2.getMajority(a, b);
					int d = t3.getMajority(a, b);
					int e = t4.getMajority(a, b);
					
					if( c == d )
						x = t1.getMinority(d, e);
					else if( d == e )
						x = t1.getMajority(d, c);
					else
						return null;
					
					c = t2.getMinority(a, b);
					d = t3.getMinority(a, b);
					e = t4.getMinority(a, b);
					
					if( c == d )
						y = t1.getMinority(d, e);
					else if( d == e )
						y = t1.getMajority(d, c);
					else
						return null;
					
					term.setValue(a, b, x, y); 
				}

			return term;
		}
		
		void print()
		{
			if( size > 9 )
				throw new UnsupportedOperationException();
	
			boolean wnu = isWeakNU();
			
			for(int a = 0; a < size; ++a)
			{
				for(int b = 0; b < size; ++b)
				{
					if( b != 0 )
						System.out.print(' ');
					
					System.out.print(getMajority(a, b));
				}
				
				if( !wnu )
				{
					System.out.print(" \t");
				
					for(int b = 0; b < size; ++b)
					{
						if( b != 0 )
							System.out.print(' ');
					
						System.out.print(getMinority(a, b));
					}
				}

				System.out.println();
			}
			
			System.out.println("" + index + ": " + comment);
			System.out.println();
		}
	}
	
	static class Clone
	{
		int size;
		
		List<Term> list = new ArrayList<Term>();
	
		Clone(int size)
		{
			this.size = size;
			list.add(Term.createVariable(size));
		}
		
		void add(Term term)
		{
			if( term == null )
				return;
			
			if( term.size != size )
				throw new IllegalArgumentException();
			
			if( ! list.contains(term) )
				list.add(term);
		}
		
		void addWeakNu(int[] product)
		{
			add(Term.createWeakNU(product));
		}
		
		void print()
		{
			for(Term term : list)
				term.print();
		}
		
		List<Term> getWeakNUs()
		{
			ArrayList<Term> wnus = new ArrayList<Term>();
			
			for(Term term : list)
				if( term.isWeakNU() )
					wnus.add(term);
			
			return wnus;
		}
		
		static int MAX_CLONE_SIZE = 100;
		
		void closure()
		{
			int i = 0;
			while( i < list.size() && i < MAX_CLONE_SIZE )
			{
				int j = 0;
				while( j <= i )
				{
					add(Term.WeakNUCompose(list.get(i), list.get(j)));
					add(Term.WeakNUCompose(list.get(j), list.get(i)));

					int k = 0;
					while( k <= i )
					{
						add(Term.ProductCompose(list.get(i), list.get(j), list.get(k)));
						add(Term.ProductCompose(list.get(j), list.get(i), list.get(k)));
						add(Term.ProductCompose(list.get(j), list.get(k), list.get(i)));

						add(Term.ProductCompose2(list.get(i), list.get(j), list.get(k)));
						add(Term.ProductCompose2(list.get(j), list.get(i), list.get(k)));
						add(Term.ProductCompose2(list.get(j), list.get(k), list.get(i)));

						int l = 0;
						while( l <= i )
						{
							add(Term.MaltsevCompose(list.get(i), list.get(j), list.get(k), list.get(l)));
							add(Term.MaltsevCompose(list.get(j), list.get(i), list.get(k), list.get(l)));
							add(Term.MaltsevCompose(list.get(j), list.get(k), list.get(i), list.get(l)));
							add(Term.MaltsevCompose(list.get(j), list.get(k), list.get(l), list.get(i)));
							
							++l;
						}
						++k;
					}
					++j;
				}
				++i;
			}
		}
	}

	static boolean isSmaller(int[] product, int[] perm1, int[] inv1, int[] perm2, int[] inv2)
	{
		int size = perm1.length;
		
		for(int a = 0; a < size; ++a)
			for(int b = 0; b < size; ++b)
			{
				int c = inv1[product[perm1[a] * size + perm1[b]]];
				int d = inv2[product[perm2[a] * size + perm2[b]]];
				
				if( d < c )
					return true;
				else if( c < d )
					return false;
			}
		
		return false;
	}
	
	static int[] getNormalizedTable(int[] product, int size)
	{
		PermArgument arg = new PermArgument(size);
		arg.reset();
		
		int[] perm = arg.vector;
		int[] inv = arg.getInverse();
		
		int[] bestPerm = perm.clone();
		int[] bestInv = inv.clone();
		
		do
		{
			if( isSmaller(product, bestPerm, bestInv, perm, inv) )
			{
				bestPerm = perm.clone();
				bestInv = inv.clone();
			}
		} while( arg.next() );
		
		int[] newProduct = new int[product.length];
		for(int a = 0; a < size; ++a)
			for(int b = 0; b < size; ++b)
				newProduct[a * size + b]= bestInv[product[bestPerm[a] * size + bestPerm[b]]];
		
		return newProduct;
	}
	
	static List<Term> getNormalizedWNUs(Clone clone)
	{
		ArrayList<Term> wnus = new ArrayList<Term>();
		
		for(Term term : clone.list)
		{
			if( term.isWeakNU() )
			{
				int[] product = getNormalizedTable(term.majority, term.size);
				Term wnu = Term.createWeakNU(product);

				if( ! wnus.contains(wnu) )
					wnus.add(wnu);
			}
		}
			
		return wnus;
	}
	
	static int isMinimal(int[] wnu)
	{
		int size = squareRoot(wnu.length);
		
		Clone clone = new Clone(size);
		clone.addWeakNu(wnu);
		clone.closure();

		int opCount = clone.list.size();
		
		List<Term> wnus = clone.getWeakNUs();
		for( Term gen : wnus )
		{
			clone = new Clone(size);
			clone.add(gen);
			clone.closure();
		
			if( clone.list.size() != opCount && clone.list.size() < Clone.MAX_CLONE_SIZE )
				return 0;
		}

		return opCount < Clone.MAX_CLONE_SIZE ? 1 : -1;
	}

	static void printGeneratum(int[] wnu)
	{
		int size = squareRoot(wnu.length);

		Term.resetIndices();
		Clone clone = new Clone(size);
		clone.addWeakNu(wnu);

		clone.closure();
		clone.print();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		BufferedReader file = new BufferedReader(new FileReader(
				"C:\\Documents and Settings\\mmaroti\\Desktop\\wnu4min2q.txt"));

		Parser parser = new Parser();

		while( file.ready() )
		{
			String line = file.readLine();
			String[] list = parser.parseList(line, " ");
			
			int[] wnu = new int[list.length];
			for(int i = 0; i < list.length; ++i)
				wnu[i] = Integer.parseInt(list[i]);

			int m = isMinimal(wnu);
			if( m != 0 )
			{
//				System.out.println("*****");
//				printGeneratum(wnu);

				System.out.println((m > 0 ? "+ " : "? ") + line);
			}
		}
		
		file.close();
	}
}
