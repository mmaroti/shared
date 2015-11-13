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
	private final BoolAlg<BOOL> alg;
	private final Tensor<BOOL> tensor;
	private final int size;
	private final int arity;

	public BoolAlg<BOOL> getBoolAlg() {
		return alg;
	}

	public Tensor<BOOL> getTensor() {
		return tensor;
	}

	public int getSize() {
		return size;
	}

	public int getArity() {
		return arity;
	}

	public Operation(Relation<BOOL> relation) {
		alg = relation.getBoolAlg();
		if (alg == BoolAlg.BOOLEAN)
			assert (Boolean) relation.isFunction();

		tensor = relation.getTensor();
		size = relation.getSize();
		arity = relation.getArity() - 1;
	}

	public Operation(BoolAlg<BOOL> alg, Tensor<BOOL> tensor) {
		this(new Relation<BOOL>(alg, tensor));
	}

	public BOOL isFunction() {
		Tensor<BOOL> rel = Tensor.fold(alg.ONE, 1, tensor);
		return Tensor.fold(alg.ALL, rel.getOrder(), rel).get();
	}

	public BOOL isPermutation() {
		assert arity == 1;

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), new int[] { 1, 0 });
		tmp = Tensor.fold(alg.ANY, 1, tmp);
		tmp = Tensor.fold(alg.ALL, 1, tmp);

		return tmp.get();
	}

	private static int[] createShape(int size, int arity) {
		assert size > 1 && arity >= 0;

		int[] shape = new int[1 + arity];
		for (int i = 0; i <= arity; i++)
			shape[i] = size;
		return shape;
	}

	public static <BOOL> Operation<BOOL> opProjection(final BoolAlg<BOOL> alg,
			int size, int arity, final int coord) {
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

	public static <BOOL> Operation<BOOL> lift(BoolAlg<BOOL> alg,
			Operation<Boolean> op) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, op.tensor);
		return new Operation<BOOL>(alg, tensor);
	}
}
