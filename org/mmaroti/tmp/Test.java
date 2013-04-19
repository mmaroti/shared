package org.mmaroti.tmp;

import org.mmaroti.ua.math.*;

public class Test
{
	static void randomize(Matrix matrix)
	{
		for(int i = 0; i < matrix.getRows(); ++i)
			for(int j = 0; j < matrix.getColumns(); ++j)
			{
				int d = 0;
				if( Math.random() < 0.8 )
					d = (int)(Math.random() * 19.0)-9;
				
				matrix.set(i, j, matrix.getField().getIndex(new Double(d)));
			}
	}

	static double get(Matrix matrix, int i, int j)
	{
		return ((Double)matrix.getField().getElement(matrix.get(i,j))).doubleValue();
	}

	static int geti(Matrix matrix, int i, int j)
	{
		return Math.round((float)get(matrix,i,j));
	}

	static boolean isIntegral(Matrix matrix)
	{
		for(int r = 0; r < matrix.getRows(); ++r)
			for(int c = 0; c < matrix.getColumns(); ++c)
				if( Math.abs(get(matrix,r,c) - Math.round(get(matrix,r,c))) > 0.0001 )
					return false;
		
		return true;
	}
	
	static boolean isNaN(Matrix matrix)
	{
		for(int r = 0; r < matrix.getRows(); ++r)
			for(int c = 0; c < matrix.getColumns(); ++c)
				if( Double.isNaN(get(matrix,r,c)) || Double.isInfinite(get(matrix,r,c)) )
					return true;
		
		return false;
	}
	
	static String print(Matrix matrix)
	{
		String s = "\\begin{pmatrix} ";
		for(int r = 0; r < matrix.getRows(); ++r)
		{
			if( r != 0 )
				s += " \\\\ ";

			for(int c = 0; c < matrix.getColumns(); ++c)
			{
				if( c != 0 )
					s += " & ";

				s += geti(matrix,r,c);
			}
		}
		s += " \\end{pmatrix}";
		
		return s;
	}
	
	static String printe(Matrix matrix)
	{
		int i = (int)(Math.random()*matrix.getRows());
		int j = (int)(Math.random()*matrix.getColumns());
		
		String s = "\\begin{pmatrix} ";
		for(int r = 0; r < matrix.getRows(); ++r)
		{
			if( r != 0 )
				s += " \\\\ ";

			for(int c = 0; c < matrix.getColumns(); ++c)
			{
				if( c != 0 )
					s += " & ";

				if( r == i && c == j )
					s += geti(matrix,r,c);
				else
					s += "?";
			}
		}
		s += " \\end{pmatrix}";
		
		return s;
	}

	static String printn(Matrix matrix)
	{
		int i = (int)(Math.random()*matrix.getRows());
		int j = (int)(Math.random()*matrix.getColumns());
		
		String s = "\\begin{pmatrix} ";
		for(int r = 0; r < matrix.getRows(); ++r)
		{
			if( r != 0 )
				s += " \\\\ ";

			for(int c = 0; c < matrix.getColumns(); ++c)
			{
				if( c != 0 )
					s += " & ";

				if( r == i && c == j )
				{
					if( Math.random() < 0.5 )
						s += Math.round(get(matrix,r,c) - 1 - Math.random()*3);
					else
						s += Math.round(get(matrix,r,c) + 1 + Math.random()*3);
				}
				else
					s += "?";
			}
		}
		s += " \\end{pmatrix}";
		
		return s;
	}

	static String printVar(Matrix m, int i, int j, String var)
	{
		int a = geti(m, i, j);
		
		String s = "";
		if( var.charAt(0) == '+' )
		{
			var = var.substring(1);
			
			if( a >= 0 )
				s = "+";
		}

		if( var.length() != 0 && a == 0 )
			return "";

		if( var.length() != 0 && a == 1 )
			return s + var;
		
		if( var.length() != 0 && a == -1 )
			return "-" + var;
		
		return s + Integer.toString(a) + var;
	}
	
	static String printEqu(Matrix m, int i)
	{
		String s = "";
		for(int j = 0; j < m.getColumns()-1; ++j)
		{
			String var = "" + (char)('x'+j);

			if( s.length() > 0 )
				var = "+" + var;
			
			s += printVar(m,i,j,var);
		}
		
		if( s.equals("") )
			s = "0";
		
		return s + "&=" + geti(m,i,m.getColumns()-1);
	}
	
	static int solutionCount(Matrix m)
	{
		outer: for(int i = 0; i < m.getRows(); ++i)
		{
			for(int j = 0; j < m.getColumns()-1; ++j)
				if( geti(m,i,j) != 0 )
					continue outer;
			
			if( geti(m,i,m.getColumns()-1) != 0 )
				return 0;
		}

		for(int i = 0; i < Math.min(m.getRows(),m.getColumns()-1); ++i)
		{
			if( geti(m,i,i) == 0 )
				return 2;
		}
	
		return 1;
	}

	static boolean hasZeroEqu(Matrix m)
	{
		int c = 0;
		
		outer: for(int i = 0; i < m.getRows(); ++i)
		{
			if( geti(m,i,m.getColumns()-1) == 0 )
				++c;
			
			for(int j = 0; j < m.getColumns()-1; ++j)
				if( geti(m,i,j) != 0 )
					continue outer;
			
			return true;
		}
	
		return c > 0;
	}
	
	public static void main(String[] _)
	{
		for(int x = 0; x < 10;)
		{
			Matrix a = new Matrix(new Reals(), 4, 4);
			randomize(a);

			if( hasZeroEqu(a) )
				continue;
			
			Matrix b = new Matrix(a);
			
			b.doGaussElimination();
			if( isNaN(b) )
				continue;

			if( solutionCount(b) == 2 )
			{
				System.out.println("\\item{t} Az \\begin{align*}" + printEqu(a,0) + "\\\\" + printEqu(a,1) + "\\\\" + printEqu(a,2) 
					+ "\\end{align*} lineáris egyenletrendszernek nincs megoldása + $" + print(b) + "$.");

				++x;
			}
		}
	}
}