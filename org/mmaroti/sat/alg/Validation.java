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

package org.mmaroti.sat.alg;

import java.text.*;
import java.util.*;
import org.mmaroti.sat.core.*;
import org.mmaroti.sat.solvers.*;

public class Validation {
	boolean failed = false;

	void verify(String msg, int count, int expected) {
		System.out.println("The number of " + msg + " is " + count + ".");
		if (count != expected) {
			System.out.println("FAILED, the correct value is " + expected);
			failed = true;
		}
	}

	void checkEquivalences() {
		BoolProblem problem = new BoolProblem(new int[] { 7, 7 }) {
			@Override
			public <ELEM> ELEM compute(BoolAlgebra<ELEM> alg,
					List<Tensor<ELEM>> tensors) {
				Relation<ELEM> rel = new Relation<ELEM>(alg, tensors.get(0));
				return rel.isEquivalence();
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("equivalences on a 7 element set", count, 877);
	}

	void checkPartialOrders() {
		BoolProblem problem = new BoolProblem(new int[] { 5, 5 }) {
			@Override
			public <ELEM> ELEM compute(BoolAlgebra<ELEM> alg,
					List<Tensor<ELEM>> tensors) {
				Relation<ELEM> rel = new Relation<ELEM>(alg, tensors.get(0));
				return rel.isPartialOrder();
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("partial orders on a 5 element set", count, 4231);
	}

	void checkPermutations() {
		BoolProblem problem = new BoolProblem(new int[] { 7, 7 }) {
			@Override
			public <ELEM> ELEM compute(BoolAlgebra<ELEM> alg,
					List<Tensor<ELEM>> tensors) {
				Operation<ELEM> op = new Operation<ELEM>(alg, tensors.get(0));
				return alg.and(op.isFunction(), op.isPermutation());
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("permutations on a 7 element set", count, 5040);
	}

	private static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	void check() {
		failed = false;

		long time = System.currentTimeMillis();
		checkEquivalences();
		checkPartialOrders();
		checkPermutations();
		time = System.currentTimeMillis() - time;

		System.out.println("Finished in " + TIME_FORMAT.format(0.001 * time)
				+ " seconds.");

		if (failed)
			System.out.println("*** SOME TESTS HAVE FAILED ***");
	}

	public static void main(String[] args) {
		new Validation().check();
	}
}
