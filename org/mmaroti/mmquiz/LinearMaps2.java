
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;
import org.mmaroti.ua.util.*;

public class LinearMaps2 
{
	static Rationals field = new Rationals();
	
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

	public static String printVariableCoefficient(Object a, char style)
	{
		if( field.areEquals(a, field.unitElement()) )
			return style == 'p' ? "+" : "";
		else if( field.areEquals(a, field.negative(field.unitElement())) )
			return "-";

		return field.toString(a, style);
	}
	
	public static String printLinCombination(Object a, Object b)
	{
		if( field.areEquals(a, field.zeroElement()) )
		{
			if( field.areEquals(b, field.zeroElement()) )
				return "0";

			return printVariableCoefficient(b, 'n') + "y";
		}
		else if( field.areEquals(b, field.zeroElement()) )
			return printVariableCoefficient(a, 'n') + "x";
		else
			return printVariableCoefficient(a, 'n') + "x" 
				+ printVariableCoefficient(b, 'p') + "y";
	}

	public static String printLinearTransformation(Matrix m)
	{
		return "(x,y)\\varphi = (" + printLinCombination(m.getElem(0,0), m.getElem(1,0))
			+ "," + printLinCombination(m.getElem(0,1), m.getElem(1,1)) + ")";
	}
	
	public static String printCharPolynom(Matrix m)
	{
		String s = "\\lambda^2";
		
		Object b = field.negative(field.sum(m.getElem(0,0), m.getElem(1,1)));
		Object c = field.sum(field.product(m.getElem(0,0), m.getElem(1,1)),
				field.negative(field.product(m.getElem(0,1), m.getElem(1,0))));

		if( ! field.areEquals(b, field.zeroElement()) )
			s += printVariableCoefficient(b, 'p') + "\\lambda";
		
		if( ! field.areEquals(c, field.zeroElement()) )
			s += field.toString(c, 'p');

		return s;
	}
	
	public static void printProblemOne(String t, Matrix a, Matrix b)
	{
		System.out.println("\\item{" + t + "} $" + printLinearTransformation(a) + "$, $f=" + printCharPolynom(b) + "$");
	}
	
	public static void problemOneGood()
	{
		for(int i = 0; i < 120; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
		
			printProblemOne("t", a, a);
			++i;
		}
	}
	
	public static void problemOneBad()
	{
		for(int i = 0; i < 60; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix b = new Matrix(field, 2, 2);
			randomize(b);
		
			if( printCharPolynom(a).equals(printCharPolynom(b)) )
				continue;
			
			printProblemOne("f", a, b);
			++i;
		}

		for(int i = 0; i < 60; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix b = new Matrix(a);
			
			int x = (int)(Math.random() * 2);
			int y = (int)(Math.random() * 2);
			b.setElem(x, x, field.negative(b.getElem(x, y)));
		
			if( printCharPolynom(a).equals(printCharPolynom(b)) )
				continue;
			
			printProblemOne("f", a, b);
			++i;
		}
	}
	
	public static void printProblemTwo(String t, Matrix a, Matrix b)
	{
		System.out.println("\\item{" + t + "} " + printMatrix(a) + ", $v=" + printVector(b,0) + "$");
	}
	
	public static void problemTwoGood()
	{
		for(int i = 0; i < 120; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix b = new Matrix(field, 1, 2);
			randomize(b);
			
			if( field.signum(b.getElem(0,0)) == 0 || field.signum(b.getElem(0,1)) == 0 )
				continue;
			
			Matrix c = b.product(a);

			if( ! field.areEquals(field.product(c.getElem(0,0), b.getElem(0,1)), 
					field.product(c.getElem(0,1), b.getElem(0,0))) )
				continue;
		
			printProblemTwo("t", a, b);
			++i;
		}
	}
	
	public static void problemTwoBad()
	{
		for(int i = 0; i < 120; )
		{
			Matrix a = new Matrix(field, 2, 2);
			randomize(a);
			
			Matrix b = new Matrix(field, 1, 2);
			randomize(b);
			
			if( field.signum(b.getElem(0,0)) == 0 || field.signum(b.getElem(0,1)) == 0 )
				continue;
			
			Matrix c = b.product(a);

			if( field.areEquals(field.product(c.getElem(0,0), b.getElem(0,1)), 
					field.product(c.getElem(0,1), b.getElem(0,0))) )
				continue;
		
			printProblemTwo("f", a, b);
			++i;
		}
	}

	public static void main(String[] args)
	{
		problemOneGood();
		problemOneBad();
//		problemTwoGood();
//		problemTwoBad();
	}
}
