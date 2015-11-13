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

import java.text.DecimalFormat;
import java.util.Map;

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
		Problem problem = new Problem("rel", new int[] { 7, 7 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get("rel"));
				return rel.isEquivalence();
			}
		};

		int count = problem.solveAll(new Sat4J()).get("rel").getDim(2);
		verify("equivalences on a 7 element set", count, 877);
	}

	void checkPartialOrders() {
		Problem problem = new Problem("rel", new int[] { 5, 5 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {
				Relation<BOOL> rel = new Relation<BOOL>(alg, tensors.get("rel"));
				return rel.isPartialOrder();
			}
		};

		int count = problem.solveAll(new Sat4J()).get("rel").getDim(2);
		verify("partial orders on a 5 element set", count, 4231);
	}

	void checkPermutations() {
		Problem problem = new Problem("op", new int[] { 7, 7 }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {
				Operation<BOOL> op = new Operation<BOOL>(alg, tensors.get("op"));
				return alg.and(op.isFunction(), op.isPermutation());
			}
		};

		int count = problem.solveAll(new Sat4J()).get("op").getDim(2);
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
