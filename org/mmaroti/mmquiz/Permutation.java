
package org.mmaroti.mmquiz;

public class Permutation 
{
	static class Perm
	{
		int f[];
		
		Perm(int size)
		{
			f = new int[size];
		}

		Perm(Perm p)
		{
			f = p.f.clone();
		}

		int size()
		{
			return f.length;
		}
		
		int randomEntry()
		{
			return (int)(Math.random() * f.length);
		}
		
		void random()
		{
			for(int i = 0; i < f.length; ++i)
			{
				middle: for(;;)
				{
					int a = randomEntry();
					for(int j = 0; j < i; ++j)
						if( f[j] == a )
							continue middle;
			
					f[i] = a;
					break middle;
				}
			}
		}
	
		void randomTransposition()
		{
			int a = randomEntry();
			int b;
			do
			{
				b = randomEntry();
			} while( b == a );
			
			for(int i = 0; i < f.length; ++i)
				f[i] = i;
			
			f[a] = b;
			f[b] = a;
		}
		
		String printTwoLine()
		{
			String s = new String();
			
			s += "\\begin{pmatrix}";
			
			for(int i = 0; i < f.length; ++i)
			{
				if( i != 0 )
					s += " &";
				
				s += " " + (i+1);
			}
			
			s += " \\\\";
			
			for(int i = 0; i < f.length; ++i)
			{
				if( i != 0 )
					s += " &";
				
				s += " " + (f[i]+1);
			}
		
			s += " \\end{pmatrix}";
			
			return s;
		}
		
		String printCycles()
		{
			int moved = 0;
			int g[] = f.clone();
			for(int i = 0; i < f.length; ++i)
			{
				g[i] = f[i];
				if( f[i] != i )
					++moved;
			}
			
			if( moved == 0 )
				return "\\id";
			
			String s = new String();
			while( moved > 0 )
			{
				int a = randomEntry();
				if( g[a] == a )
					continue;
				
				s += "(";
				int b = a;
				do
				{
					if( a != b )
						s += "\\ ";
					
					s += (b+1);
					g[b] = b;
					--moved;
					
					b = f[b];
				} while( b != a );
				s += ")";
			}
			
			return s;
		}

		String printCyclesOmitOne()
		{
			String s = printCycles();
			int a = s.indexOf(')');
			if( a > 0 && a < s.length()-1 )
				return s.substring(a+1);
			else
				return null;
		}
		
		void multiply(Perm p)
		{
			for(int i = 0; i < f.length; ++i)
				f[i] = p.f[f[i]];
		}
		
		int cycleCount()
		{
			String s = printCycles();

			int a = 0;
			for(int i = 0; i < s.length(); ++i)
				if( s.charAt(i) == '(' )
					++a;
			
			return a;
		}
		
		Perm cycleOf(int a)
		{
			Perm p = new Perm(f.length);
			for(int i = 0; i < f.length; ++i)
				p.f[i] = i;
			
			int b = a;
			do
			{
				p.f[b] = f[b];
				b = f[b];
			} while( b != a );

			return p;
		}

		Perm inverse()
		{
			Perm p = new Perm(f.length);
			for(int i = 0; i < f.length; ++i)
				p.f[f[i]] = i;
			return p;
		}
		
		public boolean equals(Object a)
		{
			Perm p = (Perm)a;
			
			if( f.length != p.f.length )
				return false;
			
			for(int i = 0; i < f.length; ++i)
				if( f[i] != p.f[i] )
					return false;
			
			return true;
		}
	}

	public static void problemOneSimpleBad()
	{
		Perm p = new Perm(6);
		
		for(int i = 0; i < 50; ++i)
		{
			p.random();
			
			Perm q = new Perm(p);
			
			Perm t = new Perm(p.size());
			t.randomTransposition();
			q.multiply(t);
			
			System.out.println("\\item{f} $" + q.printTwoLine() + " = " + p.printCycles() + "$");
		}
	}
	
	public static void problemOneOmitBad()
	{
		Perm p = new Perm(6);
		
		for(int i = 0; i < 50; ++i)
		{
			String s;

			do
			{
				p.random();
				s = p.printCyclesOmitOne();
			} while( s == null );
			
			System.out.println("\\item{f} $" + p.printTwoLine() + " = " + s + "$");
		}
	}
	
	public static void problemTwoGood()
	{
		Perm p = new Perm(5);
		Perm q = new Perm(5);
		
		for(int i = 0; i < 150; ++i)
		{
			p.random();
			q.random();
			
			Perm r = new Perm(p);
			r.multiply(q);
			
			System.out.println("\\item{t} $" + p.printCycles() + q.printCycles() + " = " + r.printCycles() + "$");
		}
	}
	
	public static void problemTwoSimpleBad()
	{
		Perm p = new Perm(5);
		Perm q = new Perm(5);
		
		for(int i = 0; i < 50; ++i)
		{
			p.random();
			q.random();
			
			Perm r = new Perm(p);
			r.multiply(q);

			Perm t = new Perm(p.size());
			t.randomTransposition();
			r.multiply(t);

			System.out.println("\\item{f} $" + p.printCycles() + q.printCycles() + " = " + r.printCycles() + "$");
		}
	}
	
	public static void problemTwoOrderBad()
	{
		Perm p = new Perm(5);
		Perm q = new Perm(5);
		
		for(int i = 0; i < 50; ++i)
		{
			Perm r, b;
			
			do
			{
				p.random();
				q.random();
			
				r = new Perm(p);
				r.multiply(q);

				b = new Perm(q);
				b.multiply(p);
			} while( b.equals(r) );
			
			System.out.println("\\item{f} $" + p.printCycles() + q.printCycles() + " = " + b.printCycles() + "$");
		}
	}

	public static void problemTwoOmitBad()
	{
		Perm p = new Perm(5);
		Perm q = new Perm(5);
		
		for(int i = 0; i < 50; ++i)
		{
			Perm r;
			String s;
			
			for(;;)
			{
				p.random();
				q.random();

				s = p.printCycles();
				if( s.charAt(0) != '(' )
					continue;
				
				r = new Perm(p);
				r.multiply(q);

				int a = p.randomEntry();
				if( r.f[a] == a )
					continue;

				Perm t = r.cycleOf(a);
				int b = s.charAt(1) - '1';
				
				if( t.f[b] != b )
					continue;
				
				r.multiply(t.inverse());
				if( r.cycleCount() == 0 )
					continue;
				
				break;
			}
			
			System.out.println("\\item{f} $" + s + q.printCycles() + " = " + r.printCycles() + "$");
		}
	}

	public static void main(String[] args)
	{
		problemTwoOmitBad();
	}
}
