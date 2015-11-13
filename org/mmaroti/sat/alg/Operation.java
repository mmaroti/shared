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

public class Operation<BOOL> extends Relation<BOOL> {
	public int getOpArity() {
		return tensor.getOrder() - 1;
	}

	public Operation(AlgObject<BOOL> object) {
		super(object);

		if (getBoolAlg() == BoolAlgebra.INSTANCE)
			assert (Boolean) isFunction();
	}

	public Operation(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		this(new Relation<BOOL>(alg, tensor));
	}

	public BOOL isPermutation() {
		assert getOpArity() == 1;

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), new int[] { 1, 0 });
		tmp = Tensor.fold(alg.ANY, 1, tmp);
		tmp = Tensor.fold(alg.ALL, 1, tmp);

		return tmp.get();
	}

	public static <BOOL> Operation<BOOL> makeProjection(
			final BoolAlgebra<BOOL> alg, int size, int arity, final int coord) {
		assert 0 <= coord && coord < arity;

		Tensor<BOOL> tensor = Tensor.generate(createShape(size, 1 + arity),
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

	public BOOL isSatisfied(int... identity) {
		assert getOpArity() == identity.length;

		int[] map = new int[identity.length + 1];

		int vars = 0;
		for (int i = 0; i < identity.length; i++) {
			vars = Math.max(vars, 1 + identity[i]);
			map[1 + i] = identity[i];
		}

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		tmp = Tensor.fold(alg.ALL, vars, tmp);
		return tmp.get();
	}

	public BOOL isIdempotent() {
		return isSatisfied(new int[getOpArity()]);
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

	public Operation<BOOL> composeOperation(Operation<BOOL> op) {
		checkSize(op);
		assert getOpArity() == 1;

		int[] shape = createShape(size, op.getOpArity() + 2);

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, new int[] { 1, 0 });

		int[] map = new int[op.getOpArity() + 1];
		for (int i = 0; i < map.length; i++)
			map[i] = i + 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(op.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Operation<BOOL>(alg, tmp);
	}

	public static <BOOL> Operation<BOOL> lift(BoolAlgebra<BOOL> alg,
			Operation<Boolean> op) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, op.tensor);
		return new Operation<BOOL>(alg, tensor);
	}
}
