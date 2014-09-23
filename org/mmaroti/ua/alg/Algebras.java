/**
 *	Copyright (C) Miklos Maroti, 2002
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

package org.mmaroti.ua.alg;

import org.mmaroti.ua.util.*;

/**
 * This class contains various static helper methods and classes for algebras
 * and operations.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class Algebras {
	/**
	 * To prevent anyone creating an instance of this class.
	 */
	private Algebras() {
	}

	protected interface EntryFormatter {
		public String getLabel(int[] args);
	}

	/**
	 * Prints a multidimensional table.
	 * 
	 * @param size
	 *            The size of each dimension.
	 * @param arity
	 *            The dimension of the table.
	 * @param padding
	 *            If the returned entry is shorter than padding, it will be
	 *            padded by spaces on the left.
	 * @param writer
	 *            The writer used to print the table.
	 * @param formatter
	 *            The formatter used to get the elements of the table,
	 */
	protected static void printTable(int size, int arity, int padding,
			XmlWriter writer, EntryFormatter formatter) {
		int[] args = new int[arity];

		if (arity == 0) {
			writer.printLine(formatter.getLabel(args));
			return;
		}

		StringBuffer line = new StringBuffer();

		outer: for (;;) {
			if (arity > 2 && args[arity - 1] == 0 && args[arity - 2] == 0) {
				line.append("indices (");
				for (int i = 0; i < arity - 2; ++i) {
					line.append(args[i]);
					line.append(",");
				}
				line.append("*,*)");

				writer.printComment(line.toString());
				line.setLength(0);
			}

			String label = formatter.getLabel(args);
			for (int i = label.length(); i < padding; ++i)
				line.append(" ");
			line.append(label);

			if (++args[arity - 1] < size) {
				line.append(" ");
				continue outer;
			}

			writer.printLine(line.toString());
			line.setLength(0);
			args[arity - 1] = 0;

			int i = arity - 1;
			while (--i >= 0) {
				if (++args[i] < size)
					continue outer;

				args[i] = 0;
			}

			break;
		}
	}

	/**
	 * Prints an algebra to the specified writer. This method prints the
	 * operation and relation tables using the indices. It completely disregards
	 * the labeling information.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the algebra cannot be enumerated
	 */
	public static void printTo(Algebra algebra, XmlWriter writer) {
		int size = algebra.getSize();

		writer.startElem("algebra");
		writer.attr("size", size);

		printUniverseTo(algebra, writer);

		Operation[] ops = algebra.getOperations();
		for (int i = 0; i < ops.length; ++i) {
			final Operation op = ops[i];
			XmlWriter.out.startElem("operation");
			XmlWriter.out.attr("name", op.getSymbol().getName());
			XmlWriter.out.attr("arity", op.getSymbol().getArity());

			printTable(size, op.getSymbol().getArity(), size <= 99 ? 2
					: size <= 999 ? 3 : 1, writer, new EntryFormatter() {
				public String getLabel(int[] args) {
					return String.valueOf(op.getValue(args));
				}
			});

			XmlWriter.out.endElem();
		}

		writer.endElem();
	}

	public static void printUniverseTo(Algebra algebra, XmlWriter writer) {
		int size = algebra.getSize();

		writer.startElem("universe");
		writer.attr("size", size);

		for (int i = 0; i < size; ++i)
			writer.printLine("" + i + " : "
					+ algebra.toString(algebra.getElement(i)));

		writer.endElem();
	}
}
