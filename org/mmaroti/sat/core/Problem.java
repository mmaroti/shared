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

package org.mmaroti.sat.core;

import java.util.*;
import org.mmaroti.sat.solvers.*;

public abstract class Problem {
	protected final Map<String, int[]> shapes;

	public Map<String, int[]> getShapes() {
		return shapes;
	}

	public Problem(Map<String, int[]> shapes) {
		this.shapes = shapes;
	}

	public Problem(String name, int[] shape) {
		shapes = new TreeMap<String, int[]>();
		shapes.put(name, shape);
	}

	public Problem(String name1, int[] shape1, String name2, int[] shape2) {
		shapes = new TreeMap<String, int[]>();
		shapes.put(name1, shape1);
		shapes.put(name2, shape2);
	}

	public Problem(String name1, int[] shape1, String name2, int[] shape2,
			String name3, int[] shape3) {
		shapes = new TreeMap<String, int[]>();
		shapes.put(name1, shape1);
		shapes.put(name2, shape2);
		shapes.put(name3, shape3);
	}

	public abstract <BOOL> BOOL compute(BoolAlg<BOOL> alg,
			Map<String, Tensor<BOOL>> tensors);

	public boolean check(Map<String, Tensor<Boolean>> tensors) {
		return compute(BoolAlg.BOOLEAN, tensors);
	}

	public <BOOL> Map<String, Tensor<Boolean>> solveOne(SatSolver<BOOL> solver) {
		solver.clear();

		Map<String, Tensor<BOOL>> tensors = new TreeMap<String, Tensor<BOOL>>();
		for (String key : shapes.keySet())
			tensors.put(key, Tensor.generate(shapes.get(key), solver.VARIABLE));

		solver.clause(Arrays.asList(compute(solver, tensors)));

		if (!solver.solve())
			return null;

		Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
		for (String key : shapes.keySet())
			solution.put(key, Tensor.map(solver.DECODE, tensors.get(key)));

		assert check(solution);
		return solution;
	}

	public <BOOL> List<Map<String, Tensor<Boolean>>> solveAll(
			SatSolver<BOOL> solver, int maxCount) {
		solver.clear();

		Map<String, Tensor<BOOL>> tensors = new TreeMap<String, Tensor<BOOL>>();
		for (String key : shapes.keySet())
			tensors.put(key, Tensor.generate(shapes.get(key), solver.VARIABLE));

		solver.clause(Arrays.asList(compute(solver, tensors)));

		List<Map<String, Tensor<Boolean>>> solutions = new ArrayList<Map<String, Tensor<Boolean>>>();
		while (solver.solve()) {
			ArrayList<BOOL> exclude = new ArrayList<BOOL>();

			Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
			for (String key : shapes.keySet()) {
				Tensor<BOOL> t = tensors.get(key);
				Tensor<Boolean> s = Tensor.map(solver.DECODE, t);
				solution.put(key, s);

				t = Tensor.map2(solver.ADD, Tensor.map(solver.LIFT, s), t);
				for (BOOL b : t)
					exclude.add(b);
			}

			assert check(solution);
			solutions.add(solution);
			solver.clause(exclude);

			if (solutions.size() == maxCount) {
				System.err.println("... at least " + maxCount
						+ " solutions found, aborting.");
				return solutions;
			} else if (solutions.size() % 100000 == 0)
				System.err.println("... still working, " + solutions.size()
						+ " solutions so far ...");
		}

		return solutions;
	}

	public <BOOL> List<Map<String, Tensor<Boolean>>> solveAll(
			SatSolver<BOOL> solver) {
		return solveAll(solver, 0);
	}
}
