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

class Constraint
{
	Constraint(int[] variables, Relation relation, Constraint next)
	{
		if( variables.length != relation.arity )
			throw new IllegalArgumentException("Incorrect arity");
		
		this.variables = variables;
		this.relation = relation;
		this.next = next;
	}
	
	/**
	 * The indices of variables of this constraint in the 
	 * problem.variables array.
	 */
	int[] variables;
	
	/**
	 * The underlying relation of this constraint.
	 */
	Relation relation;

	/**
	 * The constraints of a problem are kept in a linked list
	 * (actually it can be a tree) with this pointer. 
	 */
	Constraint next;
	
	/**
	 * The constraints that need to be reevaluated are kept
	 * in a linked list with this pointer. 
	 */
	Constraint nextDirty;
}
