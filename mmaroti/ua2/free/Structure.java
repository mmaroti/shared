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

import java.util.*;

public abstract class Structure {
	public static abstract class Operation {
		protected Operation(Symbol symbol, int size) {
			assert (size >= 0);

			this.symbol = symbol;
			this.size = size;
		}

		public final Symbol symbol;
		public final int size;

		public abstract int getValue(int[] elements);

		public static final Operation[] EMPTY = new Operation[0];
	};

	public Structure(Operation[] operations, int size) {
		assert (size >= 0);

		for (Operation op : operations)
			assert (op.size == size);

		this.operations = operations;
		this.size = size;
	}

	public final int size;
	public final Operation[] operations;

	public abstract String getName(int element);

	public Symbol[] getSignature() {
		Symbol[] signature = new Symbol[operations.length];

		for (int i = 0; i < signature.length; ++i)
			signature[i] = operations[i].symbol;

		return signature;
	}

	public Operation getOperation(Symbol symbol) {
		for (Operation op : operations)
			if (op.symbol == symbol)
				return op;

		throw new IllegalArgumentException("Unknown operation " + symbol.name);
	}

	public void printElements() {
		System.out.println("Elements:");
		for (int i = 0; i < size; ++i)
			System.out.println("" + i + ":\t" + getName(i));
	}

	public void printSignature() {
		System.out.print("Signature: ");
		Symbol[] signature = getSignature();
		for(int i = 0; i < signature.length; ++i) {
			if (i != 0)
				System.out.print(", ");
			System.out.print(signature[i].name + "(" + signature[i].arity + ")");
		}
		System.out.println();
	}
	
	public int evaluate(final Expression expr,
			final HashMap<Symbol, Integer> assignment) {

		final Symbol symbol = expr.symbol;
		final Expression[] subs = expr.subs;

		if (symbol.type == Symbol.OPERATION) {
			Operation op = getOperation(symbol);

			int[] elements = new int[subs.length];
			for (int i = 0; i < subs.length; ++i)
				elements[i] = evaluate(subs[i], assignment);

			return op.getValue(elements);
		} else if (symbol == Symbol.EQUALS) {
			assert (subs.length == 2);

			int elem0 = evaluate(subs[0], assignment);
			int elem1 = evaluate(subs[1], assignment);

			return elem0 == elem1 ? 1 : 0;
		} else if (symbol.type == Symbol.CONNECTIVE) {
			if (symbol == Symbol.LAND) {
				for (int i = 0; i < subs.length; ++i)
					if (evaluate(subs[i], assignment) == 0)
						return 0;

				return 1;
			} else if (symbol == Symbol.LOR) {
				for (int i = 0; i < subs.length; ++i)
					if (evaluate(subs[i], assignment) == 1)
						return 1;

				return 0;
			} else if (symbol == Symbol.LIMP) {
				assert (subs.length == 2);

				if (evaluate(subs[0], assignment) == 0)
					return 1;

				return evaluate(subs[1], assignment);
			} else if (symbol == Symbol.LNOT) {
				assert (subs.length == 1);

				return evaluate(subs[0], assignment) > 0 ? 0 : 1;
			} else if (symbol == Symbol.LIFF) {
				assert (subs.length == 2);

				int elem0 = evaluate(subs[0], assignment);
				int elem1 = evaluate(subs[1], assignment);

				return elem0 == elem1 ? 1 : 0;
			} else
				throw new IllegalArgumentException("Unknown connective: "
						+ symbol.name);
		} else {
			assert (symbol.type == Symbol.VARIABLE);

			int result = assignment.get(symbol);
			assert (0 <= result && result < size);

			return result;
		}
	}

	public boolean evaluate(final Expression expr) {
		return evaluate(expr, false);
	}

	public boolean evaluate(final Expression expr, boolean print) {
		if (!expr.isFormula())
			throw new IllegalArgumentException("Expression is not a formula");

		if (size <= 0)
			return true;

		Symbol[] variables = expr.getVariables();

		HashMap<Symbol, Integer> assignment = new HashMap<Symbol, Integer>();
		for (int i = 0; i < variables.length; ++i)
			assignment.put(variables[i], 0);

		outer: for (;;) {
			if (evaluate(expr, assignment) == 0) {
				if (print) {
					System.out.print("Falsifying assignment:");
					for (int i = 0; i < variables.length; ++i)
						System.out.print(" " + variables[i].name + "="
								+ getName(assignment.get(variables[i])));
					System.out.println();
				}
				return false;
			}

			for (int i = 0; i < variables.length; ++i) {
				int a = assignment.get(variables[i]) + 1;
				if (a < size) {
					assignment.put(variables[i], a);
					continue outer;
				} else
					assignment.put(variables[i], 0);
			}

			return true;
		}
	}
}
