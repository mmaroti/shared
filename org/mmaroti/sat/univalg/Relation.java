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

public class Relation<ELEM> {
	private final BoolAlgebra<ELEM> alg;
	private final Tensor<ELEM> tensor;
	private final int size;
	private final int arity;

	public BoolAlgebra<ELEM> getBoolAlg() {
		return alg;
	}

	public Tensor<ELEM> getTensor() {
		return tensor;
	}

	public int getSize() {
		return size;
	}

	public int getArity() {
		return arity;
	}

	public Relation(BoolAlgebra<ELEM> alg, Tensor<ELEM> tensor) {
		arity = tensor.getOrder();
		assert 1 <= arity;

		size = tensor.getDim(0);
		for (int i = 1; i < arity; i++)
			assert tensor.getDim(i) == size;

		this.tensor = tensor;
		this.alg = alg;
	}

	public static <ELEM> Relation<ELEM> relEquals(final BoolAlgebra<ELEM> alg,
			int size) {
		Tensor<ELEM> tensor = Tensor.generate(size, size,
				new Func2<ELEM, Integer, Integer>() {
					@Override
					public ELEM call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() == elem2.intValue());
					}
				});
		return new Relation<ELEM>(alg, tensor);
	}

	public static <ELEM> Relation<ELEM> relNotEquals(
			final BoolAlgebra<ELEM> alg, int size) {
		Tensor<ELEM> tensor = Tensor.generate(size, size,
				new Func2<ELEM, Integer, Integer>() {
					@Override
					public ELEM call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() != elem2.intValue());
					}
				});
		return new Relation<ELEM>(alg, tensor);
	}

	public static <ELEM> Relation<ELEM> relLessThan(
			final BoolAlgebra<ELEM> alg, int size) {
		Tensor<ELEM> tensor = Tensor.generate(size, size,
				new Func2<ELEM, Integer, Integer>() {
					@Override
					public ELEM call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() < elem2.intValue());
					}
				});
		return new Relation<ELEM>(alg, tensor);
	}

	public static <ELEM> Relation<ELEM> relLessThanOrEquals(
			final BoolAlgebra<ELEM> alg, int size) {
		Tensor<ELEM> tensor = Tensor.generate(size, size,
				new Func2<ELEM, Integer, Integer>() {
					@Override
					public ELEM call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() <= elem2.intValue());
					}
				});
		return new Relation<ELEM>(alg, tensor);
	}

	private void checkSize(Relation<ELEM> rel) {
		assert alg == rel.alg && size == rel.size;
	}

	private void checkArity(Relation<ELEM> rel) {
		checkSize(rel);
		assert arity == rel.arity;
	}

	public Relation<ELEM> intersection(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.AND, tensor, rel.tensor);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> union(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.OR, tensor, rel.tensor);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> inverse() {
		int[] map = new int[arity];
		for (int i = 0; i < arity; i++)
			map[i] = arity - 1 - i;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> rotated() {
		int[] map = new int[arity];
		for (int i = 0; i < arity - 1; i++)
			map[i] = i + 1;
		map[arity - 1] = 0;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> composeHead(Relation<ELEM> rel) {
		checkSize(rel);
		assert arity + rel.arity >= 3;

		int[] shape = new int[arity + rel.arity - 1];
		for (int i = 0; i < shape.length; i++)
			shape[i] = size;

		int[] map = new int[arity];
		for (int i = 1; i < map.length; i++)
			map[i] = i;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, shape, map);

		map = new int[rel.arity];
		for (int i = 1; i < map.length; i++)
			map[i] = arity + i - 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(rel.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> compose(Relation<ELEM> rel) {
		return rotated().composeHead(rel);
	}

	public Relation<ELEM> diagonal() {
		int[] shape = new int[] { size };
		int[] map = new int[arity];

		Tensor<ELEM> tmp = Tensor.reshape(tensor, shape, map);
		return new Relation<ELEM>(alg, tmp);
	}

	public ELEM isFull() {
		Tensor<ELEM> tmp = Tensor.fold(alg.ALL, arity, tensor);
		return tmp.get();
	}

	public ELEM isEmpty() {
		Tensor<ELEM> tmp = Tensor.fold(alg.ANY, arity, tensor);
		return alg.not(tmp.get());
	}

	public ELEM isEqual(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.EQU, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, arity, tmp);
		return tmp.get();
	}

	public ELEM isSubset(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.LEQ, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, arity, tmp);
		return tmp.get();
	}

	public ELEM isFunction() {
		Tensor<ELEM> rel = Tensor.fold(alg.ONE, 1, tensor);
		return Tensor.fold(alg.ALL, rel.getOrder(), rel).get();
	}

	public ELEM isReflexive() {
		return diagonal().isFull();
	}

	public ELEM isSymmetric() {
		return isSubset(rotated());
	}

	public ELEM isTransitive() {
		assert arity == 2;
		// mask out diagonal to get fewer literals
		Relation<ELEM> rel = intersection(relNotEquals(alg, size));
		return rel.compose(rel).isSubset(this);
	}

	public ELEM isAntiSymmetric() {
		assert arity == 2;
		Relation<ELEM> rel = intersection(relNotEquals(alg, size));
		rel = rel.intersection(rel.inverse());
		return rel.isEmpty();
	}

	public ELEM isEquivalence() {
		ELEM b = isReflexive();
		b = alg.and(b, isSymmetric());
		return alg.and(b, isTransitive());
	}

	public ELEM isPartialOrder() {
		ELEM b = isReflexive();
		b = alg.and(b, isAntiSymmetric());
		return alg.and(b, isTransitive());
	}

	public static <ELEM> Relation<ELEM> lift(BoolAlgebra<ELEM> alg,
			Relation<Boolean> rel) {
		Tensor<ELEM> tensor = Tensor.map(alg.LIFT, rel.tensor);
		return new Relation<ELEM>(alg, tensor);
	}
}
