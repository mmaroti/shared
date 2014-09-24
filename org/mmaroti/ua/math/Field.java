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

/**
 * This is the general interface for fields. 
 */
public interface Field extends Ring
{
	/**
	 * Returns the inverse of the specified element.
	 * @param elem a nonzero element of the field
	 * @return the inverse of the element
	 * @throws InvalidArgument if the element is the zero element. 
	 */
	public abstract Object inverse(Object elem);

	/**
	 * Returns the inverse of the specified element.
	 * @param elem a nonzero element of the field
	 * @return the inverse of the element
	 * @throws InvalidArgument if the element is the zero element. 
	 */
	public abstract int inverse(int elem);
	
	/**
	 * Returns the character of the field, which is either a
	 * prime number, or zero if the character is infinite.
	 */
	public abstract int character();

	/**
	 * This is the Set.getElement method, but because of inheritance 
	 * problems, we repeat it here.
	 */
	public abstract Object getElement(int index);

	/**
	 * This is the Set.getIndex method, but because of inheritance 
	 * problems, we repeat it here.
	 */
	public abstract int getIndex(Object element);
	
	/**
	 * This is the Set.toString method, but because of inheritance 
	 * problems, we repeat it here.
	 */
	public abstract String toString(Object element);
}
