
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;

public class ComplexNumbers 
{
	static Rationals r = new Rationals();
	
	static Object randomRational()
	{
		int a, b;
		
		for(;;)
		{
			a = (int)(Math.random() * 21) - 10;
			b = (int)(Math.random() * 21) - 10;
			if( b != 0 )
				break;
		}

		String s = Integer.toString(a) + "/" + Integer.toString(b);
		
		return r.parse(s);
	}

	static Object randomRationalWhole()
	{
		int a = (int)(Math.random() * 21) - 10;
		return r.parse(Integer.toString(a));
	}

	static Complex randomComplex()
	{
		return new Complex(randomRational(), randomRational());
	}
	
	static Complex randomComplexWhole()
	{
		return new Complex(randomRationalWhole(), randomRationalWhole());
	}
	
	static class Complex
	{
		Complex(Object a, Object b)
		{
			this.a = a;
			this.b = b;
		}
		
		Object a, b;
		
		Complex sum(Complex c)
		{
			return new Complex(r.sum(a, c.a), r.sum(b, c.b));
		}
		
		Complex negative()
		{
			return new Complex(r.negative(a), r.negative(b));
		}
		
		Complex conjugate()
		{
			return new Complex(a, r.negative(b));
		}
		
		Complex product(Complex c)
		{
			return new Complex(
					r.sum(r.product(a, c.a), r.negative(r.product(b, c.b))),
					r.sum(r.product(a, c.b), r.product(b, c.a))
				);
		}

		Complex inverse()
		{
			Object d = r.inverse(r.sum(r.product(a, a), r.product(b, b)));
			return new Complex( r.product(a, d), r.product(r.negative(b), d));
		}
		
		boolean isReal()
		{
			return r.signum(b) == 0;
		}
		
		public boolean equals(Object c)
		{
			Complex cc = (Complex)c;
			return r.areEquals(a, cc.a) && r.areEquals(b, cc.b);
		}
		
		public String toString()
		{
			if( r.signum(b) == 0 )
				return r.toString(a, 'n');
			
			if( r.signum(a) == 0 )
			{
				if( r.areEquals(b, r.unitElement()) )
					return "i";
				else if( r.areEquals(b, r.negative(r.unitElement())) )
					return "-i";
				else
					return r.toString(b, 'n') + "i";
			}

			String s = r.toString(a, 'n');
			if( r.signum(b) > 0 )
				s += "+";
			
			if( r.areEquals(b, r.unitElement()) )
				s += "i";
			else if( r.areEquals(b, r.negative(r.unitElement())) )
				s += "-i";
			else
				s += r.toString(b, 'n') + "i";
			
			return s;
		}
	}

	static Object moduloTwoPi(Object a)
	{
		Object two = r.sum(r.unitElement(), r.unitElement());

		while( r.signum(r.sum(a, r.negative(two))) >= 0 )
			a = r.sum(a, r.negative(two));
		
		while( r.signum(a) < 0 )
			a = r.sum(a, two);
		
		return a;
	}

	static Object randomRadian()
	{
		int a = (int)(Math.random() * 15) + 1;
		String s;
		
		if( a < 12 )
			s = "" + a + "/6";
		else
			s = "" + (2*(a-12)+1) + "/4";
		
		return r.parse(s);
	}
	
	static String printUnit(Object a)
	{
		String s = r.toString(moduloTwoPi(a));

		if( s.equals("0") )
			return "1";
		else if( s.equals("1/6") )
			return "\\frac{\\sqrt{3}}{2} + \\frac{1}{2}i";
		else if( s.equals("1/3") )
			return "\\frac{1}{2} + \\frac{\\sqrt{3}}{2}i";
		else if( s.equals("1/2") )
			return "i";
		else if( s.equals("2/3") )
			return "-\\frac{1}{2} + \\frac{\\sqrt{3}}{2}i";
		else if( s.equals("5/6") )
			return "-\\frac{\\sqrt{3}}{2} + \\frac{1}{2}i";
		else if( s.equals("1") )
			return "-1";
		else if( s.equals("7/6") )
			return "-\\frac{\\sqrt{3}}{2} - \\frac{1}{2}i";
		else if( s.equals("4/3") )
			return "-\\frac{1}{2} - \\frac{\\sqrt{3}}{2}i";
		else if( s.equals("3/2") )
			return "-i";
		else if( s.equals("5/3") )
			return "\\frac{1}{2} - \\frac{\\sqrt{3}}{2}i";
		else if( s.equals("11/6") )
			return "\\frac{\\sqrt{3}}{2} - \\frac{1}{2}i";
		else if( s.equals("1/4") )
			return "\\frac{\\sqrt{2}}{2} + \\frac{\\sqrt{2}}{2}i"; 
		else if( s.equals("3/4") )
			return "-\\frac{\\sqrt{2}}{2} + \\frac{\\sqrt{2}}{2}i"; 
		else if( s.equals("5/4") )
			return "-\\frac{\\sqrt{2}}{2} - \\frac{\\sqrt{2}}{2}i"; 
		else if( s.equals("7/4") )
			return "\\frac{\\sqrt{2}}{2} - \\frac{\\sqrt{2}}{2}i";
		else
			throw new IllegalArgumentException(s);
	}
	
	public static void problemOneGood()
	{
		for(int i = 0; i < 120; ++i)
		{
			Complex u = randomComplexWhole();
			Complex v = randomComplexWhole();

			if( u.isReal() || v.isReal() )
			{
				--i;
				continue;
			}
			
			Complex w = u.product(v.inverse());
			
			System.out.println("\\item{t} $\\frac{" + u.toString() + "}{" + v.toString() + "} = " + w.toString() + "$");
		}
	}
	
	public static void problemOneBad()
	{
		for(int i = 0; i < 120; )
		{
			Complex u = randomComplexWhole();
			Complex v = randomComplexWhole();

			if( u.isReal() || v.isReal() )
				continue;
			
			Complex w = u.product(v.inverse());
		
			int a = (int)(Math.random() * 6);
			if( a == 0 )
				u = u.conjugate();
			else if( a == 1 )
				u = u.negative().conjugate();
			else if( a == 2 )
				v = v.conjugate();
			else if( a == 3 )
				v = v.negative().conjugate();
			else if( a == 4 )
				w = w.conjugate();
			else
				w = w.negative().conjugate();

			Complex ww = u.product(v.inverse());
			if( w.equals(ww) )
				continue;
			
			System.out.println("\\item{f} $\\frac{" + u.toString() + "}{" + v.toString() + "} = " + w.toString() + "$");
			++i;
		}
	}
	
	public static void problemTwoGood()
	{
		for(int i = 0; i < 120; )
		{
			Object o = randomRadian();
			int n = (int)(Math.random()*40)-20;
			if( n >= 0 )
				++n;
			
			Object u = r.product(o, r.parse("" + n));
			
			System.out.println("\\item{t} $\\left(" + printUnit(o) + "\\right)^{" + n + "} = " + printUnit(u) + "$");
			
			++i;
		}
	}

	public static void problemTwoBad1()
	{
		for(int i = 0; i < 80; )
		{
			Object o = randomRadian();
			int n = (int)(Math.random()*40)-20;
			if( n >= 0 )
				++n;

			int m = (int)(Math.random()*41)-20;

			Object u = r.product(o, r.parse("" + n));
			Object v = r.product(o, r.parse("" + m));
			if( r.areEquals(u, v) )
				continue;
			
			System.out.println("\\item{f} $\\left(" + printUnit(o) + "\\right)^{" + n + "} = " + printUnit(v) + "$");
			
			++i;
		}
	}

	public static void problemTwoBad2()
	{
		for(int i = 0; i < 40; )
		{
			Object o = randomRadian();
			int n = (int)(Math.random()*40)-20;
			if( n >= 0 )
				++n;

			Object u = r.product(o, r.parse("" + n));
			Object v = randomRadian();
			if( r.areEquals(u, v) )
				continue;
			
			System.out.println("\\item{f} $\\left(" + printUnit(o) + "\\right)^{" + n + "} = " + printUnit(v) + "$");
			
			++i;
		}
	}

	public static void main(String[] args)
	{
		problemTwoGood();
		problemTwoBad1();
		problemTwoBad2();
	}
}
