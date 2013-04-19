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

class Variable
{
	Variable(String name)
	{
		this.name = name;
		constraints = new Constraint[0];
	}

	public String printValues(int value)
	{
		String s = name + '=';

		boolean first = true;
		for(int j = 0; j < 32; ++j)
		{
			if( (value & (1<<j)) != 0 )
			{
				if( first )
					first = false;
				else
					s += ','; 

				char c = (char)(j < 10 ? '0' + j : 'a' + j - 10);
				s += c;
			}
		}

		return s;
	}
	
	/**
	 * The name of the variable.
	 */
	String name;

	/**
	 * The constraints of the at least binary arity in which 
	 * this variable occurs. These are the constraints that need 
	 * to be propagated if the value of this variable is changed. 
	 */
	Constraint[] constraints;
}
