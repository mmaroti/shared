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

public class FanoPlane extends Problem {
	public FanoPlane(int dim, int lines) {
		super("x", new int[] { dim, lines }, "y", new int[] { dim, lines });
	}

	public static <BOOL> BOOL nonzero(BoolAlg<BOOL> alg, Tensor<BOOL> tensor) {
		return Tensor.fold(Tensor.fold(tensor, 1, alg.ANY), 1, alg.ALL).get();
	}

	@Override
	public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
			Map<String, Tensor<BOOL>> tensors) {
		Tensor<BOOL> x = tensors.get("x");
		Tensor<BOOL> y = tensors.get("y");
		Tensor<BOOL> xy = Tensor.map2(alg.ADD, x, y);

		BOOL good = nonzero(alg, x);
		good = alg.and(good, nonzero(alg, y));
		good = alg.and(good, nonzero(alg, xy));

		Tensor<BOOL> xvy = Tensor.reduce(alg.ALL, "ij", alg.EQU, x.named("di"),
				y.named("dj"));
		Tensor<BOOL> xvxy = Tensor.reduce(alg.ALL, "ij", alg.EQU, x.named("di"),
				xy.named("dj"));
		Tensor<BOOL> yvxy = Tensor.reduce(alg.ALL, "ij", alg.EQU, y.named("di"),
				xy.named("dj"));

		// test, line1, line2
		Tensor<BOOL> comp = Tensor.stack(Arrays.asList(xvy, xvxy, yvxy));
		comp = Tensor.fold(comp, 1, alg.ONE);

		Tensor<Boolean> diag = Tensor.generate(comp.getDim(0), comp.getDim(1),
				Func2.INT_EQ);
		comp = Tensor.map2(alg.OR, comp, Tensor.map(alg.LIFT, diag));

		// TODO: lines are compared twice
		good = alg.and(good, Tensor.fold(comp, 2, alg.ALL).get());

		return good;
	}
}
