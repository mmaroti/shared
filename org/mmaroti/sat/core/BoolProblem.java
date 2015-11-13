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

public abstract class BoolProblem {
	protected final List<Tensor<Boolean>> masks;

	public BoolProblem(List<Tensor<Boolean>> masks) {
		this.masks = masks;
	}

	public BoolProblem(Tensor<Boolean> mask) {
		masks = new ArrayList<Tensor<Boolean>>();
		masks.add(mask);
	}

	public BoolProblem(int[] shape) {
		masks = new ArrayList<Tensor<Boolean>>();
		masks.add(Tensor.constant(shape, Boolean.TRUE));
	}

	public BoolProblem(String name1, Tensor<Boolean> mask1, String name2,
			Tensor<Boolean> mask2) {
		masks = new ArrayList<Tensor<Boolean>>();
		masks.add(mask1);
		masks.add(mask2);
	}

	public BoolProblem(int[] shape1, int[] shape2) {
		masks = new ArrayList<Tensor<Boolean>>();
		masks.add(Tensor.constant(shape1, Boolean.TRUE));
		masks.add(Tensor.constant(shape2, Boolean.TRUE));
	}

	public abstract <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
			List<Tensor<BOOL>> tensors);

	public boolean check(List<Tensor<Boolean>> tensors) {
		return compute(BoolAlgebra.INSTANCE, tensors);
	}

	public <BOOL> List<Tensor<Boolean>> solveOne(SatSolver<BOOL> solver) {
		solver.clear();

		List<Tensor<BOOL>> tensors = new ArrayList<Tensor<BOOL>>();
		for (Tensor<Boolean> mask : masks)
			tensors.add(Tensor.generate(mask.getShape(), solver.VARIABLE));

		solver.clause(Arrays.asList(compute(solver, tensors)));

		if (!solver.solve())
			return null;

		List<Tensor<Boolean>> solution = new ArrayList<Tensor<Boolean>>();
		for (Tensor<BOOL> tensor : tensors)
			solution.add(Tensor.map(solver.DECODE, tensor));

		assert check(solution);
		return solution;
	}

	public <BOOL> List<Tensor<Boolean>> solveAll(SatSolver<BOOL> solver,
			int maxCount) {
		solver.clear();

		List<Tensor<BOOL>> tensors = new ArrayList<Tensor<BOOL>>();
		for (Tensor<Boolean> mask : masks)
			tensors.add(Tensor.generate(mask.getShape(), solver.VARIABLE));

		solver.clause(Arrays.asList(compute(solver, tensors)));

		List<List<Tensor<Boolean>>> solutions = new ArrayList<List<Tensor<Boolean>>>();
		while (solver.solve()) {
			List<BOOL> exclude = new ArrayList<BOOL>();

			List<Tensor<Boolean>> solution = new ArrayList<Tensor<Boolean>>();
			for (int key = 0; key < masks.size(); key++) {
				Tensor<BOOL> t = tensors.get(key);
				Tensor<Boolean> s = Tensor.map(solver.DECODE, t);
				solution.add(s);

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

		List<Tensor<Boolean>> result = new ArrayList<Tensor<Boolean>>();
		for (int key = 0; key < masks.size(); key++) {
			List<Tensor<Boolean>> list = new ArrayList<Tensor<Boolean>>();
			for (List<Tensor<Boolean>> solution : solutions)
				list.add(solution.get(key));

			int[] shape = masks.get(key).getShape();
			result.add(Tensor.concat(shape, list));
		}

		return result;
	}

	public <BOOL> List<Tensor<Boolean>> solveAll(SatSolver<BOOL> solver) {
		return solveAll(solver, 0);
	}
}
