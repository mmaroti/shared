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
	public boolean debugging = true;

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

	private final Func1<Integer, int[]> GENERATOR = new Func1<Integer, int[]>() {
		@Override
		public Integer call(int[] elem) {
			return variable();
		}
	};

	public final Tensor<Integer> tensor(int... shape) {
		return Tensor.generate(shape, GENERATOR);
	}

	public void ensure(int... clause) {
		clauses.add(clause);
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

	// variable indices in clauses and solution start at 1
	public abstract boolean[] solve();

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

	public Map<String, Tensor<Boolean>> solveOne(Problem problem) {
		Map<String, int[]> shapes = problem.getShapes();

		Map<String, Tensor<Integer>> tensors = new TreeMap<String, Tensor<Integer>>();
		for (String key : shapes.keySet())
			tensors.put(key, tensor(shapes.get(key)));

		clear();
		ensure(problem.compute(this, tensors));
		final boolean[] sol = solve();
		if (sol == null)
			return null;

		Func1<Boolean, Integer> LOOKUP = new Func1<Boolean, Integer>() {
			@Override
			public Boolean call(Integer elem) {
				return sol[elem];
			}
		};

		Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
		for (String key : tensors.keySet())
			solution.put(key, Tensor.map(LOOKUP, tensors.get(key)));

		return solution;
	}

	public List<Map<String, Tensor<Boolean>>> solveAll(Problem problem) {
		Map<String, int[]> shapes = problem.getShapes();

		Map<String, Tensor<Integer>> tensors = new TreeMap<String, Tensor<Integer>>();
		int vars = 0;

		for (String key : shapes.keySet()) {
			int[] shape = shapes.get(key);
			vars += Tensor.getSize(shape);
			tensors.put(key, tensor(shape));
		}

		clear();
		ensure(problem.compute(this, tensors));

		List<Map<String, Tensor<Boolean>>> solutions = new ArrayList<Map<String, Tensor<Boolean>>>();
		for (;;) {
			final boolean[] sol = solve();
			if (sol == null)
				return solutions;

			final int[] exclude = new int[vars];

			Func1<Boolean, Integer> LOOKUP = new Func1<Boolean, Integer>() {
				int index = 0;

				@Override
				public Boolean call(Integer elem) {
					exclude[index++] = sol[elem] ? -elem : elem;
					return sol[elem];
				}
			};

			Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
			for (String key : tensors.keySet())
				solution.put(key, Tensor.map(LOOKUP, tensors.get(key)));

			if (solutions.size() == -20000) {
				System.err.println("... more than " + solutions.size()
						+ " solutions, aborting.");

				return solutions;
			}

			solutions.add(solution);
			ensure(exclude);

			if (solutions.size() % 100000 == 0)
				System.err.println("... still working, " + solutions.size()
						+ " solutions so far ...");
		}
	}
}
