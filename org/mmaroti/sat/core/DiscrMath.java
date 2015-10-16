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

package org.mmaroti.sat.core;

import java.util.*;
import org.mmaroti.sat.solvers.*;

public class DiscrMath<BOOL> {
	public final BoolAlg<BOOL> alg;

	public DiscrMath(BoolAlg<BOOL> alg) {
		this.alg = alg;
	}

	BOOL isFunction(Tensor<BOOL> tensor) {
		assert tensor.getOrder() >= 1;

		tensor = Tensor.fold(alg.ONE, 1, tensor);
		tensor = Tensor.fold(alg.ALL, tensor.getOrder(), tensor);

		return tensor.get();
	}

	BOOL isPermutation(Tensor<BOOL> tensor) {
		assert tensor.getOrder() == 2;
		assert tensor.getDim(0) == tensor.getDim(1);

		BOOL a = isFunction(tensor);
		BOOL b = isFunction(inverse(tensor));
		return alg.and(a, b);
	}

	Tensor<BOOL> inverse(Tensor<BOOL> tensor) {
		int[] shape = tensor.getShape();
		assert shape.length == 2;

		return Tensor.reshape(tensor, new int[] { shape[1], shape[0] },
				new int[] { 1, 0 });
	}

	Tensor<BOOL> compose(Tensor<BOOL> tensor, Tensor<BOOL> arg1) {
		assert tensor.getOrder() == 2 && arg1.getOrder() >= 1;
		assert tensor.getDim(1) == arg1.getDim(0);

		int[] shape = new int[1 + arg1.getOrder()];
		shape[0] = tensor.getDim(1);
		shape[1] = tensor.getDim(0);
		System.arraycopy(arg1.getShape(), 1, shape, 2, shape.length - 1);
		tensor = Tensor.reshape(tensor, shape, new int[] { 1, 0 });

		int[] map = new int[arg1.getOrder()];
		for (int i = 1; i < map.length; i++)
			map[i] = i + 1;
		arg1 = Tensor.reshape(arg1, shape, map);

		tensor = Tensor.map2(alg.AND, tensor, arg1);
		tensor = Tensor.fold(alg.ANY, 1, tensor);

		return tensor;
	}

	public Tensor<BOOL> projection(final int size, final int arity,
			final int coord) {
		assert size >= 0 && arity >= 1 && 0 <= coord && coord < arity;

		int[] shape = new int[1 + arity];
		Arrays.fill(shape, size);

		return Tensor.generate(shape, new Func1<BOOL, int[]>() {
			@Override
			public BOOL call(int[] index) {
				return alg.lift(index[0] == index[1 + coord]);
			}
		});
	}

	public Tensor<BOOL> lift(Tensor<Boolean> tensor) {
		return Tensor.map(alg.LIFT, tensor);
	}

	public Tensor<BOOL> graph(final Tensor<Integer> tensor, final int domain) {
		assert domain >= 0;

		final int[] arg = new int[tensor.getOrder()];

		int[] shape = new int[1 + tensor.getOrder()];
		shape[0] = domain;
		System.arraycopy(tensor.getShape(), 0, shape, 1, arg.length);

		return Tensor.generate(shape, new Func1<BOOL, int[]>() {
			@Override
			public BOOL call(int[] elem) {
				System.arraycopy(elem, 1, arg, 0, arg.length);
				int res = tensor.getElem(arg);
				assert (0 <= res && res < domain);

				return alg.lift(res == elem[0]);
			}
		});
	}

	public static void main(String[] args) {
		Map<String, int[]> shapes = new HashMap<String, int[]>();
		shapes.put("f", new int[] { 3, 3 });

		Problem problem = new Problem(shapes) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				DiscrMath<BOOL> discr = new DiscrMath<BOOL>(alg);
				return discr.isPermutation(tensors.get("f"));
			}
		};

		Map<String, Tensor<Boolean>> solutions = problem.solveAll(
				new Sat4J(), 2);
		System.out.println(solutions.get("f"));
		// for (Map<String, Tensor<Boolean>> solution : solutions)
		// Tensor.print(solution, System.out);
	}

	public static void main2(String[] args) {
		DiscrMath<Boolean> discr = new DiscrMath<Boolean>(BoolAlg.BOOLEAN);

		Tensor<Integer> f = Tensor.matrix(new int[] { 3, 2 },
				Arrays.asList(0, 1, 2, 0, 2, 1));
		Tensor<Boolean> g = discr.graph(f, 3);

		Tensor<Boolean> h = Tensor.reduce(BoolAlg.BOOLEAN.ANY, "xzpq",
				BoolAlg.BOOLEAN.AND, g.named("xyp"), g.named("yzq"));
		Tensor<Boolean> m = Tensor.reduce(BoolAlg.BOOLEAN.ALL, "pqr",
				BoolAlg.BOOLEAN.EQU, h.named("xypq"), g.named("xyr"));

		Tensor.print(m, System.out);
	}
}
