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

/**
 * Implements the not all equals relation of arbitrary arity
 */
public class NotAllEquals extends Relation
{
	public NotAllEquals(int arity)
	{
		super(arity);
	}

	// TODO: optimize this to remove the last possibility for the last coordinate
	public void contains(int[] input, int[] output)
	{
		int a = 0;
		
		for(int i = 0; i < arity; ++i)
		{
			a |= input[i];
			output[i] = input[i];
		}

		// at most one bit is set
		if( (a & (a-1)) == 0 )
		{
			for(int i = 0; i < arity; ++i)
				output[i] = 0;
		}
	}
}
