
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;
import org.mmaroti.ua.util.*;

public class LinearMaps 
{
	static Rationals field = new Rationals();
	
	static void randomize(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				int d = 0;
				if( Math.random() < 0.6 )
					d = (int)(Math.random() * 7) - 3;
				
				matrix.setElem(i, j, field.parse("" + d));
			}
	}
	
	static boolean isZeroRow(Matrix matrix, int row)
	{
		for(int i = 0; i < matrix.getColumns(); ++i)
			if( field.zero() != matrix.get(row, i) )
				return false;
		
		return true;
	}
	
	static int getNonZeroRowCount(Matrix matrix)
	{
		int i = 0;
		while( i < matrix.getRows() && ! isZeroRow(matrix, i) )
			++i;
		
		return i;
	}
	
	static Matrix permuteRows(Matrix matrix)
	{
		int rows = matrix.getRows();
		
		PermArgument perm = new PermArgument(rows);
		perm.setIndex((int)(Math.random() * perm.getMaxIndex()));
		
		Matrix p = new Matrix(matrix.getField(), rows, rows);
		for(int i = 0; i < rows; ++i)
			p.set(i, perm.vector[i], matrix.getField().unit());
		
		return p.product(matrix); 
	}
	
	static String printRows(Matrix matrix)
	{
		String s = "";

		for(int i = 0; i < matrix.getRows(); ++i)
		{
			if( i != 0 )
				s += ",\\ ";
			
			s += "(";
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				if( j != 0 )
					s += ", ";
				
				s += matrix.getField().toString(matrix.getField().getElement(matrix.get(i,j)));
			}
				
			s += ")";
		}
		
		return s;
	}

	static String getAz(String string)
	{
		int i, c = ' ';
		for(i = 0; i < string.length(); ++i)
		{
			c = string.charAt(i);
			if( Character.isDigit(c) )
				break;
		}
		
		return (c == '1' || c == '5') ? "z" : "";
	}

	static Matrix removeZeroRows(Matrix matrix)
	{
		int c = 0;
		for(int i = 0; i < matrix.getRows(); ++i)
			if( ! isZeroRow(matrix, i) )
				++c;
		
		Matrix p = new Matrix(matrix.getField(), c, matrix.getRows());

		c = 0;
		for(int i = 0; i < matrix.getRows(); ++i)
			if( ! isZeroRow(matrix, i) )
				p.set(c++, i, 1);
		
		return p.product(matrix);
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

	public static String printBase(Matrix b)
	{
		String s = "";
		
		for(int i = 0; i < b.getRows(); ++i)
		{
			if( i != 0 )
				s += ", ";
			
			s += "$v_" + (i+1) + "=" + printVector(b, i) + "$";
		}
		
		return s;
	}

	public static String printMatrix(Matrix a)
	{
		String s = "$A = \\begin{pmatrix}";
	
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
		return s + "\\end{pmatrix}$";
	}

	public static String printEquations(Matrix matrix)
	{
		String s = "\\{\\, (";
		for(int i = 0; i < matrix.getColumns(); ++i)
		{
			if( i != 0 )
				s += ", ";
			
			s += "x_" + (i+1);
		}
		s += ") : ";
		
		for(int i = 0; i < matrix.getRows(); ++i)
		{
			if( i != 0 )
				s += ",\\ ";
			
			boolean was = false;
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				if( matrix.get(i, j) != matrix.getField().zero() )
				{
					if( was )
						s += "+";
				
					if( matrix.get(i,j) != matrix.getField().unit() )
						s += "" + matrix.get(i, j);
					s += "x_" + (j+1);
					
					was = true;
				}
			}
			
			s += "=0";
		}
		
		s += " \\,\\}";
		return s;
	}

	public static void printProblemOne(String t, Matrix b, Matrix a, Matrix v, Matrix w)
	{
		System.out.println("\\item{" + t + "} " + printBase(b) + ", " + printMatrix(a) + ", $$" + printVector(v,0) + "\\varphi=" + printVector(w,0) + "$$");
	}
	
	public static void problemOneGood()
	{
		for(int i = 0; i < 120; )
		{
			Matrix b = new Matrix(field, 2, 2);
			randomize(b);
		
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix v = new Matrix(field, 1, 2);
			randomize(v);
			if( isZeroRow(v,0) )
				continue;
			
			Matrix c = b.inverse();
			if( c == null )
				continue;

			Matrix w = v.product(c).product(a).product(b);

			printProblemOne("t", b, a, v, w);
			
			++i;
		}
	}
	
	public static void problemOneBad()
	{
		for(int i = 0; i < 120; )
		{
			Matrix b = new Matrix(field, 2, 2);
			randomize(b);
		
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix v = new Matrix(field, 1, 2);
			randomize(v);
			if( isZeroRow(v,0) )
				continue;
			
			Matrix c = b.inverse();
			if( c == null )
				continue;

			Matrix w = v.product(c).product(a).product(b);
			Matrix w2 = v.product(b).product(a).product(c);
			if( w.equals(w2) )
				continue;

			printProblemOne("f", b, a, v, w2);
			
			++i;
		}
	}

	public static String getMultiplier(Object elem, String map)
	{
		Object zero = field.zeroElement();
		Object one = field.unitElement();
		Object negativeOne = field.negative(one);
		
		if( field.areEquals(elem, zero) )
			return "";
		else if( field.areEquals(elem, one) )
			return "+"+map;
		else if( field.areEquals(elem, negativeOne) )
			return "-"+map;
		else if( field.signum(elem) > 0 )
			return "+" + field.toString(elem, 'n') + map;
		else
			return field.toString(elem, 'n') + map;
	}
	
	public static void problemTwoGoodBad(char type)
	{
		for(int n = 0; n < 120; )
		{
			Matrix a = new Matrix(field, 2, 2);
			a.setElem(0, 0, field.parse("1"));
			a.setElem(0, 1, field.parse("0"));
			a.setElem(1, 0, field.parse("0"));
			a.setElem(1, 1, field.parse("0"));
	
			Matrix b = new Matrix(field, 2, 2);
			b.setElem(0, 0, field.parse("1"));
			b.setElem(0, 1, field.parse("2"));
			b.setElem(1, 0, field.parse("3"));
			b.setElem(1, 1, field.parse("4"));
	
			Matrix c = new Matrix(field, 2, 2);
			c.setElem(0, 0, field.parse("0"));
			c.setElem(0, 1, field.parse("1"));
			c.setElem(1, 0, field.parse("-1"));
			c.setElem(1, 1, field.parse("0"));
			
			Matrix r = new Matrix(field, 1, 6);
			randomize(r);
			
			int rc = 0;
			for(int j = 0; j < r.getColumns(); ++j)
				if( r.get(0,j) != field.zero() )
					++rc;
			
			if( rc != 2 )
				continue;
		
			String s = "";
			s += getMultiplier(r.getElem(0,0), "\\alpha");
			s += getMultiplier(r.getElem(0,1), "\\beta");
			s += getMultiplier(r.getElem(0,2), "\\gamma");
			s += getMultiplier(r.getElem(0,3), "\\alpha\\beta");
			s += getMultiplier(r.getElem(0,4), "\\beta\\gamma");
			s += getMultiplier(r.getElem(0,5), "\\gamma^2");
			if( s.startsWith("+") )
				s = s.substring(1);

			Matrix p;
			p = a.product(r.get(0,0));
			p = p.sum(b.product(r.get(0,1)));
			p = p.sum(c.product(r.get(0,2)));
			p = p.sum(a.product(b).product(r.get(0,3)));
			p = p.sum(b.product(c).product(r.get(0,4)));
			p = p.sum(c.product(c).product(r.get(0,5)));
			
			Matrix rr = new Matrix(field, 1, 6);
			randomize(rr);
			
			rc = 0;
			for(int j = 0; j < r.getColumns(); ++j)
				if( rr.get(0,j) != field.zero() )
					++rc;
			
			if( rc != 2 )
				continue;
			
			Matrix q;
			q = a.product(rr.get(0,0));
			q = q.sum(b.product(rr.get(0,1)));
			q = q.sum(c.product(rr.get(0,2)));
			q = q.sum(a.product(b).product(rr.get(0,3)));
			q = q.sum(b.product(c).product(rr.get(0,4)));
			q = q.sum(c.product(c).product(rr.get(0,5)));
			
			if( p.equals(q) )
				continue;
			
			if( type == 't' )
				System.out.println("\\item{t} $\\varphi=" + s + "$, " + printMatrix(p) );
			else
				System.out.println("\\item{f} $\\varphi=" + s + "$, " + printMatrix(q) );
			
			++n;
		}
	}
	
	public static void main(String[] args)
	{
//		problemOneGood();
//		problemOneBad();
		problemTwoGoodBad('t');
		problemTwoGoodBad('f');
	}
}
