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

import org.mmaroti.sat.core.*;

public class Operation<BOOL> {
	public final BoolAlg<BOOL> alg;
	public final Tensor<BOOL> tensor;
	public final int size;
	public final int arity;

	public Operation(BoolAlg<BOOL> alg, Tensor<BOOL> tensor) {
		arity = tensor.getOrder() - 1;
		assert arity >= 0;

		size = tensor.getDim(0);
		assert size >= 1;
		for (int i = 1; i < arity; i++)
			assert tensor.getDim(i) == size;

		this.tensor = tensor;
		this.alg = alg;
	}

	private static int[] createShape(int size, int arity) {
		assert size > 1 && arity >= 0;

		int[] shape = new int[1 + arity];
		for (int i = 0; i <= arity; i++)
			shape[i] = size;
		return shape;
	}

	public static <BOOL> Operation<BOOL> createProjection(
			final BoolAlg<BOOL> alg, int size, int arity, final int coord) {
		assert 0 <= coord && coord < arity;

		Tensor<BOOL> tensor = Tensor.generate(createShape(size, arity),
				new Func1<BOOL, int[]>() {
					@Override
					public BOOL call(int[] elem) {
						return alg.lift(elem[0] == elem[1 + coord]);
					}
				});

		return new Operation<BOOL>(alg, tensor);
	}

	public Relation<BOOL> graph() {
		return new Relation<BOOL>(alg, tensor);
	}

	public static String getArityName(int arity) {
		if (arity == 0)
			return "nullary";
		else if (arity == 1)
			return "unary";
		else if (arity == 2)
			return "binary";
		else if (arity == 3)
			return "ternary";
		else
			return "" + arity + "-ary";
	}

	public BOOL isEqual(Operation<BOOL> op) {
		checkSize(op);
		assert arity == op.arity;

		Tensor<BOOL> tmp = Tensor.map2(alg.LEQ, tensor, op.tensor);
		tmp = Tensor.fold(alg.ALL, arity, tmp);
		return tmp.get();
	}

	public BOOL isSatisfied(int... identity) {
		assert arity == identity.length;

		int[] map = new int[identity.length + 1];

		int vars = 0;
		for (int i = 0; i < identity.length; i++) {
			vars = Math.max(vars, 1 + identity[i]);
			map[1 + i] = identity[i];
		}

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, createShape(size, vars), map);
		tmp = Tensor.fold(alg.ALL, vars, tmp);
		return tmp.get();
	}

	public BOOL isIdempotent() {
		return isSatisfied(new int[arity]);
	}

	public BOOL isIdentity() {
		return isSatisfied(0, 0);
	}

	public BOOL isMajority() {
		BOOL b = isSatisfied(1, 0, 0);
		b = alg.and(b, isSatisfied(0, 1, 0));
		b = alg.and(b, isSatisfied(0, 0, 1));
		return b;
	}

	public BOOL isMinority() {
		BOOL b = isSatisfied(0, 1, 1);
		b = alg.and(b, isSatisfied(1, 0, 1));
		b = alg.and(b, isSatisfied(1, 1, 0));
		return b;
	}

	public BOOL isMaltsev() {
		BOOL b = isSatisfied(0, 1, 1);
		b = alg.and(b, isSatisfied(1, 1, 0));
		return b;
	}

	private void checkSize(Operation<BOOL> op) {
		assert alg == op.alg && size == op.size;
	}

	public Operation<BOOL> compose(Operation<BOOL> op) {
		checkSize(op);
		assert arity == 1;

		int[] shape = createShape(size, op.arity + 2);

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, new int[] { 1, 0 });

		int[] map = new int[op.arity + 1];
		for (int i = 0; i < map.length; i++)
			map[i] = i + 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(op.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Operation<BOOL>(alg, tmp);
	}
}
