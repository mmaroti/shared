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

package org.mmaroti.ua.sat;

import java.util.*;

public class MonadInt extends Problem {
	final int size;
	final Tensor<Boolean> monoid;

	MonadInt(final int size, final int[] monoid) {
		super("func", new int[] { size, size, size });
		assert monoid.length % size == 0;

		this.size = size;

		List<Integer> elems = new ArrayList<Integer>();
		for (int a : monoid)
			elems.add(a);

		this.monoid = Tensor.generate(new int[] { size, size,
				monoid.length / size }, new Func1<Boolean, int[]>() {
			@Override
			public Boolean call(int[] elem) {
				int i = elem[1] + elem[2] * size;
				return monoid[i] == elem[0];
			}
		});
	}

	public <BOOL> BOOL isFunction(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		func = Tensor.fold(func, 1, alg.ONE);
		func = Tensor.fold(func, func.getOrder(), alg.ALL);
		return func.get();
	}

	public <BOOL> BOOL doesStabilize(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xytp", alg.AND,
				func.named("xyz"), monoid.named("ztp"));
		t = Tensor.reduce(alg.ANY, "xtpq", alg.AND, t.named("xytp"),
				monoid.named("ytq"));
		t = Tensor.reduce(alg.ALL, "rpq", alg.EQ, t.named("xtpq"),
				monoid.named("xtr"));
		t = Tensor.fold(Tensor.fold(t, 1, alg.ONE), 2, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL isEssential(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		Tensor<BOOL> t;

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 2, alg.ALL);
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 2, alg.ALL);
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	@Override
	public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
			Map<String, Tensor<BOOL>> tensors) {
		Tensor<BOOL> func = tensors.get("func");

		BOOL res = isFunction(alg, func);
		res = alg.and(res, doesStabilize(alg, func));
		res = alg.and(res, isEssential(alg, func));

		return res;
	}

	public static void main(String[] args) {
		MonadInt prob = new MonadInt(3, new int[] { 0,0,0, 1,1,1, 0,1,2 });
		Tensor.print(prob.solveAll(new MiniSat()), System.out);
	}
}
