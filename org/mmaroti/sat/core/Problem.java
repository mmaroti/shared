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
	protected final Map<String, Tensor<Boolean>> masks;

	public Problem(Map<String, Tensor<Boolean>> masks) {
		this.masks = masks;
	}

	public Problem(String name, Tensor<Boolean> mask) {
		masks = new TreeMap<String, Tensor<Boolean>>();
		masks.put(name, mask);
	}

	public Problem(String name, int[] shape) {
		masks = new TreeMap<String, Tensor<Boolean>>();
		masks.put(name, Tensor.constant(shape, Boolean.TRUE));
	}

	public Problem(String name1, Tensor<Boolean> mask1, String name2,
			Tensor<Boolean> mask2) {
		masks = new TreeMap<String, Tensor<Boolean>>();
		masks.put(name1, mask1);
		masks.put(name2, mask2);
	}

	public Problem(String name1, int[] shape1, String name2, int[] shape2) {
		masks = new TreeMap<String, Tensor<Boolean>>();
		masks.put(name1, Tensor.constant(shape1, Boolean.TRUE));
		masks.put(name2, Tensor.constant(shape2, Boolean.TRUE));
	}

	public abstract <BOOL> BOOL compute(BoolAlg<BOOL> alg,
			Map<String, Tensor<BOOL>> tensors);

	public boolean check(Map<String, Tensor<Boolean>> tensors) {
		return compute(BoolAlg.BOOLEAN, tensors);
	}

	public <BOOL> Map<String, Tensor<Boolean>> solveOne(SatSolver<BOOL> solver) {
		solver.clear();

		Map<String, Tensor<BOOL>> tensors = new TreeMap<String, Tensor<BOOL>>();
		for (String key : masks.keySet()) {
			int[] shape = masks.get(key).getShape();
			tensors.put(key, Tensor.generate(shape, solver.VARIABLE));
		}

		solver.clause(Arrays.asList(compute(solver, tensors)));

		if (!solver.solve())
			return null;

		Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
		for (String key : masks.keySet())
			solution.put(key, Tensor.map(solver.DECODE, tensors.get(key)));

		assert check(solution);
		return solution;
	}

	public <BOOL> Map<String, Tensor<Boolean>> solveAll(SatSolver<BOOL> solver,
			int maxCount) {
		solver.clear();

		Map<String, Tensor<BOOL>> tensors = new TreeMap<String, Tensor<BOOL>>();
		for (String key : masks.keySet()) {
			int[] shape = masks.get(key).getShape();
			tensors.put(key, Tensor.generate(shape, solver.VARIABLE));
		}

		solver.clause(Arrays.asList(compute(solver, tensors)));

		List<Map<String, Tensor<Boolean>>> solutions = new ArrayList<Map<String, Tensor<Boolean>>>();
		while (solver.solve()) {
			ArrayList<BOOL> exclude = new ArrayList<BOOL>();

			Map<String, Tensor<Boolean>> solution = new TreeMap<String, Tensor<Boolean>>();
			for (String key : masks.keySet()) {
				Tensor<BOOL> t = tensors.get(key);
				Tensor<Boolean> s = Tensor.map(solver.DECODE, t);
				solution.put(key, s);

				t = Tensor.map2(solver.ADD, Tensor.map(solver.LIFT, s), t);

				Iterator<Boolean> iter = masks.get(key).iterator();
				for (BOOL b : t) {
					if (iter.next())
						exclude.add(b);
				}
				assert !iter.hasNext();
			}

			assert check(solution);
			solutions.add(solution);
			solver.clause(exclude);

			if (solutions.size() == maxCount) {
				System.out.println("... at least " + maxCount
						+ " solutions found, aborting.");
				break;
			} else if (solutions.size() % 100000 == 0)
				System.out.println("... still working, " + solutions.size()
						+ " solutions so far ...");
		}

		Map<String, Tensor<Boolean>> result = new HashMap<String, Tensor<Boolean>>();
		for (String key : masks.keySet()) {
			List<Tensor<Boolean>> list = new ArrayList<Tensor<Boolean>>();
			for (Map<String, Tensor<Boolean>> solution : solutions)
				list.add(solution.get(key));

			int[] shape = masks.get(key).getShape();
			result.put(key, Tensor.concat(shape, list));
		}

		return result;
	}

	public <BOOL> Map<String, Tensor<Boolean>> solveAll(SatSolver<BOOL> solver) {
		return solveAll(solver, 0);
	}
}
