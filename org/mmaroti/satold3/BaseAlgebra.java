/**
 *	Copyright (C) Miklos Maroti, 2014
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

package org.mmaroti.satold3;

public class BaseAlgebra {
	protected final SatSolver solver;
	protected final String name;
	protected final Operation[] operations;
	protected final Relation[] relations;

	public BaseAlgebra(SatSolver solver, String name, Signature signature,
			int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		this.solver = solver;
		this.name = name;

		Symbol[] operations = signature.getOperations();
		this.operations = new Operation[operations.length];
		for (int i = 0; i < operations.length; ++i)
			this.operations[i] = new Operation(operations[i], size);

		Symbol[] relations = signature.getRelations();
		this.relations = new Relation[relations.length];
		for (int i = 0; i < relations.length; ++i)
			this.relations[i] = new Relation(relations[i], size);

	}

	/**
	 * Calculates <code>base^exponent</code>. The exponent must be non-negative.
	 * 
	 * @throws IllegalArgumentException
	 *             if the exponent is negative, or the result does not fit in an
	 *             Integer.
	 */
	protected static int power(int base, int exponent) {
		if (exponent < 0)
			throw new IllegalArgumentException(
					"the exponent must be non-negative");

		long result = 1;
		while (--exponent >= 0) {
			result *= base;
			if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
				throw new IllegalArgumentException(
						"the power is too big (does not fit in an integer)");
		}

		return (int) result;
	}

	public class Operation {
		Operation(Symbol symbol, int size) {
		}
	}

	public Operation[] getOperations() {
		return operations;
	}

	public class Relation {
		protected final SatSolver.Literal[] literals;

		Relation(Symbol symbol, int size) {
			literals = new SatSolver.Literal[power(size, symbol.getArity())];

			int[] arg = new int[symbol.getArity()];
			int pos = 0;

			outer: for (;;) {
				String s = name + "." + symbol.name + "(";
				for (int i = 0; i < arg.length; ++i) {
					if (i != 0)
						s += ",";
					s += Integer.toString(arg[i]);
				}
				s += ")";

				literals[pos] = solver.addLiteral(s);

				for (int i = 0; i < arg.length; ++i) {
					if (++arg[i] < size)
						continue outer;

					arg[i] = 0;
				}
			}
		}
	}

	public Relation[] getRelations() {
		return relations;
	}
}
