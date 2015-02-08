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

package org.mmaroti.setsat;

import java.util.*;
import java.io.*;

public class Kneser {
	public static Set<ArrayList<Integer>> kneser(final int n, final int k,
			final int s) {
		assert n > 0 && k >= 0 && s >= 1;

		final SmallSet domain = new SmallSet(2 * n + k);
		final SmallSet counter = new SmallSet(n + 2);

		Set<ArrayList<Integer>> subset = new SubSet<ArrayList<Integer>>(
				new PowerSet<Integer, int[]>(BoolSet.INSTANCE, domain)) {
			@Override
			public int filter(Instance instance, ArrayList<Integer> elem) {
				return counter.eq(instance, counter.constant(n),
						counter.bitCount(instance, elem));
			}
		};

		if (s >= 2) {
			subset = new SubSet<ArrayList<Integer>>(subset) {
				@Override
				public int filter(Instance instance, ArrayList<Integer> elem) {
					int t = Instance.TRUE;

					for (int i = 0; i < elem.size(); i++)
						for (int x = 1; x < s; ++x) {
							int j = (i + x) % elem.size();
							int r = instance.and(elem.get(i), elem.get(j));
							t = instance.and(t, instance.not(r));
						}

					return t;
				}
			};
		}

		return subset;
	}

	public static int isDisjunct(Instance instance, ArrayList<Integer> elem1,
			ArrayList<Integer> elem2) {
		assert elem1.size() == elem2.size();

		int t = Instance.TRUE;
		for (int i = 0; i < elem1.size(); i++)
			t = instance.and(t,
					instance.not(instance.and(elem1.get(i), elem2.get(i))));

		return t;
	}

	public static Set<ArrayList<ArrayList<Integer>>> homomorphisms(
			Set<ArrayList<Integer>> set1, Set<ArrayList<Integer>> set2) {

		Set<ArrayList<ArrayList<Integer>>> hom = new PowerSet<ArrayList<Integer>, ArrayList<Integer>>(
				set1, set2);

		ArrayList<ArrayList<Integer>> vertices = new ArrayList<ArrayList<Integer>>(
				set2.elements());
		ArrayList<Pair<Integer, Integer>> edges = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 0; i < vertices.size(); i++)
			for (int j = 0; j < vertices.size(); j++) {
				if (Instance.lower(isDisjunct(Instance.BOOL, vertices.get(i),
						vertices.get(j))))
					edges.add(new Pair<Integer, Integer>(i, j));
			}

		hom = new SubSet<ArrayList<ArrayList<Integer>>>(hom) {
			@Override
			public int filter(Instance instance,
					ArrayList<ArrayList<Integer>> elem) {
				// TODO Auto-generated method stub
				return 0;
			}
		};

		return hom;
	}

	public static void main(String[] args) throws IOException {
		Solver solver = new MiniSAT();
		solver.debugging = false;

		Set<ArrayList<Integer>> set = kneser(5, 2, 2);

		// set.print(set.elements());
		System.out.println(set.elements().size());
		System.out.println(set.solveAll(solver).size());
	}
}
