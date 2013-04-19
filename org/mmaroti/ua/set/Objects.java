/**
 *	Copyright (C) Miklos Maroti, 2004-2006
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

package org.mmaroti.ua.set;

/**
 * This universe represents all java objects. 
 * All methods are implemented by invoking the appropriate
 * methods in the <code>Object</code> class.
 */
public class Objects extends Set
{
	/**
	 * To prevent this object to be created
	 */
	private Objects()
	{
	}

	/**
	 * There is only one object universe, and this object is that
	 */
	public static Set INSTANCE = new Objects(); 
	
	public int getSize()
	{
		throw new UnsupportedOperationException("all objects are not enumerable");
	}
	
	public int getIndex(Object element)
	{
		throw new UnsupportedOperationException("all objects are not enumerable");
	}
	
	public Object getElement(int index)
	{
		throw new UnsupportedOperationException("all objects are not enumerable");
	}

	public boolean areEquals(Object elem1, Object elem2)
	{
		return elem1.equals(elem2);
	}

	public int hashCode(Object element)
	{
		return element.hashCode();
	}

	public String toString(Object element)
	{
		return element.toString();
	}
	
	public Object parse(String string)
	{
		return null;
	}
}
