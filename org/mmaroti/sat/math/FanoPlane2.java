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
	public static class Planes extends BoolProblem {
		public Tensor<Boolean> pts;

		public Planes() {
			super(new int[] { 7, 3 });

			pts = Tensor.matrix(3, 7, Arrays.asList(true, false, false, false,
					true, false, false, false, true, true, true, false, true,
					false, true, false, true, true, true, true, true));
		}

		@Override
		public <ELEM> ELEM compute(BoolAlgebra<ELEM> alg,
				List<Tensor<ELEM>> tensors) {
			Tensor<ELEM> g = tensors.get(0);
			Tensor<ELEM> p = Tensor.map(alg.LIFT, pts);

			Tensor<ELEM> t = Tensor.reduce(alg.SUM, "ik", alg.AND,
					g.named("ij"), p.named("jk"));
			ELEM b = Tensor.fold(alg.ALL, 1, Tensor.fold(alg.ANY, 1, t)).get();

			List<Tensor<ELEM>> xs = Tensor.unstack(t);

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

	public static void main(String[] args) {
		Planes p = new Planes();
		System.out.println(p.solveAll(new Sat4J()).size());
	}
}
