/**
 *	Copyright (C) Miklos Maroti, 2003-2006
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
 * Instances of this class are used to construct finite sets of integers,
 * variables and other objects. The objects are {@link Integer} objects
 * numbered from <code>0</code> to <code>size-1</code>. The string 
 * representation of the objects are constructed from a pattern string
 * that must contain exactly one % character, which is replaced with
 * the index value.
 * <pre>
 * "%", NUMERICAL	produces: 0, 1, 2, 3, ...
 * "x%", NUMERICAL	produces: x0, x1, x2, x3, ...
 * "%", ALPHABET	produces: a, b, c, d, ..., z
 * "%", CAPITALS	produces: A, B, C, D, ..., Z
 * </pre>
 * 
 * @author mmaroti@math.vanderbilt.edu
 */
public class FiniteSet extends Universe
{
	/**
	 * Constructs a finite set of size <code>size</code>. 
	 * 
	 * @param size the size of the set, must be non-negative
	 */
	public FiniteSet(int size)
	{
		if( size < 0 )
			throw new IllegalArgumentException("the size of the set must be nonnegative");
	
		this.size = size;
		this.pattern = "%";
		this.style = NUMERICAL;
	}
	
	/**
	 * Creates a finite set of size <code>size</code>. 
	 * 
	 * @param size the size of the set, must be non-negative
	 * @param pattern a string containing the style of the element.
	 */
	public FiniteSet(int size, String pattern, int style)
	{
		if( size < 0 )
			throw new IllegalArgumentException("The size of the set must be nonnegative");

		if( pattern.indexOf('%') < 0 || pattern.indexOf('%') != pattern.lastIndexOf('%') )
			throw new IllegalArgumentException("The pattern must contain the % character exactly once");
	
		if( ! pattern.trim().equals(pattern) )
			throw new IllegalArgumentException("The pattern should have no leading or trailing white spaces");
		
		if( style != NUMERICAL && size > 26 )
			throw new IllegalArgumentException("The size cannot be larger than 26 for sets labeled by letters");
	
		this.size = size;
		this.pattern = pattern;
		this.style = style;
	}
	
	protected int size;
	protected String pattern;
	protected int style;
	
	public static final int NUMERICAL = 1;
	public static final int ALPHABET = 2;
	public static final int CAPITALS = 3;

	public int getSize()
	{
		return size;
	}

	public Object getElement(int index)
	{
		if( index < 0 || index >= size )
			throw new IllegalArgumentException("invalid index");

		return new Integer(index);
	}

	public int getIndex(Object elem) 
	{
		return ((Integer)elem).intValue();
	}

	public boolean areEquals(Object elem1, Object elem2)
	{
		return elem1.equals(elem2);
	}

	public int hashCode(Object elem)
	{
		return elem.hashCode();
	}

	public String toString(Object elem)
	{
		int index = getIndex(elem);

		switch( style )
		{
		case NUMERICAL:
			return pattern.replace("%",Integer.toString(index));
		
		case ALPHABET:
			return pattern.replace("%",Character.toString((char)('a' + index)));
		
		case CAPITALS:
			return pattern.replace("%",Character.toString((char)('A' + index)));
		
		default:
			throw new IllegalArgumentException("Illegal style");
		}
	}

	public Object parse(String string)
	{
		try
		{
			string = string.trim();

			int pos = pattern.indexOf('%');
			int index = Integer.parseInt(string.substring(pos, string.length()-(pattern.length()-pos-1)));
			
			if( 0 <= index && index < size )
			{
				Object elem = getElement(index);
			
				if( toString(elem).equals(string) )
					return elem;
			}
		}
		catch(NumberFormatException e) { }
		
		return null;
	}
}
