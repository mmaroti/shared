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

import java.text.*;
import java.util.*;
import org.sat4j.core.*;
import org.sat4j.minisat.*;
import org.sat4j.specs.*;

public class Sat4J extends SatSolver<Integer> {
	protected int variables;
	protected ISolver solver = SolverFactory.newDefault();
	protected boolean[] solution;

	public Sat4J() {
		super(1);
		clear();
	}

	public void clear() {
		variables = 1;
		solver.reset();
		solution = new boolean[0];

		solver.newVar(1);
		clause(new int[] { 1 });
	}

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	@Override
	public boolean solve() {
		if (solution == null)
			return false;

		try {
			if (debugging)
				System.err.print("Running Sat4J with " + variables
						+ " variables ... ");

			long time = System.currentTimeMillis();
			boolean satisfiable = solver.isSatisfiable();
			time = System.currentTimeMillis() - time;

			if (debugging)
				System.err.println("finished in "
						+ TIME_FORMAT.format(0.001 * time) + " seconds: "
						+ (satisfiable ? "satisfiable." : "unsatisfiable."));

			if (!satisfiable)
				return false;

			int[] model = solver.model();
			solution = new boolean[variables];

			for (int i = 0; i < model.length; i++) {
				int a = model[i];
				if (a == 0 || Math.abs(i) > variables)
					throw new RuntimeException(
							"Sat4J produced unexpected output");

				if (a > 0)
					solution[a - 1] = true;
			}

			return true;
		} catch (TimeoutException e) {
			System.err.println("SAT4J timeout");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Integer variable() {
		int a = solver.newVar(++variables);
		assert a == variables;

		return variables;
	}

	public void clause(int[] clause) {
		try {
			solver.addClause(new VecInt(clause));
		} catch (ContradictionException e) {
			solution = null;
		}
	}

	@Override
	public void clause(List<Integer> clause) {
		int[] c = new int[clause.size()];
		for (int i = 0; i < clause.size(); i++)
			c[i] = clause.get(i);

		clause(c);
	}

	@Override
	public boolean decode(Integer term) {
		return solution[term - 1];
	}

	@Override
	public Integer not(Integer elem) {
		assert elem != 0 && elem != Integer.MIN_VALUE;
		return -elem;
	}
}
