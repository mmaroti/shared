/*
 * Created on Aug 7, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;
import Jama.*;

/**
 * This class solves systems of linear equations modulo 2*PI.
 * @author mmaroti
 */
public class PhaseEquations
{
	/**
	 * All equations use this same modulo value
	 */
	public static final double MODULO = 2 * Math.PI;

	/**
	 * Returns the a congruent value in the range [-MODULO/2,MODULO/2)
	 */
	public static double roundPhase(double radian)
	{
		return radian - MODULO * Math.round(radian / MODULO);
	}
	
	/**
	 * Holds a map from variable names to variable indices
	 */
	protected TreeMap variables = new TreeMap();

	/**
	 * Get the index of a variable with this method.
	 */
	protected int getVariable(String name)
	{
		if( variables.containsKey(name) )
			return ((Integer)variables.get(name)).intValue();

		int n = variables.size();
		variables.put(name, new Integer(n));
		return n;
	}

	/**
	 * Returns the set of variable names in the system. 
	 */
	public Set getVariables()
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
			coefficients = new double[PhaseEquations.this.variables.size() + 10];
		}

		protected int getVariableIndex(String name)
		{
			int n = PhaseEquations.this.getVariable(name);
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
			constant = roundPhase(value); 
		}

		public void addConstant(double value)
		{
			constant = roundPhase(constant + value);
		}

		public void subConstant(double value)
		{
			constant = roundPhase(constant - value);
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
	protected List equations = new ArrayList();

	/**
	 * Returns the list of equations in the system.
	 */
	public List getEquations()
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
				return values[((Integer)variables.get(name)).intValue()];
				
			return Double.NaN;
		}

		public Map getValueMap()
		{
			Map assignment = new HashMap();
			
			Iterator iter = variables.entrySet().iterator();
			while( iter.hasNext() )
			{
				Map.Entry entry = (Map.Entry)iter.next();
				
				double value = values[((Integer)entry.getValue()).intValue()];
				assignment.put(entry.getKey(), new Double(value));
			}
			
			return assignment;
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
			Iterator iter = variables.keySet().iterator();
			while( iter.hasNext() )
			{
				String name = (String)iter.next();
				int index = ((Integer)variables.get(name)).intValue();
				System.out.println(name + " = " + values[index]);
			}
			System.out.println("Average Error " + getAverageError());
			System.out.println("Maximum Error " + getMaximumError());
		}

		public void printErrors()
		{
			Iterator iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = (Equation)iter.next();
				System.out.println(equation.getSignedError(this));
			}
		}

		/**
		 * Returns the average error in the system of equations.
		 */	
		public double getAverageError()
		{
			double d = 0.0;
		
			Iterator iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = (Equation)iter.next();
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
		
			Iterator iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = (Equation)iter.next();
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
		
			Iterator iter = equations.iterator();
			while( iter.hasNext() )
			{
				Equation equation = (Equation)iter.next();
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
	 * All singular values smaller than this tolerance
	 * (relative to the main component) are considered zero.
	 */
	public double TOLERANCE = 1.0E-6;

	/**
	 *  the number of equations
	 */
	protected int M;
	
	/**
	 * the number of variables
	 */
	protected int N;
	
	/**
	 * the A matrix of size M by N in the Ax = b equation
	 */
	protected Matrix A;
	
	/**
	 * the b vector of size M in the Ax = b equation
	 */
	protected Matrix B;
	
	/**
	 * The orthonormal U matrix of size M by N in the singular 
	 * value decomposition
	 */
	protected Matrix U;
	
	/**
	 * the singular values of size N
	 */
	protected double[] singularValues;
	
	/**
	 * the orthonormal V matrix of size N by N in the singular 
	 * value decomposition
	 */
	protected Matrix V;
	
	/**
	 * Creates the A and B matrices for the system of linear 
	 * equations of form Ax = b. Then calculates the singular
	 * value decomposition of A and corrects the list of
	 * singular values with the specified tolarence.
	 */
	protected void createMatrices()
	{
		M = equations.size();
		N = variables.size();
		
		A = new Matrix(M,N);
		B = new Matrix(M,1);
		
		for(int i = 0; i < M; ++i)
		{
			Equation equation = (Equation)equations.get(i);

			int j = equation.coefficients.length;
			if( j > N )
				j = N;
			while( --j >= 0 )
				A.set(i, j, equation.coefficients[j]);

			B.set(i, 0, equation.constant);
		}
		
		SingularValueDecomposition svd = A.svd();

		U = svd.getU();
		singularValues = svd.getSingularValues().clone();
		V = svd.getV();
		
		double t = TOLERANCE * singularValues[0];
		for(int i = 0; i < singularValues.length; ++i)
		{
			if( singularValues[i] < t )
				singularValues[i] = 0.0;
		}
	}
	
	/**
	 * The solution matrix S which yields the solution x = Sb 
	 * for the Ax = b equation.
	 */
	protected Matrix S;
	
	protected void createSolution()
	{
		S = new Matrix(N,N);
		for(int j = 0; j < N && singularValues[j] != 0.0 ; ++j)
		{
			double a = 1.0 / singularValues[j];
			for(int i = 0; i < N; ++i)
				S.set(i,j, V.get(i,j) * a);
		}

		S = S.times(U.transpose());
	}

	/**
	 * The correction matrix which used to get the best integer vector y so that
	 * the norm of P(b+y) is minimal. The solution is then S(b+y).
	 */
	protected Matrix P;

	double[] errorVector;
	
	protected void createCorrection()
	{
		P = new Matrix(N,N);
		for(int i = 0; i < N; ++i)
			if( singularValues[i] != 0.0 )
				P.set(i, i, 1.0);

		P = U.times(P.times(U.transpose()));
		
		for(int i = 0; i < M; ++i)
			P.set(i, i, P.get(i, i) - 1.0);
			
		errorVector = P.times(B).getColumnPackedCopy();
	}

	protected double getModifiedCorrection(int j, double alpha)
	{
		double t = 0.0;
		
		for(int i = 0; i < M; ++i)
		{
			errorVector[i] += alpha * P.get(i,j);
			t += errorVector[i] * errorVector[i];
		}
		
		B.set(j, 0, B.get(j, 0) + alpha);

		return t;
	}

	protected void optimizeCorrection()
	{ 
		double bestError = getModifiedCorrection(0, 0.0);
		
		int steps = M;
		outer: for(;;)
			for(int i = 0; i < M; ++i)
			{
				if( --steps <= 0 )
					break outer;
				
				double e = getModifiedCorrection(i, MODULO);
				if( e < bestError - 0.00001 )
				{
					bestError = e;
					steps = M; 
					continue;
				}
				
				e = getModifiedCorrection(i, -2.0 * MODULO);
				if( e < bestError - 0.00001)
				{
					bestError = e;
					steps = M; 
					continue;
				}
				
				getModifiedCorrection(i, MODULO);
			}
		
		for(int i = 0; i < M; ++i)
			((Equation)equations.get(i)).constant = B.get(i, 0);
	}

	/**
	 * Returns a solution of a system of equations where the equations 
	 * are (almost) linearly dependent. It also fixes the constants of
	 * the equations.
	 */
	public Solution solve()
	{
		createMatrices();
		createSolution();
		createCorrection();
		optimizeCorrection();
		return new Solution(S.times(B));
	}

	/**
	 * Removes all equations whose absolute error is larger than the specified
	 * value. After this method all solutions become invalid. 
	 */	
	public void removeLargeErrorEquations(Solution solution, double maxError)
	{
		Iterator iter = equations.iterator();
		while( iter.hasNext() )
		{
			Equation equation = (Equation)iter.next();
			if( equation.getAbsoluteError(solution) > maxError )
				iter.remove();
		}
	}
}
