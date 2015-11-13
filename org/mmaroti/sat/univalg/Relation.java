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

public class Relation<BOOL> {
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

	public Relation(BoolAlg<BOOL> alg, Tensor<BOOL> tensor) {
		arity = tensor.getOrder();
		assert 1 <= arity;

		size = tensor.getDim(0);
		for (int i = 1; i < arity; i++)
			assert tensor.getDim(i) == size;

		this.tensor = tensor;
		this.alg = alg;
	}

	public static <BOOL> Relation<BOOL> relEquals(final BoolAlg<BOOL> alg,
			int size) {
		Tensor<BOOL> tensor = Tensor.generate(size, size,
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() == elem2.intValue());
					}
				});
		return new Relation<BOOL>(alg, tensor);
	}

	public static <BOOL> Relation<BOOL> relNotEquals(final BoolAlg<BOOL> alg,
			int size) {
		Tensor<BOOL> tensor = Tensor.generate(size, size,
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() != elem2.intValue());
					}
				});
		return new Relation<BOOL>(alg, tensor);
	}

	public static <BOOL> Relation<BOOL> relLessThan(final BoolAlg<BOOL> alg,
			int size) {
		Tensor<BOOL> tensor = Tensor.generate(size, size,
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() < elem2.intValue());
					}
				});
		return new Relation<BOOL>(alg, tensor);
	}

	public static <BOOL> Relation<BOOL> relLessThanOrEquals(
			final BoolAlg<BOOL> alg, int size) {
		Tensor<BOOL> tensor = Tensor.generate(size, size,
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer elem1, Integer elem2) {
						return alg.lift(elem1.intValue() <= elem2.intValue());
					}
				});
		return new Relation<BOOL>(alg, tensor);
	}

	private void checkSize(Relation<BOOL> rel) {
		assert alg == rel.alg && size == rel.size;
	}

	private void checkArity(Relation<BOOL> rel) {
		checkSize(rel);
		assert arity == rel.arity;
	}

	public Relation<BOOL> intersection(Relation<BOOL> rel) {
		checkArity(rel);

		Tensor<BOOL> tmp = Tensor.map2(alg.AND, tensor, rel.tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> union(Relation<BOOL> rel) {
		checkArity(rel);

		Tensor<BOOL> tmp = Tensor.map2(alg.OR, tensor, rel.tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> inverse() {
		int[] map = new int[arity];
		for (int i = 0; i < arity; i++)
			map[i] = arity - 1 - i;

		Tensor<BOOL> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> rotated() {
		int[] map = new int[arity];
		for (int i = 0; i < arity - 1; i++)
			map[i] = i + 1;
		map[arity - 1] = 0;

		Tensor<BOOL> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> composeHead(Relation<BOOL> rel) {
		checkSize(rel);
		assert arity + rel.arity >= 3;

		int[] shape = new int[arity + rel.arity - 1];
		for (int i = 0; i < shape.length; i++)
			shape[i] = size;

		int[] map = new int[arity];
		for (int i = 1; i < map.length; i++)
			map[i] = i;

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, map);

		map = new int[rel.arity];
		for (int i = 1; i < map.length; i++)
			map[i] = arity + i - 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(rel.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> compose(Relation<BOOL> rel) {
		return rotated().composeHead(rel);
	}

	public Relation<BOOL> diagonal() {
		int[] shape = new int[] { size };
		int[] map = new int[arity];

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, map);
		return new Relation<BOOL>(alg, tmp);
	}

	public BOOL isFull() {
		Tensor<BOOL> tmp = Tensor.fold(alg.ALL, arity, tensor);
		return tmp.get();
	}

	public BOOL isEmpty() {
		Tensor<BOOL> tmp = Tensor.fold(alg.ANY, arity, tensor);
		return alg.not(tmp.get());
	}

	public BOOL isEqual(Relation<BOOL> rel) {
		checkArity(rel);

		Tensor<BOOL> tmp = Tensor.map2(alg.EQU, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, arity, tmp);
		return tmp.get();
	}

	public BOOL isSubset(Relation<BOOL> rel) {
		checkArity(rel);

		Tensor<BOOL> tmp = Tensor.map2(alg.LEQ, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, arity, tmp);
		return tmp.get();
	}

	public BOOL isFunction() {
		Tensor<BOOL> rel = Tensor.fold(alg.ONE, 1, tensor);
		return Tensor.fold(alg.ALL, rel.getOrder(), rel).get();
	}

	public BOOL isReflexive() {
		return diagonal().isFull();
	}

	public BOOL isSymmetric() {
		return isSubset(rotated());
	}

	public BOOL isTransitive() {
		assert arity == 2;
		// mask out diagonal to get fewer literals
		Relation<BOOL> rel = intersection(relNotEquals(alg, size));
		return rel.compose(rel).isSubset(this);
	}

	public BOOL isAntiSymmetric() {
		assert arity == 2;
		Relation<BOOL> rel = intersection(relNotEquals(alg, size));
		rel = rel.intersection(rel.inverse());
		return rel.isEmpty();
	}

	public BOOL isEquivalence() {
		BOOL b = isReflexive();
		b = alg.and(b, isSymmetric());
		return alg.and(b, isTransitive());
	}

	public BOOL isPartialOrder() {
		BOOL b = isReflexive();
		b = alg.and(b, isAntiSymmetric());
		return alg.and(b, isTransitive());
	}

	public static <BOOL> Relation<BOOL> lift(BoolAlg<BOOL> alg,
			Relation<Boolean> rel) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, rel.tensor);
		return new Relation<BOOL>(alg, tensor);
	}
}
