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

public class FanoPlane2 {
	public static class Planes extends Problem {
		public Tensor<Boolean> pts;

		public Planes() {
			super("gen", new int[] { 7, 3 });

			pts = Tensor.matrix(new int[] { 3, 7 }, Arrays.asList(true, false,
					false, false, true, false, false, false, true, true, true,
					false, true, false, true, false, true, true, true, true,
					true));
		}

		@Override
		public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
				Map<String, Tensor<BOOL>> tensors) {
			Tensor<BOOL> g = tensors.get("gen");
			Tensor<BOOL> p = Tensor.map(alg.LIFT, pts);

			Tensor<BOOL> t = Tensor.reduce(alg.SUM, "ik", alg.AND,
					g.named("ij"), p.named("jk"));
			BOOL b = Tensor.fold(alg.ALL, 1, Tensor.fold(alg.ANY, 1, t)).get();

			List<Tensor<BOOL>> xs = Tensor.unconcat(t);

			b = alg.and(b, alg.lexless(xs.get(0), xs.get(1)));
			b = alg.and(b, alg.lexless(xs.get(0), xs.get(2)));
			b = alg.and(b, alg.lexless(xs.get(0), xs.get(3)));
			b = alg.and(b, alg.lexless(xs.get(0), xs.get(4)));
			b = alg.and(b, alg.lexless(xs.get(0), xs.get(5)));
			b = alg.and(b, alg.lexless(xs.get(0), xs.get(6)));

			b = alg.and(b, alg.lexless(xs.get(1), xs.get(2)));
			b = alg.and(b, alg.lexless(xs.get(1), xs.get(3)));
			b = alg.and(b, alg.lexless(xs.get(1), xs.get(4)));
			b = alg.and(b, alg.lexless(xs.get(1), xs.get(5)));
			b = alg.and(b, alg.lexless(xs.get(1), xs.get(6)));

			b = alg.and(b, alg.lexless(xs.get(2), xs.get(4)));
			b = alg.and(b, alg.lexless(xs.get(2), xs.get(5)));
			b = alg.and(b, alg.lexless(xs.get(2), xs.get(6)));

			return b;
		}
	}

	public static Tensor<Boolean> collect(
			List<Map<String, Tensor<Boolean>>> solutions, String name) {
		List<Tensor<Boolean>> list = new ArrayList<Tensor<Boolean>>();
		for (Map<String, Tensor<Boolean>> solution : solutions)
			list.add(solution.get(name));

		return Tensor.stack(list);
	}

	public static void main(String[] args) {
		Planes p = new Planes();
		System.out.println(p.solveAll(new Sat4J()).size());
	}
}
