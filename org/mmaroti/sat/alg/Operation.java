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

package org.mmaroti.sat.alg;

import org.mmaroti.sat.core.*;

public class Operation<ELEM> extends Relation<ELEM> {
	public int getOpArity() {
		return tensor.getOrder() - 1;
	}

	public Operation(AlgObject<ELEM> object) {
		super(object);

		if (getBoolAlg() == BoolAlgebra.INSTANCE)
			assert (Boolean) isFunction();
	}

	public Operation(BoolAlgebra<ELEM> alg, Tensor<ELEM> tensor) {
		this(new Relation<ELEM>(alg, tensor));
	}

	public ELEM isPermutation() {
		assert getOpArity() == 1;

		Tensor<ELEM> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), new int[] { 1, 0 });
		tmp = Tensor.fold(alg.ANY, 1, tmp);
		tmp = Tensor.fold(alg.ALL, 1, tmp);

		return tmp.get();
	}

	public static <ELEM> Operation<ELEM> makeProjection(
			final BoolAlgebra<ELEM> alg, int size, int arity, final int coord) {
		assert 0 <= coord && coord < arity;

		Tensor<ELEM> tensor = Tensor.generate(createShape(size, 1 + arity),
				new Func1<ELEM, int[]>() {
					@Override
					public ELEM call(int[] elem) {
						return alg.lift(elem[0] == elem[1 + coord]);
					}
				});
		return new Operation<ELEM>(alg, tensor);
	}

	public Relation<ELEM> graph() {
		return new Relation<ELEM>(alg, tensor);
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

	public ELEM isSatisfied(int... identity) {
		assert getOpArity() == identity.length;

		int[] map = new int[identity.length + 1];

		int vars = 0;
		for (int i = 0; i < identity.length; i++) {
			vars = Math.max(vars, 1 + identity[i]);
			map[1 + i] = identity[i];
		}

		Tensor<ELEM> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		tmp = Tensor.fold(alg.ALL, vars, tmp);
		return tmp.get();
	}

	public ELEM isIdempotent() {
		return isSatisfied(new int[getOpArity()]);
	}

	public ELEM isIdentity() {
		return isSatisfied(0, 0);
	}

	public ELEM isMajority() {
		ELEM b = isSatisfied(1, 0, 0);
		b = alg.and(b, isSatisfied(0, 1, 0));
		b = alg.and(b, isSatisfied(0, 0, 1));
		return b;
	}

	public ELEM isMinority() {
		ELEM b = isSatisfied(0, 1, 1);
		b = alg.and(b, isSatisfied(1, 0, 1));
		b = alg.and(b, isSatisfied(1, 1, 0));
		return b;
	}

	public ELEM isMaltsev() {
		ELEM b = isSatisfied(0, 1, 1);
		b = alg.and(b, isSatisfied(1, 1, 0));
		return b;
	}

	public Operation<ELEM> composeOperation(Operation<ELEM> op) {
		checkSize(op);
		assert getOpArity() == 1;

		int[] shape = createShape(size, op.getOpArity() + 2);

		Tensor<ELEM> tmp = Tensor.reshape(tensor, shape, new int[] { 1, 0 });

		int[] map = new int[op.getOpArity() + 1];
		for (int i = 0; i < map.length; i++)
			map[i] = i + 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(op.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Operation<ELEM>(alg, tmp);
	}

	public static <BOOL> Operation<BOOL> lift(BoolAlgebra<BOOL> alg,
			Operation<Boolean> op) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, op.tensor);
		return new Operation<BOOL>(alg, tensor);
	}
}
