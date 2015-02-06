/**
 *	Copyright (C) Miklos Maroti, 2015
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

package org.mmaroti.setsat;

import java.io.*;
import java.util.*;

public final class Instance {
	private int variables = 0;

	private HashSet<Integer> missing = new HashSet<Integer>();

	public int newvar() {
		int lit = ++variables;
		missing.add(lit);
		return lit;
	}

	public static int exclude(int var, boolean[] solution) {
		assert 1 <= var && var <= solution.length;
		return solution[var - 1] ? -var : var;
	}

	public static int decode(int var, boolean[] solution) {
		assert 1 <= var && var <= solution.length;
		return solution[var - 1] ? TRUE : FALSE;
	}

	private static class IntPair {
		final int first;
		final int second;

		IntPair(int first, int second) {
			assert first < second;

			this.first = first;
			this.second = second;
		}

		public boolean equals(Object object) {
			IntPair pair = (IntPair) object;
			return first == pair.first && second == pair.second;
		}

		public int hashCode() {
			return 11971 * first ^ 32009 * second;
		}
	}

	public static final Instance BOOL = new Instance();

	public static final int TRUE = Integer.MAX_VALUE;
	public static final int FALSE = -Integer.MAX_VALUE;

	public int lift(boolean arg) {
		return arg ? TRUE : FALSE;
	}

	public int not(int arg) {
		assert arg != 0
				&& (Math.abs(arg) <= variables || Math.abs(arg) == TRUE);

		return -arg;
	}

	private HashMap<IntPair, Integer> ormap = new HashMap<IntPair, Integer>();

	public int or(int arg1, int arg2) {
		assert arg1 != 0
				&& (Math.abs(arg1) <= variables || Math.abs(arg1) == TRUE);
		assert arg2 != 0
				&& (Math.abs(arg2) <= variables || Math.abs(arg2) == TRUE);

		int lit;

		if (arg1 == TRUE || arg2 == TRUE || arg1 == -arg2)
			lit = TRUE;
		else if (arg1 == FALSE || arg1 == arg2)
			lit = arg2;
		else if (arg2 == FALSE)
			lit = arg1;
		else {
			assert this != BOOL;

			if (arg2 < arg1) {
				int t = arg1;
				arg1 = arg2;
				arg2 = t;
			}

			missing.remove(Math.abs(arg1));
			missing.remove(Math.abs(arg2));

			IntPair arg = new IntPair(arg1, arg2);
			Integer res = ormap.get(arg);
			if (res != null)
				lit = res;
			else {
				lit = ++variables;
				ormap.put(arg, lit);
			}
		}

		return lit;
	}

	public int and(int arg1, int arg2) {
		return -or(-arg1, -arg2);
	}

	public int leq(int arg1, int arg2) {
		return or(-arg1, arg2);
	}

	private HashMap<IntPair, Integer> eqmap = new HashMap<IntPair, Integer>();

	public int eq(int arg1, int arg2) {
		assert arg1 != 0
				&& (Math.abs(arg1) <= variables || Math.abs(arg1) == TRUE);
		assert arg2 != 0
				&& (Math.abs(arg2) <= variables || Math.abs(arg2) == TRUE);

		boolean flip = false;
		if (arg1 < 0) {
			flip = !flip;
			arg1 = -arg1;
		}
		if (arg2 < 0) {
			flip = !flip;
			arg2 = -arg2;
		}

		int lit;
		if (arg1 == TRUE)
			lit = arg2;
		else if (arg2 == TRUE)
			lit = arg1;
		else if (arg1 == arg2)
			lit = TRUE;
		else {
			assert this != BOOL;

			if (arg2 < arg1) {
				int t = arg1;
				arg1 = arg2;
				arg2 = t;
			}

			missing.remove(arg1);
			missing.remove(arg2);

			IntPair arg = new IntPair(arg1, arg2);
			Integer res = eqmap.get(arg);
			if (res != null)
				lit = res;
			else {
				lit = ++variables;
				eqmap.put(arg, lit);
			}
		}

		return flip ? -lit : lit;
	}

	public int add(int arg1, int arg2) {
		return -eq(arg1, arg2);
	}

	private HashSet<Integer> trueset = new HashSet<Integer>();

	public void ensure(int lit) {
		if (lit != TRUE) {
			if (lit == FALSE) {
				lit = ++variables;
				trueset.add(-lit);
			} else
				missing.remove(Math.abs(lit));

			trueset.add(lit);
		}
	}

	public int getVariables() {
		return variables;
	}

	public void printDimacs(PrintStream stream) {
		int clauses = trueset.size();
		clauses += missing.size();
		clauses += eqmap.size() * 4;
		clauses += ormap.size() * 3;

		if (clauses <= 0)
			throw new IllegalArgumentException();

		stream.println("p cnf " + variables + " " + clauses);

		for (Integer a : trueset) {
			stream.println(a + " 0");
		}

		for (Integer a : missing) {
			stream.println(a + " " + (-a) + " 0");
		}

		for (Map.Entry<IntPair, Integer> entry : eqmap.entrySet()) {
			int a = entry.getKey().first;
			int b = entry.getKey().second;
			int c = entry.getValue();

			stream.println(a + " " + b + " " + c + " 0");
			stream.println((-a) + " " + (-b) + " " + c + " 0");
			stream.println((-a) + " " + b + " " + (-c) + " 0");
			stream.println(a + " " + (-b) + " " + (-c) + " 0");
		}

		for (Map.Entry<IntPair, Integer> entry : ormap.entrySet()) {
			int a = entry.getKey().first;
			int b = entry.getKey().second;
			int c = entry.getValue();

			stream.println(a + " " + b + " " + (-c) + " 0");
			stream.println((-a) + " " + c + " 0");
			stream.println((-b) + " " + c + " 0");
		}
	}
}
