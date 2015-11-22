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

package org.mmaroti.sat.univalg;

import java.text.*;
import java.util.*;
import org.mmaroti.sat.core.*;
import org.mmaroti.sat.solvers.*;

public class Validation {
	boolean failed = false;

	void verify(String msg, int count, int expected) {
		System.out.println(msg + " is " + count + ".");
		if (count != expected) {
			System.out
					.println("FAILED, the correct value is " + expected + ".");
			failed = true;
		}
	}

	void checkEquivalences() {
		BoolProblem problem = new BoolProblem(new int[] { 7, 7 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get(0));
				return rel.isEquivalence();
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A000110: the number of equivalences on a 7 element set", count,
				877);
	}

	void checkPartialOrders() {
		BoolProblem problem = new BoolProblem(new int[] { 5, 5 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get(0));
				return rel.isPartialOrder();
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A001035: the number of partial orders on a 5 element set",
				count, 4231);
	}

	void checkLinearExtensions() {
		PartialOrder<Boolean> c2 = PartialOrder.chain(2);
		PartialOrder<Boolean> c3 = PartialOrder.chain(3);
		final PartialOrder<Boolean> ord = c3.product(c2).product(c2);

		BoolProblem problem = new BoolProblem(new int[] { ord.getSize(),
				ord.getSize() }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get(0));
				Relation<BOOL> ord2 = Relation.lift(alg, ord.asRelation());
				return alg.and(ord2.isSubsetOf(rel), rel.isTotalOrder());
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A114714: the number of linear extensions of 2x2x4", count, 2452);
	}

	void checkPermutations() {
		BoolProblem problem = new BoolProblem(new int[] { 7, 7 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Permutation<BOOL> perm = new Permutation<BOOL>(alg,
						tensors.get(0));
				return perm.isPermutation();
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A000142: the number of permutations on a 7 element set", count,
				5040);
	}

	void checkAlternations() {
		BoolProblem problem = new BoolProblem(new int[] { 7, 7 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Permutation<BOOL> perm = new Permutation<BOOL>(alg,
						tensors.get(0));
				return alg.and(perm.isPermutation(), perm.isEven());
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A001710: The number of even permutations on a 7 element set",
				count, 2520);
	}

	void checkAntiChains() {
		final PartialOrder<Boolean> ord = PartialOrder.powerset(4);

		BoolProblem problem = new BoolProblem(new int[] { ord.getSize() }) {
			@Override
			public <BOOL> BOOL compute(BoolAlgebra<BOOL> alg,
					List<Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get(0));
				PartialOrder<BOOL> ord2 = PartialOrder.lift(alg, ord);
				return ord2.isAntiChain(rel);
			}
		};

		int count = problem.solveAll(new Sat4J()).get(0).getLastDim();
		verify("A000372: the number of antichains of 2^4", count, 168);
	}

	void check() {
		failed = false;

		long time = System.currentTimeMillis();
		parseRelations();
		checkEquivalences();
		checkPermutations();
		checkAntiChains();
		checkPartialOrders();
		checkAlternations();
		checkLinearExtensions();
		time = System.currentTimeMillis() - time;

		System.out.println("Finished in " + TIME_FORMAT.format(0.001 * time)
				+ " seconds.");

		if (failed)
			System.out.println("*** SOME TESTS HAVE FAILED ***");
	}

	void verify(String msg, String printout, String expected) {
		String passed;
		if (printout.equals(expected))
			passed = "passed";
		else {
			passed = "FAILED with " + printout;
			failed = true;
		}

		System.out.println(msg + ": " + passed + ".");
	}

	void parseRelations() {
		String a = "00 01 02 03 04 11 12 13 22 23 33 44 43";
		Relation<Boolean> rel = Relation.parseMembers(5, 2, a);
		String b = Relation.formatMembers(rel.asPartialOrder().covers());
		verify("Parsing the N5 poset relation and printing its covers", b,
				"01 04 12 23 43");
	}

	void parse() {
		parseRelations();
	}

	private static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	public static void main(String[] args) {
		Validation v = new Validation();
		v.check();
	}
}
