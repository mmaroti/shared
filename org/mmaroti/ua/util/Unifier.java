/**
 *	Copyright (C) Miklos Maroti, 2001-2003
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
 * 59 Temple Place, Suite 330, Boston,MA 02111-1307 USA
 */

package org.mmaroti.ua.util;

/**
 * The implementations of Comparator must define an equivalence 
 * relation on all objects. This equivalence could be different
 * from the object identity (== operator) and the standard
 * object equality (Object.equals). 
 */
public interface Unifier
{
	/**
	 * Implementors must return a hash code for the provided object.
	 * The hash code of equivalent objects must be the same.  
	 */
	public int hashCode(Object object);

	/**
	 * Returns true if the two objects are equivalent.
	 *  The implementation could be different from simply 
	 *  using the == operator, or using the Object.equality() 
	 *  method. 
	 * @param a The first object
	 * @param b The second object
	 * @return true if the two objects are equivalent.
	 */
	public boolean equals(Object a, Object b);
	
	/**
	 * Must return an equal copy of the original one.
	 * @param a the original object
	 * @return a newly created object that is equal to the original
	 */
	public Object clone(Object a);
}
