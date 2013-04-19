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
 * A utility class for enumerating all possible arguments of a
 * finitary operation over a finite set with at least one of
 * the argument being the maximum value. These elements are on
 * the outer surface of the cube. The number of such arguments
 * is <code>size^arity - (size-1)^arity</code>.
 */
public class SphereArgument extends Argument
{
	/**
	 * One less than the size of the underlying set.
	 */
	protected int radius;

	/**
	 * The first coordinate where the argument is
	 * exactly the radius.
	 */
	protected int firstBig;

	/**
	 * Returns the size of the underlying set.
	 */
	public int getSize()
	{
		return radius+1;
	}

	public int getIndex()
	{
		if( vector.length == 0 )
			return 0;
		
		int index = 0;

		int a = CubeArgument.power(radius+1, vector.length - 1);
		for(int i = 0; i < firstBig; ++i)
		{
			index += a;
			a /= radius+1;
			a *= radius;
		}
		
		a = 0;
		for(int i = 0; i < firstBig; ++i)
		{
			a *= radius;
			a += vector[i];
		}
		for(int i = firstBig + 1; i < vector.length; ++i)
		{
			a *= radius+1;
			a += vector[i];
		}
		
		return index + a;
	}

	public void setIndex(int index)
	{
		if( vector.length == 0 )
			return;
		
		firstBig = 0;
		int a = CubeArgument.power(radius+1, vector.length - 1);
		while( index >= a )
		{
			index -= a;
			a /= radius +1;
			a *= radius;
			++firstBig;
		}
		
		if( firstBig >= vector.length )
			throw new IllegalArgumentException("invalid index");
		
		vector[firstBig] = radius;
		
		int i = vector.length;
		while( --i > firstBig )
		{
			vector[i] = index % (radius+1);
			index /= (radius+1);
		}
		
		while( --i >= 0 )
		{
			vector[i] = index % radius;
			index /= radius;
		}
	}

	public int getMaxIndex()
	{
		if( radius < 0 )
			return vector.length == 0 ? 1 : 0;
		
		return CubeArgument.power(radius+1, vector.length) 
			- CubeArgument.power(radius, vector.length);
	}

	public boolean reset()
	{
		if( (vector.length == 0 && radius >= 0) || (vector.length > 0 && radius < 0) ) 
			return false;
		
		int i = vector.length;
		while( --i >= 0 )
			vector[i] = 0;

		firstBig = 0;
		if( vector.length > 0 )
			vector[0] = radius;

		return true;
	}
	
	public boolean next()
	{
		if( vector.length == 0 )
			return false;
		
		int i = vector.length;
		while( --i > firstBig )
		{
			if( ++vector[i] <= radius )
				return true;
				
			vector[i] = 0;
		}
		
		while( --i >= 0 )
		{
			if( ++vector[i] < radius )
				return true;
			
			vector[i] = 0;
		}

		vector[firstBig] = 0;
		if( radius == 0 || ++firstBig >= vector.length )
			return false;
		
		vector[firstBig] = radius;
		return true;
	}
	
	/**
	 * Constructs an argument enumerator for a finitary operation 
	 * over a finite set. Only those vectors are listed that have
	 * at least one entry with the maximum value. For example, if
	 * arity is 2 and size is 3 then this class enumerates the
	 * following 5 vectors
	 * <pre>
	 * 0 : [2,0]
	 * 1 : [2,1]
	 * 2 : [2,2]
	 * 3 : [0,2]
	 * 4 : [1,2]
	 * </pre>
	 *
	 * @param arity The arity of the operation.
	 * @param size The size of the underlying set.
	 */
	public SphereArgument(int arity, int size)
	{
		super(arity);

		if( arity < 0 )
			throw new IllegalArgumentException("the arity must be positive");
		if( size < 0 )
			throw new IllegalArgumentException("size must be positive");

		radius = size-1;

		if( arity > 0 )
			vector[0] = radius;
	}
}
