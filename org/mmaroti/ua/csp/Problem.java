/**
 *	Copyright (C) Miklos Maroti, 2008
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

package org.mmaroti.ua.csp;

import java.io.PrintWriter;

// TODO: you cannot really add constraints to derived problems
// TODO: the variables should be marked dirty instead of constraints (??)
// TODO: when applying a unary constraint, then not the whole problem
// need to be evaluated, but only that part which is affected 

/**
 * This class defines a constraint satisfaction problem 
 * by a set of variables and constraint relations. The
 * solution set of a problem is one-consistent only.
 */
public class Problem
{
	/**
	 * The constraints are kept in a linked list via the
	 * <code>Constraint.nextConstraint</code> pointer, where 
	 * this is the head pointer;
	 */
	Constraint firstConstraint;

	/**
	 * This is the list of variables.
	 */
	Variable[] variables;
	
	/**
	 * For each variable we have one slot where the possible
	 * values are stored in a bit field. The length of this
	 * array is the same as that of <code>variables</code>.
	 */
	int[] values;

	/**
	 * These arrays are used for temporary calculations. The
	 * length must be larger than the maximum arity of all
	 * constraints.
	 */
	int[] input, output;
	
	/**
	 * Creates an empty constraint satisfaction problem.
	 */
	public Problem()
	{
		firstConstraint = null;
		variables = new Variable[0];
		values = new int[0];
		input = new int[2];
		output = new int[2];
	}

	/**
	 * Creates a clone of the parent problem. You can add new
	 * constraints and variables to the new problem, but you
	 * shall not add variables or constraints to the old one.  
	 */
	public Problem(Problem parent)
	{
		firstConstraint = parent.firstConstraint;
		variables = parent.variables;
		values = parent.values.clone();
		input = parent.input;
		output = parent.output;
	}
	
	int getVariable(String name)
	{
		for(int i = 0; i < variables.length; ++i)
			if( name.equals(variables[i].name) )
				return i;
		
		return -1;
	}
	
	int[] getVariables(String[] names)
	{
		int[] vars = new int[names.length];
		for(int i = 0; i < names.length; ++i)
		{
			int a = getVariable(names[i]);
			if( a < 0 )
				throw new IllegalArgumentException("Unknown variable");
			
			vars[i] = a;
		}
		return vars;
	}
	
	/**
	 * Adds a new variable of the given name to the constraint 
	 * satisfaction problem. This variable can get a value between 
	 * <code>0</code> and <code>size-1</code>. The <code>size</code>
	 * cannot be larger than 32. 
	 */
	public void addVariable(String name, int size)
	{
		if( size <= 0 || size > 32 )
			throw new IllegalArgumentException("Illegal variable size");

		if( getVariable(name) >= 0 )
			throw new IllegalArgumentException("Duplicated variable");
		
		variables = append(variables, new Variable(name));
		values = append(values, size < 32 ? (1<<size)-1 : -1);
	}

	/**
	 * Adds a new constraint relation to the problem. The same
	 * relation can be reused in different constraints. 
	 */
	public void addConstraint(String[] names, Relation relation)
	{
		if( names.length != relation.arity )
			throw new IllegalArgumentException("Incorrect arity");

		if( relation.arity > input.length )
		{
			input = new int[relation.arity];
			output = new int[relation.arity];
		}
		
		int[] vars = getVariables(names);

		// process unary constraints immediately
		if( relation.arity == 1 )
		{
			input[0] = values[vars[0]];
			relation.contains(input, output);
			values[vars[0]] = output[0];
		}
		else
		{
			firstConstraint = new Constraint(vars, relation, firstConstraint);
		
			for(int i = 0; i < vars.length; ++i)
				variables[vars[i]].constraints = append(variables[vars[i]].constraints, firstConstraint);
		}
	}

	/**
	 * Adds a unary constraint to the problem. These constraints
	 * are processed immediately, they restrict the possible
	 * values a variable can take. 
	 */
	public void addUnaryConstraint(String name, int[] relation)
	{
		int var = getVariable(name);
		if( var < 0 )
			throw new IllegalArgumentException("Unknown variable");
		
		int v = 0;
		for(int i = 0; i < relation.length; ++i)
		{
			if( relation[i] < 0 || relation[i] >= 32 )
				throw new IllegalArgumentException("Illegal value");
		
			v |= 1 << relation[i];
		}

		values[var] &= v; 
	}
	
	static Variable[] append(Variable[] variables, Variable variable)
	{
		Variable[] vars = new Variable[variables.length + 1];
		System.arraycopy(variables, 0, vars, 0, variables.length);
		vars[variables.length] = variable;
		return vars;
	}

	static Constraint[] append(Constraint[] constraints, Constraint constraint)
	{
		Constraint[] cons = new Constraint[constraints.length + 1];
		System.arraycopy(constraints, 0, cons, 0, constraints.length);
		cons[constraints.length] = constraint;
		return cons;
	}

	static int[] append(int[] values, int value)
	{
		int[] vals = new int[values.length + 1];
		System.arraycopy(values, 0, vals, 0, values.length);
		vals[values.length] = value;
		return vals;
	}

	/**
	 * Finds a one-consistent solution, which is an assignment of 
	 * possible values to each variable such that no constraint 
	 * limits this set further.  
	 */
	public void runOneConsistency()
	{
		if( firstConstraint == null )
			return;

		Constraint head = firstConstraint;

		Constraint tail = head;
		while( tail.next != null )
		{
			tail.nextDirty = tail.next;
			tail = tail.next;
		}
		tail.nextDirty = null;
		
		while( head != null )
		{
			int[] input = new int[head.variables.length];
			for(int i = 0; i < input.length; ++i)
				input[i] = values[head.variables[i]];
			
			int[] output = new int[input.length];
			head.relation.contains(input, output);

			for(int i = 0; i < input.length; ++i)
			{
				if( input[i] != output[i] )
				{
					values[head.variables[i]] = output[i];
					
					Constraint[] cons = variables[head.variables[i]].constraints;
					for( Constraint con : cons )
					{
						if( con.nextDirty == null && con != tail )
						{
							tail.nextDirty = con;
							tail = con;
						}
					}
				}
			}

			Constraint prev = head;
			head = head.nextDirty;
			prev.nextDirty = null;
		}
	}

	/**
	 * This method finds a solution of the problem if it exists 
	 * with a depth-first search. It returns <code>true</code>
	 * if a solution has been found and <code>false</code> if the
	 * problem has no solution.  
	 */
	public boolean findOneSolution()
	{
		runOneConsistency();

		int a = getUnassignedVariableCount();
		if( a < 0 )
			return false;
		else if( a == 0 )
			return true;
		
		// search for the first value that is not uniquely assigned starting from 'a'
		a = (int)(Math.random() * values.length);
		int i = a;
		for(;;)
		{
			int value = values[i];

			if( (value & (value-1)) != 0 )
				break;
			
			if( ++i >= values.length )
				i = 0;
		}

		// restrict this variable to all possible values
		for(int j = 0; j < 32; ++j)
		{
			if( (values[i] & (1<<j)) == 0 )
				continue;
			
			Problem subproblem = new Problem(this);
			subproblem.values[i] = 1<<j;
			
			if( subproblem.findOneSolution() == true )
			{
				values = subproblem.values;
				return true;
			}
		}

		values[i] = 0;
		return false;
	}
	
	/**
	 * Returns the number of variables whose values are not
	 * uniquely determined. If for some variable no possible
	 * values are left, then <code>-1</code> is returned.
	 */
	public int getUnassignedVariableCount()
	{
		int c = 0; 
		
		for(int value : values)
		{
			if( (value & (value-1)) != 0 )
				++c;
			else if( value == 0 )
				return -1;
		}
		
		return c;
	}
	
	/**
	 * Returns <code>true</code> if this solution set is already
	 * the empty set, that is, no possible value can be assigned to
	 * some variable. Even if this method returns <code>false</code>
	 * there might be no solution at all.
	 */
	public boolean hasNoSolution()
	{
		return getUnassignedVariableCount() < 0;
	}

	/**
	 * Returns <code>true</code> if every variable has a unique
	 * value assigned, so this is actually a solution. 
	 */
	public boolean hasUniqueSolution()
	{
		return getUnassignedVariableCount() == 0;
	}

	/**
	 * Returns the list of possible values of the given variable
	 * If the solution set of the problem is not unique, then 
	 * not all values can actually be part of a solution.
	 */
	public int[] getValue(String name)
	{
		int[] result = new int[0];
		
		int a = values[getVariable(name)];
		for(int i = 0; i < 32; ++i)
			if( (a & (1<<i)) != 0 )
				result = append(result, i);
		
		return result;
	}
	
	/**
	 * Prints out the possible variable assignments.
	 */
	public void printValues(PrintWriter writer)
	{
		for(int i = 0; i < variables.length; ++i)
			writer.println(variables[i].printValues(values[i]));
		
		if( hasNoSolution() )
			writer.println("has no solution");
	}

	/**
	 * Prints out the possible variable assignments to the standard output
	 */
	public void printValues()
	{
		PrintWriter writer = new PrintWriter(System.out);
		printValues(writer);
		writer.flush();
	}
}
