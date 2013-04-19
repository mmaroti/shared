/**
 * Copyright (C) Miklos Maroti, 2011
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

package org.mmaroti.compiler;

/**
 * This interface encapsulates the execution of a single function.
 * There (should) be a parser that takes a program written in 
 * some programming language (java), and turns that into a sequence
 * of calls to the Executor. Constants in the program are automatically
 * evaluated without a call to the Executor. For example, a loop
 * variable can be constant, in which case the loop will be unrolled.  
 */
public interface Executor
{
	/**
	 * This method is called, when a constant occurs in the program
	 */
	public Variable constant(String string);

	/**
	 * This method is called, when a constant occurs in the program
	 */
	public Variable constant(int integer);
	
	/**
	 * This method is called, when a new variable is declared
	 */
	public Variable declare(String name);

	/**
	 * This method is called, when a variable is referenced
	 * by name in an expression. Only declared or global variables 
	 * can be looked up. If no match is found, then null is returned.
	 */
	public Variable lookup(String name);

	/**
	 * This method is called when a value is assigned to a 
	 * variable.
	 */
	public void assign(Variable variable, Variable value);

	/**
	 * This method is called, when the dot notation is used
	 * to access a member of a composite unit.
	 */
	public Variable member(Variable variable, String part);
	
	/**
	 * This method is called, when an object is followed by
	 * parenthesis with enclosed arguments. 
	 */
	public Variable call(Variable function, Variable[] arguments);
	
	/**
	 * This method is called, when two variables are added together.
	 * Operations on constants are automatically executed inline. 
	 */
	public Variable sum(Variable left, Variable right);

	/**
	 * This method is called when an if statement is encountered.
	 */
	public void ifStatement(Variable condition, Block then, Block other);
	
	public void whileStatement(Variable condition, Block code);
}
