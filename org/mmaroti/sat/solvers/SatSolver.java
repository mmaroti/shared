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

package org.mmaroti.sat.solvers;

import java.io.*;
import java.util.*;
import org.mmaroti.sat.core.*;

public abstract class SatSolver extends Solver<Integer> {
	protected int variables;
	protected List<int[]> clauses = new ArrayList<int[]>();

	public SatSolver() {
		super(1);
		clear();
	}

	public void clear() {
		variables = 1;
		clauses.clear();
		clauses.add(new int[] { 1 });
	}

	public final Integer variable() {
		return ++variables;
	}

	public void ensure(Integer literal) {
		clauses.add(new int[] { literal });
	}

	public void ensure(Integer[] clause) {
		int[] c = new int[clause.length];
		for (int i = 0; i < clause.length; i++)
			c[i] = clause[i];

		clauses.add(c);
	}

	@Override
	public Integer not(Integer b) {
		return -b;
	}

	@Override
	public Integer or(Integer elem1, Integer elem2) {
		int a = elem1.intValue();
		int b = elem2.intValue();

		if (a == -1)
			return b;
		else if (a == 1)
			return 1;
		else if (b == -1)
			return a;
		else if (b == 1)
			return 1;
		else if (a == b)
			return a;
		else if (a == -b)
			return 1;

		int var = variable();
		clauses.add(new int[] { -a, var });
		clauses.add(new int[] { -b, var });
		clauses.add(new int[] { a, b, -var });
		return var;
	}

	@Override
	public Integer add(Integer elem1, Integer elem2) {
		int a = elem1.intValue();
		int b = elem2.intValue();

		if (a == 1)
			return -b;
		else if (a == -1)
			return b;
		else if (b == 1)
			return -a;
		else if (b == -1)
			return a;

		int var = variable();
		clauses.add(new int[] { a, b, -var });
		clauses.add(new int[] { a, -b, var });
		clauses.add(new int[] { -a, b, var });
		clauses.add(new int[] { -a, -b, -var });
		return var;
	}

	public void dimacs(PrintStream stream) {
		stream.println("p cnf " + variables + " " + clauses.size());
		for (int[] clause : clauses) {
			for (int i : clause) {
				assert i != 0 && Math.abs(i) <= variables;

				stream.print(i);
				stream.print(' ');
			}
			stream.println('0');
		}
	}
}
