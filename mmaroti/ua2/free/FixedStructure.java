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

public class FixedStructure extends Structure {
	private final static boolean debugging = false;
	
	public static class Operation extends Structure.Operation {
		private Operation(Symbol symbol, int size) {
			super(symbol, size);

			int len = 1;
			for (int i = 0; i < symbol.arity; ++i)
				len *= size;

			table = new int[len];
		}

		private Operation(Symbol symbol, int size, int[] table) {
			super(symbol, size);

			int len = 1;
			for (int i = 0; i < symbol.arity; ++i)
				len *= size;

			assert (table.length == len);
			for (int i = 0; i < len; ++i)
				assert (0 <= table[i] && table[i] < size);

			this.table = table;
		}

		public final int[] table;

		public int getPosition(int[] elements) {
			assert (elements.length == symbol.arity);

			int pos = 0;
			for (int i = 0; i < elements.length; ++i) {
				assert (0 <= elements[i] && elements[i] < size);
				pos = pos * size + elements[i];
			}

			return pos;
		}

		public int getValue(int elements[]) {
			int a = table[getPosition(elements)];
			
			if (debugging) {
				System.out.print(symbol.name + "(");
				for (int i = 0; i < elements.length; ++i)
					System.out.print(elements[i]);
				System.out.println(")=" + a);
			}
			
			return a;
		}

		public void setValue(int elements[], int value) {
			assert (0 <= value && value < size);
			table[getPosition(elements)] = value;
		}
	}

	public FixedStructure(Symbol[] signature, int size) {
		super(createOperations(signature, size), size);
		this.names = createNames(size);
	}

	private static Operation[] createOperations(Symbol[] signature, int size) {

		Operation[] operations = new Operation[signature.length];
		for (int i = 0; i < operations.length; ++i)
			operations[i] = new Operation(signature[i], size);

		return operations;
	}

	public FixedStructure(Symbol[] signature, int size, int[][] tables) {
		super(createOperations(signature, size, tables), size);
		this.names = createNames(size);
	}

	private static Operation[] createOperations(Symbol[] signature, int size,
			int[][] tables) {
		assert (signature.length == tables.length);

		Operation[] operations = new Operation[signature.length];
		for (int i = 0; i < operations.length; ++i)
			operations[i] = new Operation(signature[i], size, tables[i]);

		return operations;
	}

	public FixedStructure(int size, Operation[] operations) {
		super(operations, size);
		this.names = createNames(size);
	}

	public final String[] names;

	private static String[] createNames(int size) {
		String[] names = new String[size];

		for (int i = 0; i < size; ++i)
			names[i] = Integer.toString(i);

		return names;
	}

	public String getName(int element) {
		assert (0 <= element && element < size);

		return names[element];
	}

	public void setName(int element, String name) {
		assert (0 <= element && element < size);

		names[element] = name;
	}
}
