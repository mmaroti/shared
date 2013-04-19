/**
 *	Copyright (C) Miklos Maroti, 2002
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

package org.mmaroti.ua.util;

/**
 * An abstract helper class for enumerating arguments.
 * You can iterate through the set of "possible" argument vectors with
 * <pre>
 *	Argument arg = new DerivedArgument(...);
 *	if( arg.reset() ) do
 *	{
 *		...
 *	} while( arg.next() );
 * </pre>
 */
public abstract class Argument
{
	/**
	 * Creates an Argument of size <code>arity</code>. 
	 *
	 * @param arity The length of the {@link #vector} array.
	 */
	protected Argument(int arity)
	{
		vector = new int[arity];
	}

	/**
	 * The argument vector.
	 * This vector can be passed as an argument in methods 
	 * like {@link Operation#getValue(int[])}.
	 */
	public int[] vector;

	/**
	 * Calculates the index of the current argument vector.
	 *
	 * @return a number in the range <code>[0,size]</code>.
	 * The value <code>size</code> means that the argument vector is invalid.
	 */
	public abstract int getIndex();

	/**
	 * Updates the argument vector from an index. 
	 * This does the opposite of {@link #getIndex}.
	 */
	public abstract void setIndex(int index);

	/**
	 * Returns the number of possible arguments this object enumerates.
	 */
	public abstract int getMaxIndex();

	/**
	 * Sets the argument vector to the first possible way.
	 * @return <code>getMaxSize()>0</code>.
	 */
	public abstract boolean reset();

	/**
	 * Returns the vector of integers as a string.
	 */
	public String toString()
	{
		String s = "[";
		
		if( vector.length > 0 )
		{
			s += Integer.toString(vector[0]);
		
			for(int i = 1; i < vector.length; ++i)
				s += "," + Integer.toString(vector[i]);
		}
		
		return s + "]";
	}

	/**
	 * Calculates the next argument vector in the enumeration.
	 * @return <code>true</code> if the returned vector is valid,
	 * <code>false</code> if the previous vector was the last,
	 * in which case the value of the vector is undefined.
	 */
	public abstract boolean next();
}
