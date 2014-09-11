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

package org.mmaroti.ua.minisat;

import java.io.*;
import java.util.*;

public class Instance {
	protected static final boolean debugging = false;

	// eliminate redundant clauses
	protected boolean eliminate = false;

	public void setEliminate(boolean eliminate) {
		this.eliminate = eliminate;
	}

	protected List<String> variables = new ArrayList<String>();
	protected Map<String, Integer> lookup = new HashMap<String, Integer>();

	public int getVariable(String var) {
		assert (var != null);

		Integer pos = lookup.get(var);
		if (pos == null) {

			pos = new Integer(variables.size() + 1);

			variables.add(var);
			lookup.put(var, pos);
		}

		assert (pos >= 1);
		return pos;
	}

	protected List<int[]> clauses = new ArrayList<int[]>();

	protected boolean implies(int[] first, int[] second) {
		assert (first != null && second != null);

		int j = 0;
		for (int i = 0; i < first.length; ++i) {
			while (j < second.length && second[j] < first[i])
				j += 1;

			if (j >= second.length || second[j] > first[i])
				return false;

			j += 1;
		}

		return true;
	}

	public void addClause(int[] clause) {
		if (debugging) {
			int s = variables.size();

			for (int i = 0; i < clause.length; ++i) {
				int c = clause[i];
				assert (-s <= c && c != 0 && c <= s);
			}

			for (int[] c : clauses) {
				if (c == clause)
					throw new IllegalArgumentException(
							"You are reusing the same clause object multiple times");
			}
		}

		// uniquely order it
		Arrays.sort(clause);

		if (debugging) {
			for (int i = 1; i < clause.length; ++i)
				assert (clause[i - 1] < clause[i]);
		}

		if (eliminate) {
			Iterator<int[]> iter = clauses.iterator();
			while (iter.hasNext()) {
				int[] c = iter.next();

				if (c.length <= clause.length && implies(c, clause))
					return; // already implied

				if (c.length > clause.length && implies(clause, c))
					iter.remove(); // removed implied clause
			}
		}

		clauses.add(clause);
	}

	public void addClause(boolean[] solution) {
		assert (solution.length == variables.size());

		int[] clause = new int[variables.size()];
		for (int i = 0; i < solution.length; ++i)
			clause[i] = solution[i] ? -(i + 1) : i + 1;

		clauses.add(clause);
	}

	public void addClause(String clause) {
		assert (clause != null && clause.length() > 0);

		// split by spaces and |
		String[] strings = clause.split("[ \\t\\n\\x0B\\f\\r|]+");
		int[] ints = new int[strings.length];

		for (int i = 0; i < strings.length; ++i) {
			String c = strings[i];
			assert (c != null && c.length() != 0);
			assert (!c.equals("-") && !c.startsWith("--"));

			int v;
			if (c.startsWith("-"))
				v = -getVariable(c.substring(1));
			else
				v = getVariable(c);

			ints[i] = v;
		}

		addClause(ints);
	}

	public void printSolution(PrintStream stream, boolean[] solution) {
		if (solution == null)
			System.out.println("no solution");
		else {
			assert (solution.length == variables.size());

			for (int i = 0; i < variables.size(); ++i)
				System.out.print((i != 0 ? ", " : "") + variables.get(i)
						+ (solution[i] ? "=1" : "=0"));

			System.out.println();
		}
	}

	public void printDimacsCnf(PrintStream stream) {
		stream.print("p cnf " + variables.size() + " " + clauses.size() + "\n");

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
