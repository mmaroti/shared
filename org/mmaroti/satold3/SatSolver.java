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

import java.io.*;
import java.util.*;

public class SatSolver {
	private static final boolean DEBUG = false;

	protected List<Literal> literals = new ArrayList<Literal>();
	protected List<int[]> clauses = new ArrayList<int[]>();

	public static class Literal {
		public final int id;
		public final String name;

		Literal(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	public Literal addLiteral(String name) {
		if (DEBUG) {
			for (Literal lit : literals) {
				if (lit.name.equals(name))
					throw new IllegalArgumentException(
							"Two literals have the same name");
			}
		}

		Literal lit = new Literal(literals.size() + 1, name);
		literals.add(lit);

		return lit;
	}

	public void addClause(int[] clause) {
		if (DEBUG) {
			int size = literals.size();

			for (int i = 0; i < clause.length; ++i) {
				int id = clause[i];
				assert (-size <= id && id != 0 && id <= size);
			}

			for (int[] c : clauses) {
				if (c == clause)
					throw new IllegalArgumentException("Clause array is reused");
			}
		}

		if (DEBUG) {
			// uniquely order it
			Arrays.sort(clause);

			for (int i = 1; i < clause.length; ++i)
				assert (clause[i - 1] < clause[i]);
		}

		clauses.add(clause);
	}

	public boolean getValue(boolean[] solution, Literal lit) {
		return solution[lit.id - 1];
	}

	public void printSolution(PrintStream stream, boolean[] solution) {
		if (solution == null)
			System.out.println("no solution");
		else {
			assert (solution.length == literals.size());

			for (int i = 0; i < literals.size(); ++i)
				System.out.println(literals.get(i).name
						+ (solution[i] ? " : 1" : " : 0"));

			System.out.println();
		}
	}

	public void printDimacsCnf(PrintStream stream) {
		stream.print("p cnf " + literals.size() + " " + clauses.size() + "\n");

		for (int[] c : clauses) {
			for (int i = 0; i < c.length; ++i)
				stream.print("" + c[i] + " ");

			stream.print("0\n");
		}
	}

	public boolean[] solve() throws IOException {
		throw new UnsupportedOperationException();
	}
}
