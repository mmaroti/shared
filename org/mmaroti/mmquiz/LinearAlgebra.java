
package org.mmaroti.mmquiz;

import org.mmaroti.ua.math.*;
import org.mmaroti.ua.util.*;
import java.util.*;

public class LinearAlgebra 
{
	static Field field = new FiniteField(2);
	static Field field2 = new FiniteField(3);
	
	static void randomize(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				int d = 0;
				if( Math.random() < 0.6 )
					d = (int)(Math.random() * matrix.getField().character());
				
				matrix.set(i, j, d);
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

	static String printVectors(Collection<Matrix> vectors)
	{
		String s = "";

		for(Matrix vector : vectors)
		{
			if( vector.getRows() != 1 )
				throw new IllegalArgumentException();
			
			if( s.length() != 0 )
				s += ",\\ ";
			
			s += "(";
			for(int j = 0; j < vector.getColumns(); ++j)
			{
				if( j != 0 )
					s += ", ";
				
				s += vector.getField().toString(vector.getElem(0,j));
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

	public static boolean nextMatrix(Matrix m)
	{
		Field field = m.getField();
		int zero = field.zero();
		int unit = field.unit();
		
		for(int i = 0; i < m.getRows(); ++i)
			for(int j = 0; j < m.getColumns(); ++j)
			{
				int a = m.get(i, j);
				a = field.sum(a, unit);
				m.set(i,j, a);
				if( a != zero )
					return true;
			}
		
		return false;
	}

	public static Set<Matrix> getSubspace(Matrix m)
	{
		Set<Matrix> vectors = new HashSet<Matrix>();
		
		Matrix coef = new Matrix(m.getField(), 1, m.getRows());
		coef.clear();
		
		do
		{
			vectors.add(coef.product(m));
		} while( nextMatrix(coef) );
		
		return vectors;
	}
	
	public static String printSubspace(Matrix m)
	{
		return printVectors(getSubspace(m));
	}
	
	public static void problemZeroGood()
	{
		for(int i = 0; i < 40; )
		{
			Matrix m = new Matrix(field, 3, 3);
			randomize(m);
		
			Matrix m2 = new Matrix(m);
			m2.doGaussElimination();
			m2 = removeZeroRows(m2);
			m2 = permuteRows(m2);

			if( m2.getRows() != 2 )
				continue;
			
			String s = printSubspace(m);
			String s2 = printRows(m2);
		
			System.out.println("\\item{t}  $[" + s2 
				+ "] = \\{" + s + "\\}$.");
			
			++i;
		}
	}
	
	public static void problemZeroBad()
	{
		for(int i = 0; i < 40; )
		{
			Matrix m = new Matrix(field, 3, 3);
			randomize(m);
		
			Matrix m2 = new Matrix(m);
			m2.doGaussElimination();
			m2 = removeZeroRows(m2);
			m2 = permuteRows(m2);

			if( m2.getRows() != 2 )
				continue;
	
			List<Matrix> vectors = new ArrayList<Matrix>();
			vectors.addAll(getSubspace(m));

			Matrix bad;
			do
			{
				bad = new Matrix(field, 1, 3);
				randomize(bad);
			} while( vectors.contains(bad) );

			int pos = (int)Math.floor(Math.random() * vectors.size());
			vectors.set(pos, bad);
			
			System.out.println("\\item{f}  $[" + printRows(m2) 
				+ "] = \\{" + printVectors(vectors) + "\\}$.");
			
			++i;
		}
	}
	
	public static void problemOneGood(int subsize)
	{
		for(int i = 0; i < 60; )
		{
			Matrix m = new Matrix(field, 3, 3);
			randomize(m);
		
			Matrix m2 = new Matrix(m);
			m2.doGaussElimination();
			m2 = removeZeroRows(m2);
			m2 = permuteRows(m2);

			if( m2.getRows() != subsize )
				continue;
			
			String s = printRows(m);
			String s2 = printRows(m2);
		
			System.out.println("\\item{t}  $U = [" + s 
				+ "]$, \\\\ $\\bar r = " + s2 + "$.");
			
			++i;
		}
	}
	
	public static void problemOneBad1()
	{
		for(int i = 0; i < 60; )
		{
			Matrix m = new Matrix(field, 3, 3);
			randomize(m);
		
			Matrix m2 = new Matrix(m);
			m2.doGaussElimination();
			m2 = removeZeroRows(m2);
			
			Matrix m3 = new Matrix(m.getField(), 1, m2.getRows());
			randomize(m3);
			m3 = m3.product(m2);
			m2 = m2.transpose().concatenate(m3.transpose()).transpose();
			
			m2 = permuteRows(m2);

			if( m2.getRows() != 2 )
				continue;
			
			String s = printRows(m);
			String s2 = printRows(m2);
		
			System.out.println("\\item{f}  $U = [" + s 
				+ "]$, \\\\ $\\bar r = " + s2 + "$.");
			
			++i;
		}
	}

	public static void problemOneBad2()
	{
		for(int i = 0; i < 60; )
		{
			Matrix m = new Matrix(field, 3, 3);
			randomize(m);
		
			Matrix m2 = new Matrix(m);
			m2.doGaussElimination();
			m2 = removeZeroRows(m2);
			
			if( getNonZeroRowCount(m2) != 2 )
				continue;
			
			m2 = m2.subMatrix(0, getNonZeroRowCount(m2)-1, 0, m2.getColumns());			
			m2 = permuteRows(m2);
		
			String s = printRows(m);
			String s2 = printRows(m2);
		
			System.out.println("\\item{f}  $U = [" + s 
				+ "]$, \\\\ $\\bar r = " + s2 + "$.");
			
			++i;
		}
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
	
	public static void problemTwoGoodBad(int dim, int solutionDim)
	{
		for(int i = 0; i < 60; )
		{
			Matrix m = new Matrix(field2, 4, 4);
			randomize(m);
			m = removeZeroRows(m);

			Matrix q = new Matrix(m);
			q.doGaussElimination();
			q = removeZeroRows(q);
			
			int d = q.getColumns() - q.getRows();
			if( d != dim )
				continue;
		
			System.out.println("\\item{" + (dim == solutionDim ? "t" : "f") +
					"} $U=" + printEquations(m) + "$, $\\dim(U)=" + solutionDim + "$.");
			
			++i;
		}
	}
	
	public static void main(String[] args)
	{
		problemZeroGood();
		problemZeroBad();
//		problemOneGood(2);
//		problemOneBad1();
//		problemOneBad2();
		
//		problemTwoGoodBad(1,1);
//		problemTwoGoodBad(2,2);
//		problemTwoGoodBad(1,2);
//		problemTwoGoodBad(2,1);
	}
}
