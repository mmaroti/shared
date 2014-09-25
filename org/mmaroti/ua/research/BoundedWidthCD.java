/**
 *	Copyright (C) Miklos Maroti, 2007-2008
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

public class BoundedWidthCD
{
	public static RewriteSystem CD2;
	
	public static void construct()
	{
		Signature signature = new Signature(new Symbol[]
		{
				new Symbol("p", 3, 0, 0),
				new Symbol("q", 3, 0, 0),
		});
		
		TermAlgebra terms = new TermAlgebra(signature, 2);
	
		RewriteSystem.Rule[] rules = new RewriteSystem.Rule[]
		{
			RewriteSystem.parseRule(terms, "p(x0,x1,x0) -> x0"),
			RewriteSystem.parseRule(terms, "q(x0,x1,x0) -> x0"),
	
			RewriteSystem.parseRule(terms, "p(x0,x0,x1) -> x0"),
			RewriteSystem.parseRule(terms, "p(x0,x1,x1) -> q(x0,x1,x1)"),
			RewriteSystem.parseRule(terms, "p(x0,x0,x1) -> x1"),
		};
		
		CD2 = new RewriteSystem(terms, rules);
	}
		
	public static void main(String[] args)
	{

		construct();

		Operation p = CD2.getOperations()[0];
//		Operation q = CD2.getOperations()[1];
		Object x = CD2.getVariable(0);
		Object y = CD2.getVariable(1);
		
		Object t = p.getValue(x,y,y);
		System.out.println(t);
	}
}
