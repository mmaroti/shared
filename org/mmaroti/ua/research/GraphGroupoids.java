/**
 *	Copyright (C) Miklos Maroti, 2003
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

package org.mmaroti.ua.research;

import org.mmaroti.ua.alg.*;
import java.util.*;

/**
 * A helper class to manipulate conservative groupoids. 
 * A graph groupoid is a groupoid obtained from a directed graph by the following
 * definition: xy=x if x->y, y otherwise. These groupoids are exactly the  
 * conservative (xy is either x or y) groupoids. This helper class is used to
 * generate and manipulate graph groupoids. 
 */
public class GraphGroupoids
{
	/**
	 * This function returns all <code>n</code>-element conservative
	 * groupoids.
	 */
	public static List<Algebra> getAllGraphs(int n)
	{
		if( n <= 0 )
			throw new IllegalArgumentException("The size of the algebra must be positive");
		
		ArrayList<Algebra> list = new ArrayList<Algebra>();

		AlgebraBuffer algebra = new AlgebraBuffer(Signature.GROUPOID, n);
		AlgebraBuffer.Op prod = algebra.getOperationTable(0);

		for(int i = 0; i < n; ++i)
			prod.setValue(i,i,i);

		outer: for(;;)
		{
			list.add(new AlgebraBuffer(algebra));

			for(int i = 0; i < n; ++i)
				for(int j = 0; j < n; ++j)
				{
					if( i == j )
						continue;
	
					int a = prod.getValue(i,j) + 1;
					if( a != n )
					{
						prod.setValue(i,j,a);
						continue outer;
					}
					else
						prod.setValue(i,j,0);
				}
			
			break;
		}
		
		return list;
	}
	
	public static void main(String[] args)
	{
		List<Algebra> graphs = getAllGraphs(2);
		ProductAlgebra product = new ProductAlgebra(graphs);
		
		for(int i = 0; i < 2; ++i)
		{
			Object[] vector = new Object[4];
			for(int j = 0; j < vector.length; ++j)
				vector[j] = new Integer(i);
				
			System.out.println(product.getUniverse().toString(vector));
		}
	}
}
