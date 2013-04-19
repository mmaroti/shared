/*
 * Created on Aug 7, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;
import Jama.*;

/**
 * This class solves systems of linear equations.
 * @author mmaroti@math.u-szeged.hu
 */
public class LinearEquations
{
	/**
	 * Holds a map from variable names to variable indices
	 */
	protected TreeMap<String,Integer> variables = new TreeMap<String,Integer>();

	/**
	 * Get the index of a variable with this method.
	 */
	protected int getVariable(String name)
	{
		if( variables.containsKey(name) )
			return variables.get(name);

		int n = variables.size();
		variables.put(name, n);
		return n;
	}

	/**
	 * Returns the set of variable names in the system. 
	 */
	public Set<String> getVariables()
	{
		return variables.keySet();
	}

	/**
	 * Represents one equation of the system.
	 * Create the equation, set the coefficient of
	 * specific elements and then set the constant.
	 */	
	public class Equation
	{
		protected Equation()
		{
			coefficients = new double[LinearEquations.this.variables.size() + 10];
		}

		protected int getVariableIndex(String name)
		{
			int n = LinearEquations.this.getVariable(name);
			if( coefficients.length <= n )
			{
				double[] c = new double[n + 10];
				System.arraycopy(coefficients, 0, c, 0, coefficients.length);
				coefficients = c;
			}
			return n;
		}

		public double getCoefficient(String name)
		{
			int n = getVariableIndex(name);
			return coefficients[n];
		}

		public void setCoefficient(String name, double value)
		{
			int n = getVariableIndex(name);
			coefficients[n] = value;
		}
		
		public void addCoefficient(String name, double value)
		{
			int n = getVariableIndex(name);
			coefficients[n] += value;
		}
		
		public void subCoefficient(String name, double value)
		{
			int n = getVariableIndex(name);
			coefficients[n] += value;
		}
		
		public double getConstant()
		{
			return constant;
		}
		
		public void setConstant(double value)
		{
			constant = value; 
		}

		public void addConstant(double value)
		{
			constant += value;
		}

		public void subConstant(double value)
		{
			constant -= value;
		}

		/**
		 * Multiplies each coefficient and the constant with this value.
		 * The new equation will hold if and only if the original does,
		 * but with the multiplied version can count with different strength
		 * in a least square solutions.
		 */
		public void multiply(double value)
		{
			for(int i = 0; i < coefficients.length; ++i)
				coefficients[i] *= value;
			
			constant *= value;
		}

		/**
		 * Returns the difference of the constant and the left hand side
		 * containing the variables.
		 */
		public double getSignedError(Solution solution)
		{
			double values[] = solution.values;
			double e = constant;

			int i = coefficients.length;
			if( values.length < i )
				i = values.length;
				
			while( --i >= 0 )
				e -= coefficients[i] * values[i];

			return e;				
		}

		public double getAbsoluteError(Solution solution)
		{
			return Math.abs(getSignedError(solution));
		}

		/**
		 * Returns the value of the left hand side. This
		 * value does not depend on the constant.
		 */
		public double getLeftHandSide(Solution solution)
		{
			return constant - getSignedError(solution);
		}
		
		protected double[] coefficients;
		protected double constant;
	}

	/**
	 * Returns a new empty equation
	 */
	public Equation createEquation()
	{
		return new Equation();
	}

	/**
	 * Holds the list of equations.
	 */
	protected List<Equation> equations = new ArrayList<Equation>();

	/**
	 * Returns the list of equations in the system.
	 */
	public List<Equation> getEquations()
	{
		return equations;
	}

	/**
	 * Creates equations first, then add them to the system
	 * using this method.
	 */
	public void addEquation(Equation equation)
	{
		equations.add(equation);
	}

	/**
	 * Removes one equation from the set of equations. Be careful,
	 * as the number of variables will not decrease and can result
	 * in underdetermined system
	 */
	public void removeEquation(Equation equation)
	{
		equations.remove(equation);
	}

	/**
	 * Remove all equations and variables.
	 */
	public void clear()
	{
		variables.clear();
		equations.clear();
	}
	
	/**
	 * Prints some statistics on the list of equations and variables.
	 */
	public void printStatistics()
	{
		System.out.println("unknowns: " + variables.size() + ", equations: " + equations.size());
	}

	/**
	 * This class represents a solution to the system of linear equations.
	 */
	public class Solution
	{
		public double[] values;

		/**
		 * Use this method to get the value of a variable in the solution,
		 * or returns <code>NaN</code> if the variable does not occur in
		 * the solution.
		 */		
		public double getValue(String name)
		{
			if( variables.containsKey(name) )
				return values[variables.get(name)];
				
			return Double.NaN;
		}

		protected Solution(Matrix X)
		{
			this.values = new double[variables.size()];

			if( X.getColumnDimension() != 1 || X.getRowDimension() != values.length )
				throw new IllegalArgumentException();
			
			for(int i = 0; i < values.length; ++i)
				values[i] = X.get(i,0);
		}

		/**
		 * Prints out the solution and some statistics on the errors.
		 */		
		public void print()
		{
			Iterator<String> iter = variables.keySet().iterator();
			while( iter.hasNext() )
			{
				String name = iter.next();
				int index = variables.get(name);
				System.out.println(name + " = " + values[index]);
			}
			System.out.println("Average Error " + getAverageError());
			System.out.println("Maximum Error " + getMaximumError());
		}

		/**
		 * Returns the average error in the system of equations.
		 */	
		public double getAverageError()
		{
			double d = 0.0;
		
			Iterator<Equation> iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = iter.next();
				d += equation.getAbsoluteError(this);
			}
		
			return d / equations.size();
		}

		/**
		 * Returns the maximum error in the system of equations.
		 */	
		public double getMaximumError()
		{
			double d = 0.0;
		
			Iterator<Equation> iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = iter.next();
				double e = equation.getAbsoluteError(this);
				if( e > d )
					d = e;
			}
		
			return d;
		}
		
		public Equation getMaximumErrorEquation()
		{
			double d = -1.0;
			Equation a = null;
		
			Iterator<Equation> iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = iter.next();
				double e = equation.getAbsoluteError(this);
				if( e > d )
				{
					d = e;
					a = equation;
				}
			}
		
			return a;
		}
	}
	
	/**
	 * Returns the solution where the least squares of errors 
	 * of the equations is the smallest. 
	 */
	public Solution solveLeastSquares()
	{
		Matrix A = new Matrix(equations.size(), variables.size());
		Matrix B = new Matrix(equations.size(), 1);
		
		for(int i = 0; i < equations.size(); ++i)
		{
			Equation equation = equations.get(i);

			int j = equation.coefficients.length;
			if( j > variables.size() )
				j = variables.size();
			while( --j >= 0 )
				A.set(i, j, equation.coefficients[j]);

			B.set(i, 0, equation.constant);
		}

		Matrix X = A.solve(B);
		
		return new Solution(X);
	}

	/**
	 * Returns a solution of a system of equations where the equations 
	 * are (almost) linearly dependent. Normalize singular values
	 * that are larger than <code>alpha</code>.
	 */
	public Solution solveWithSVD(double alpha)
	{
		int M = equations.size();
		int N = variables.size();
		
		Matrix A = new Matrix(M,N);
		Matrix B = new Matrix(M,1);
		
		for(int i = 0; i < M; ++i)
		{
			Equation equation = equations.get(i);

			int j = equation.coefficients.length;
			if( j > N )
				j = N;
			while( --j >= 0 )
				A.set(i, j, equation.coefficients[j]);

			B.set(i, 0, equation.constant);
		}

		SingularValueDecomposition svd = A.svd();

		Matrix V = svd.getV();

		double[] singularValues = svd.getSingularValues();
		alpha *= singularValues[0];

		Matrix X = new Matrix(N,N);
		for(int j = 0; j < N && singularValues[j] > alpha; ++j)
		{
			double a = 1.0 / singularValues[j];
			for(int i = 0; i < N; ++i)
				X.set(i,j, V.get(i,j) * a);
		}
		
		X = X.times(svd.getU().transpose().times(B));
				
		return new Solution(X);
	}
}
