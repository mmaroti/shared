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

package org.mmaroti.satold2;

import java.util.*;
import java.io.*;

public class Test {
	public static void main(String[] args) throws IOException {
		/*
		 * SubSet<Pair<int[], Integer>> set = new SubSet<Pair<int[], Integer>>(
		 * new ProdSet<int[], Integer>(new SmallSet(4), BoolSet.INSTANCE)) {
		 * 
		 * @Override public int filter(Instance instance, Pair<int[], Integer>
		 * elem) { return instance.or(elem.a[0], elem.b); } };
		 */

		/*
		 * SubSet<ArrayList<Integer>> set = new SubSet<ArrayList<Integer>>( new
		 * PowerSet<Integer, int[]>(BoolSet.INSTANCE, new SmallSet(3))) {
		 * 
		 * @Override public int filter(Instance instance, ArrayList<Integer>
		 * elem) { return instance.add(instance.leq(elem.get(0), elem.get(1)),
		 * elem.get(2)); } };
		 */

		final SmallSet dom = new SmallSet(6);
		SubSet<ArrayList<Integer>> set = new SubSet<ArrayList<Integer>>(
				new PowerSet<Integer, int[]>(BoolSet.INSTANCE, dom)) {
			@Override
			public int filter(Instance instance, ArrayList<Integer> elem) {
				return dom.eq(instance, dom.constant(1),
						dom.bitCount(instance, elem));
			}
		};

		set.print(set.elements());
		System.out.println();

		// Instance instance = new Instance();
		// set.generate(instance);
		// instance.printDimacs(System.out);

		set.print(set.solveAll(new MiniSAT()));
	}
}
