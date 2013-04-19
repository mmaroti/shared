/**
 *	Copyright (C) Miklos Maroti, 2002-2006
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

package org.mmaroti.ua.alg;

import java.util.Iterator;
import java.util.List;

import org.mmaroti.ua.set.*;

/**
 * The principal interface to manipulate finite or infinite algebraic
 * structures. 
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public abstract class Algebra
{
	/**
	 * Returns the underlying set of the algebra
	 */
	public abstract Set getUniverse();
	
	/**
	 * Returns the list of operations of the algebra.
	 */
	public abstract Operation[] getOperations();
	
	/**
	 * Returns the size of the universe of the algebra.
	 * @see Set.getSize
	 */
	public final int getSize()
	{
		return getUniverse().getSize();
	}

	/**
	 * Returns <code>true</code> if this algebra is compatible with
	 * the other algebra, i.e. have the same number of operations 
	 * with the same arities.
	 */
	public boolean isCompatible(Algebra other)
	{
		Operation[] as = getOperations();
		Operation[] bs = other.getOperations();
		
		if( as.length != bs.length )
			return false;
		
		for(int i = 0; i < as.length; ++i)
			if( ! as[i].isCompatible(bs[i]) )
				return false;
		
		return true;
	}

	/**
	 * Returns <code>true</code> if the algebras in the list are 
	 * pairwise compatible, <code>false</code> otherwise.
	 */
	public static boolean areCompatible(List<Algebra> algebras)
	{
		if( algebras.size() <= 1 )
			return true;

		Iterator<Algebra> iter = algebras.iterator();
		Algebra a = iter.next();
		
		while( iter.hasNext() )
			if( ! a.isCompatible(iter.next()) )
				return false;
	
		return true;
	}
}
