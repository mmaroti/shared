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

import java.io.*;
import java.util.*;

public abstract class Set<A> {
	public abstract Collection<A> elements();

	public abstract int eq(Instance instance, A arg1, A arg2);

	public void print(Collection<A> elems) {
		for (A elem : elems)
			System.out.println(show(elem));
	}

	public abstract String show(A elem);

	public Collection<A> solveAll(Solver solver) throws IOException {
		ArrayList<A> sols = new ArrayList<A>();

		Instance instance = new Instance();
		A elem = generate(instance);

		for (;;) {
			boolean[] solution = solver.solve(instance);
			if (solution == null)
				return sols;

			sols.add(decode(elem, solution));
			instance.ensure(exclude(instance, elem, solution));
		}
	}

	public A solveOne(Solver solver) throws IOException {
		Instance instance = new Instance();
		A elem = generate(instance);

		boolean[] solution = solver.solve(instance);
		if (solution == null)
			return null;

		return decode(elem, solution);
	}

	public abstract A generate(Instance instance);

	public abstract A decode(A elem, boolean[] solution);

	public abstract int exclude(Instance instance, A elem, boolean[] solution);
}
