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

/**
 * Classes implementing this interface capture fixed relations
 * over at most 32-element sets of arbitrary arity.
 */
public abstract class Relation
{
	/**
	 * The arity of the relation
	 */
	int arity;

	public Relation(int arity)
	{
		if( arity < 1 )
			throw new IllegalArgumentException("The arity has to be at least one.");
		
		this.arity = arity;
	}
	
	/**
	 * First selects only those tuples in the relation
	 * whose coordinates are allowed by the input mask,
	 * then we collect the possible values for the
	 * coordinates in the output array. The length of
	 * the arrays might be larger than the arity.
	 * 
	 * @param input an array of integers, each containing 
	 * a bit field for the possible values for that given 
	 * coordinate.
	 * @param output an array of integers, each containing 
	 * a bit field for the possible values for that given 
	 * coordinate.
	 */
	public abstract void contains(int[] input, int[] output);
}
