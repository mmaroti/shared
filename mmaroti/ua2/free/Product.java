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

public class Product extends Structure {
	private final static boolean debugging = false;

	public static class Operation extends Structure.Operation {
		private Operation(Structure.Operation[] components) {
			super(createSymbol(components), createSize(components));

			for (int k = 0; k < components.length; ++k)
				assert (components[k].symbol == symbol);

			this.components = components;

			temp1 = new int[symbol.arity];
			temp2 = new int[symbol.arity];
			temp3 = new int[components.length];
		}

		private Operation(Symbol symbol) {
			super(symbol, 1);

			this.components = Structure.Operation.EMPTY;

			temp1 = new int[symbol.arity];
			temp2 = new int[symbol.arity];
			temp3 = new int[components.length];
		}

		private static Symbol createSymbol(Structure.Operation[] components) {
			if (components.length <= 0)
				throw new IllegalArgumentException(
						"At least one component is required");

			Symbol symbol = components[0].symbol;
			for (int i = 1; i < components.length; ++i)
				if (components[i].symbol != symbol) {
					throw new IllegalArgumentException(
							"The components are not compatible: " + symbol.name
									+ " vs. " + components[i].symbol.name);
				}

			return symbol;
		}

		private static int createSize(Structure.Operation[] components) {
			int size = 1;

			for (Structure.Operation op : components)
				size *= op.size;

			return size;
		}

		public final Structure.Operation[] components;
		private final int[] temp1;
		private final int[] temp2;
		private final int[] temp3;

		public synchronized int getValue(int elements[]) {
			assert (elements.length == symbol.arity);

			for (int i = 0; i < symbol.arity; ++i) {
				assert (0 <= elements[i] && elements[i] < size);
				temp1[i] = elements[i];
			}

			for (int k = components.length - 1; k >= 0; --k) {
				int s = components[k].size;
				assert (components[k].symbol == symbol);

				for (int i = 0; i < symbol.arity; ++i) {
					temp2[i] = temp1[i] % s;
					temp1[i] = temp1[i] / s;
				}

				temp3[k] = components[k].getValue(temp2);
				assert (0 <= temp3[k] && temp3[k] < components[k].size);
			}

			int a = 0;
			for (int k = 0; k < components.length; ++k) {
				a = a * components[k].size + temp3[k];
			}

			assert (0 <= a && a < size);

			if (debugging) {
				System.out.print(symbol.name + "(");
				for (int i = 0; i < elements.length; ++i)
					System.out.print(elements[i]);
				System.out.println(")=" + a);
			}

			return a;
		}
	}

	public final Structure[] components;

	public Product(Structure[] components) {
		super(createOperations(components), createSize(components));
		this.components = components;
	}

	private static Operation[] createOperations(Structure[] components) {
		if (components.length <= 0)
			throw new IllegalArgumentException(
					"At least one component is required");

		for (int k = 1; k < components.length; ++k)
			if (components[k].operations.length != components[0].operations.length)
				throw new IllegalArgumentException(
						"The components do not have the same number of operations");

		Operation[] operations = new Operation[components[0].operations.length];
		for (int i = 0; i < operations.length; ++i) {
			Structure.Operation[] compops = new Structure.Operation[components.length];
			for (int k = 0; k < components.length; ++k)
				compops[k] = components[k].operations[i];

			operations[i] = new Operation(compops);
		}

		return operations;
	}

	private static int createSize(Structure[] components) {
		int size = 1;

		for (Structure component : components)
			size *= component.size;

		return size;
	}

	public Product(Structure component, int power) {
		super(createOperations(component, power), createSize(component, power));

		assert (power >= 0);
		components = new Structure[power];
		for (int i = 0; i < power; ++i)
			components[i] = component;
	}

	private static Operation[] createOperations(Structure component, int power) {
		if (power < 0)
			throw new IllegalArgumentException("Power cannot be negative");

		Operation[] operations = new Operation[component.operations.length];
		for (int i = 0; i < operations.length; ++i) {
			Structure.Operation[] compops = new Structure.Operation[power];
			for (int k = 0; k < power; ++k)
				compops[k] = component.operations[i];

			operations[i] = new Operation(compops);
		}

		return operations;
	}

	private static int createSize(Structure component, int power) {
		int size = 1;

		for (int i = 0; i < power; ++i)
			size *= component.size;

		return size;
	}

	public String getName(int element) {
		assert (0 <= element && element < size);

		String s = "";
		for (int k = components.length - 1; k >= 0; --k) {
			int a = element % components[k].size;
			element = element / components[k].size;

			s = components[k].getName(a) + s;
			if (k != 0)
				s = "," + s;
		}

		return "(" + s + ")";
	}
}
