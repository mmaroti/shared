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

package org.mmaroti.sat.math;

import java.util.*;

import org.mmaroti.sat.core.*;
import org.mmaroti.sat.solvers.*;
import org.mmaroti.sat.univalg.*;

public class GraphPoly {
	private SatSolver<?> solver;
	private Relation<Boolean> relation;

	public GraphPoly(SatSolver<?> solver, Relation<Boolean> relation) {
		this.solver = solver;
		this.relation = relation;
	}

	public static List<Operation<Boolean>> wrapOperations(Tensor<Boolean> tensor) {
		List<Operation<Boolean>> result = new ArrayList<Operation<Boolean>>();
		for (Tensor<Boolean> t : Tensor.unstack(tensor))
			result.add(Operation.wrap(t));

		return result;
	}

	public List<Operation<Boolean>> getBinaryOps() {
		int size = relation.getSize();
		BoolProblem prob = new BoolProblem(new int[] { size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {

				Operation<BOOL> op = Operation.wrap(alg, tensors.get(0));
				Relation<BOOL> rel = Relation.lift(alg, relation);

				BOOL res = op.isOperation();
				res = alg.and(res, op.preserves(rel));

				return res;
			}
		};

		return wrapOperations(prob.solveAll(solver).get(0));
	}

	public static void main(String[] args) {
		GraphPoly poly = new GraphPoly(new Sat4J(), null);
		poly.getBinaryOps();
	}
}
