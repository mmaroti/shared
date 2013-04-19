
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;

public class EuclideanSpaces 
{
	static Rationals field = new Rationals();
	
	static void randomizeSymmetric(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j <= i; ++j)
			{
				int d = 0;
				if( Math.random() < 0.8 )
					d = (int)(Math.random() * 9) - 4;
				
				Object o;
				if( i != j )
					o = field.parse("" + d + "/2");
				else
					o = field.parse("" + d);
				
				matrix.setElem(i,j,o);
				matrix.setElem(j,i,o);
			}
	}
	
	static void randomize(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				int d = 0;
				if( Math.random() < 0.8 )
					d = (int)(Math.random() * 9) - 4;
				
				matrix.setElem(i, j, field.parse("" + d));
			}
	}

	public static void diagonalize(Matrix m)
	{
		Field f = m.getField();
		int z = f.zero();
		int u = f.unit();
		int d = f.sum(u, u);
		
		for(int i = 0; i < m.getRows(); ++i)
		{
			if( m.get(i,i) == z )
			{
				int j = m.getRows();
				while( --j > i && m.get(i,j) == z )
					;

				if( j <= i )
					continue;

				m.combineColumns(j,i,u);
				m.combineRows(j,i,u);
				
				if( m.get(i,i) == z )
				{
					m.combineColumns(j,i,d);
					m.combineRows(j,i,d);
				}
			}
			
			int a = f.negative(f.inverse(m.get(i,i)));
			for(int j = i+1; j < m.getRows(); ++j)
			{
				int b = f.product(a, m.get(i,j));
				m.combineColumns(i,j,b);
				m.combineRows(i,j,b);
			}
		}
	}
	
	public static String printVector(Matrix a, int row)
	{
		String s = "(";
	
		for(int j = 0; j < a.getColumns(); ++j)
		{
			if( j != 0 )
				s += ",";
			
			s += field.toString(a.getElem(row,j), 'n');
		}

		return s + ")";
	}

	public static String printMatrix(Matrix a)
	{
		String s = "\\begin{pmatrix}";
	
		for(int i = 0; i < a.getRows(); ++i)
		{
			if( i != 0 )
				s += " \\\\ ";
			
			for(int j = 0; j < a.getColumns(); ++j)
			{
				if( j != 0 )
					s += "&";
				
				s += field.toString(a.getElem(i,j), 'n');
			}
		}
		return s + "\\end{pmatrix}";
	}

	public static String printVariableCoefficient(Object a, char style)
	{
		if( field.areEquals(a, field.unitElement()) )
			return style == 'p' ? "+" : "";
		else if( field.areEquals(a, field.negative(field.unitElement())) )
			return "-";

		return field.toString(a, style);
	}
	
	public static String printQuadraticForm(Matrix m)
	{
		String s = "";
		Field f = m.getField();
		int z = f.zero();
		
		for(int i = 0; i < m.getRows(); ++i)
			for(int j = i; j < m.getColumns(); ++j)
			{
				if( m.get(i,j) == z )
					continue;
				
				int b = m.get(i,j);
				if( i != j )
					b = f.sum(b,b);
				
				s += printVariableCoefficient(f.getElement(b), s.length() == 0 ? 'n' : 'p');
				
				if( i == j )
					s += "x_" + (i+1) + "^2";
				else
					s += "x_" + (i+1) + "x_" + (j+1);
			}
		
		if( s.length() == 0 )
			s = "0";
		
		return s;
	}
	
	public static String printDefinite(Matrix m)
	{
		int p = 0; 
		int z = 0;
		int n = 0;
		
		for(int i = 0; i < m.getRows(); ++i)
		{
			int s = field.signum(m.getElem(i,i));
			
			if( s == 0 )
				++z;
			else if ( s > 0 )
				++p;
			else
				++n;
		}
		
		if( p == 0 && n == 0 )
			return "nulla";
		else if( n == 0 && z == 0 )
			return "pozitív definit";
		else if( p == 0 && z == 0 )
			return "negatív definit";
		else if( n == 0 )
			return "pozitív szemidefinit";
		else if( p == 0 )
			return "negatív szemidefinit";
		else
			return "indefinit";
	}
	
	public static void printProblemOne(String t, Matrix a, Matrix b)
	{
		System.out.println("\\item{" + t + "} A $q=" + printQuadraticForm(a) + "$ kvadratikus alak " + 
				printDefinite(b));
	}
	
	public static void problemOneGood(String definit)
	{
		for(int i = 0; i < 25; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomizeSymmetric(a);

			Matrix b = new Matrix(a);
			diagonalize(b);

			if( a.equals(b) )
				continue;
			
			if( ! printDefinite(b).equals(definit) )
				continue;
			
			printProblemOne("t", a, b);
			++i;
		}
	}
	
	public static void printProblemOne(String t, Matrix a, String s)
	{
		System.out.println("\\item{" + t + "} A $q=" + printQuadraticForm(a) + "$ kvadratikus alak " + s);
	}
	
	public static void problemOneBad(String definit)
	{
		for(int i = 0; i < 25; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomizeSymmetric(a);
		
			Matrix b = new Matrix(a);
			diagonalize(b);

			if( a.equals(b) )
				continue;
			
			if( ! printDefinite(b).equals(definit) )
				continue;
			
			String s;
			int d = (int)(Math.random() * 5);
			if( d == 0 )
				s = "pozitív definit";
			else if( d == 1 )
				s = "negatív definit";
			else if( d == 2 )
				s = "pozitív szemidefinit";
			else if( d == 3 )
				s = "negatív szemidefinit";
			else
				s = "indefinit";
		
			if( definit.equals(s) )
				continue;

			printProblemOne("f", a, s);
			++i;
		}
	}

	public static int innerProduct(Matrix m, int row1, int row2)
	{
		Field f = m.getField();
		int a = f.zero();
		
		for(int i = 0; i < m.getColumns(); ++i)
			a = f.sum(a, f.product(m.get(row1,i), m.get(row2,i)));
		
		return a;
	}

	public static void problemTwoGood()
	{
		for(int i = 0; i < 120; )
		{
			Matrix m = new Matrix(field, 2, 2);
			randomize(m);
		
			if( innerProduct(m,0,0) == field.zero() 
					|| innerProduct(m,1,1) == field.zero() )
				continue;
			
			Matrix b = new Matrix(m);
			b.combineRows(0, 1, field.product(field.negative(innerProduct(b,0,1)), 
					field.inverse(innerProduct(b,0,0)))); 

			if( innerProduct(b,1,1) == field.zero() )
				continue;
	
			System.out.println("\\item{t} $v_1=" + printVector(m,0) + "$, $v_2=" + printVector(m,1) 
					+ "$, $v'_2=" + printVector(b,1) + "$");			
			
			++i;
		}
	}
	
	public static void problemTwoBad()
	{
		for(int i = 0; i < 120; )
		{
			Matrix m = new Matrix(field, 2, 2);
			randomize(m);
		
			if( innerProduct(m,0,0) == field.zero() 
					|| innerProduct(m,1,1) == field.zero() )
				continue;
			
			Matrix b = new Matrix(m);
			b.combineRows(0, 1, field.product(field.negative(innerProduct(b,0,1)), 
					field.inverse(innerProduct(b,0,0)))); 

			if( innerProduct(b,1,1) == field.zero() )
				continue;
	
			Matrix c = new Matrix(m);
			randomize(c);
			c.set(0,0,m.get(0,0));
			c.set(0,1,m.get(0,1));
			c.combineRows(0, 1, field.product(field.negative(innerProduct(c,0,1)), 
					field.inverse(innerProduct(c,0,0)))); 

			if( innerProduct(c,1,1) == field.zero() )
				continue;

			if( printVector(b,1).equals(printVector(c,1)) )
				continue;
			
			System.out.println("\\item{f} $v_1=" + printVector(m,0) + "$, $v_2=" + printVector(m,1) 
					+ "$, $v'_2=" + printVector(c,1) + "$");			
			
			++i;
		}
	}

	public static void main(String[] args)
	{
/*
		problemOneGood("pozitív definit");
		problemOneGood("negatív definit");
		problemOneGood("pozitív szemidefinit");
		problemOneGood("negatív szemidefinit");
		problemOneGood("indefinit");
		problemOneBad("pozitív definit");
		problemOneBad("negatív definit");
		problemOneBad("pozitív szemidefinit");
		problemOneBad("negatív szemidefinit");
		problemOneBad("indefinit");
*/
//		problemTwoGood();
		problemTwoBad();
	}
}
