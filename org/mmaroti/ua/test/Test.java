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

package org.mmaroti.ua.test;

import org.mmaroti.ua.math.*;

public class Test 
{
	public static void main(String[] _)
	{
		Rationals rationals = new Rationals();
		
		Object a = rationals.parse("20/3");
		Object b = rationals.parse("3/-7");
		Object c = rationals.sum(a, b);
		
		System.out.println(rationals.toString(c));
	}
}
