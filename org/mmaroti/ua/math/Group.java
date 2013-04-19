/**
 *	Copyright (C) Miklos Maroti, 2005
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

package org.mmaroti.ua.math;

import org.mmaroti.ua.set.*;

/**
 * This is the general interface for groups. We use multiplicative
 * names regardless whether the group is Abelian or not.
 */
public abstract class Group extends Set
{
	/**
	 * Returns the product of the two arguments.
	 */
	public abstract Object product(Object a, Object b);
	
	/**
	 * Returns the product of the two arguments.
	 */
	public abstract int product(int a, int b);
	
	/**
	 * Returns the inverse of an element.
	 */
	public abstract Object inverse(Object a);

	/**
	 * Returns the inverse of an element.
	 */
	public abstract int inverse(int a);

	/**
	 * Returns the unit element of the field as an object.
	 */
	public abstract Object unitElement();

	/**
	 * Returns the unit element of the field as an object.
	 */
	public abstract int unit();
}
