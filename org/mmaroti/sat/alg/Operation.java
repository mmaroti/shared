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

public class Operation<BOOL> {
	protected final BoolAlgebra<BOOL> alg;
	protected final Tensor<BOOL> tensor;

	public BoolAlgebra<BOOL> getAlg() {
		return alg;
	}

	public Tensor<BOOL> getTensor() {
		return tensor;
	}

	public int getSize() {
		return tensor.getDim(0);
	}

	public int getArity() {
		return tensor.getOrder() - 1;
	}

	public Operation(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		assert 1 <= tensor.getOrder();

		int size = tensor.getDim(0);
		for (int i = 1; i < tensor.getOrder(); i++)
			assert tensor.getDim(i) == size;

		this.alg = alg;
		this.tensor = tensor;

		if (getAlg() == BoolAlgebra.INSTANCE)
			assert (Boolean) isOperation();
	}

	public BOOL isOperation() {
		return asRelation().isFunction();
	}

	public Relation<BOOL> asRelation() {
		return new Relation<BOOL>(alg, tensor);
	}

	public BOOL isSurjective() {
		int[] map = new int[tensor.getOrder()];
		for (int i = 0; i < map.length - 1; i++)
			map[i] = i + 1;

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		tmp = Tensor.fold(alg.ANY, 1, tmp);
		tmp = Tensor.fold(alg.ALL, 1, tmp);

		return tmp.get();
	}

	private static int[] createShape(int size, int arity) {
		assert size > 1 && arity >= 0;

		int[] shape = new int[arity];
		for (int i = 0; i < arity; i++)
			shape[i] = size;
		return shape;
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

	public BOOL isSatisfied(int... identity) {
		assert getArity() == identity.length;

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
		return isSatisfied(new int[getArity()]);
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
		assert getAlg() == op.getAlg();
		assert getSize() == op.getSize();
	}

	public Operation<BOOL> compose(Operation<BOOL> op) {
		checkSize(op);
		assert getArity() == 1;

		int[] shape = createShape(getSize(), op.getArity() + 2);

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, new int[] { 1, 0 });

		int[] map = new int[op.getArity() + 1];
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
