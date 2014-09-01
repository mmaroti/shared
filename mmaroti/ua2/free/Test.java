/*
 * Copyright (C) 2014 Miklos Maroti
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mmaroti.ua2.free;

public class Test {
	public static void main(String[] args) {
		Symbol[] sig = new Symbol[] { new Symbol(Symbol.OPERATION, "p0", 3),
				new Symbol(Symbol.OPERATION, "p1", 3),
				new Symbol(Symbol.OPERATION, "p2", 3) };

		Expression eq0 = new Expression(Symbol.EQUALS, new Expression(sig[0],
				Expression.VARX, Expression.VARY, Expression.VARX),
				Expression.VARX);
		Expression eq1 = new Expression(Symbol.EQUALS, new Expression(sig[1],
				Expression.VARX, Expression.VARY, Expression.VARX),
				Expression.VARX);
		Expression eq2 = new Expression(Symbol.EQUALS, new Expression(sig[2],
				Expression.VARX, Expression.VARY, Expression.VARX),
				Expression.VARX);

		Expression eq3 = new Expression(Symbol.EQUALS, Expression.VARX,
				new Expression(sig[0], Expression.VARX, Expression.VARX,
						Expression.VARY));
		Expression eq4 = new Expression(Symbol.EQUALS, new Expression(sig[0],
				Expression.VARX, Expression.VARY, Expression.VARY),
				new Expression(sig[1], Expression.VARX, Expression.VARY,
						Expression.VARY));
		Expression eq5 = new Expression(Symbol.EQUALS, new Expression(sig[1],
				Expression.VARX, Expression.VARX, Expression.VARY),
				new Expression(sig[2], Expression.VARX, Expression.VARX,
						Expression.VARY));
		Expression eq6 = new Expression(Symbol.EQUALS, new Expression(sig[2],
				Expression.VARX, Expression.VARY, Expression.VARY),
				Expression.VARY);

		Expression eqs = new Expression(Symbol.LAND, new Expression[] { eq0,
				eq1, eq2, eq3, eq4, eq5, eq6 });

		int[] project1 = new int[] { 0, 0, 0, 0, 1, 1, 1, 1 };
		int[] project3 = new int[] { 0, 1, 0, 1, 0, 1, 0, 1 };
		int[] majority = new int[] { 0, 0, 0, 1, 0, 1, 1, 1 };
		int[] twothird = new int[] { 0, 1, 0, 0, 1, 1, 0, 1 };

		Structure A0 = new FixedStructure(sig, 2, new int[][] { majority,
				project3, project3 });
		Structure A1 = new FixedStructure(sig, 2, new int[][] { project1,
				twothird, project3 });
		Structure A2 = new FixedStructure(sig, 2, new int[][] { project1,
				project1, majority });

		Structure As = new Product(new Structure[] { A0, A1, A2 });

		Structure As3 = new Product(As, 3);
		
		As3.printSignature();
		As3.printElements();
		System.out.println(As3.evaluate(eqs, true));
	}
}
