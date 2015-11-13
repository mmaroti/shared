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

public class Relation<ELEM> extends AlgObject<ELEM> {
	protected final int size;

	public int getSize() {
		return size;
	}

	public final int getRelArity() {
		return tensor.getOrder();
	}

	public Relation(AlgObject<ELEM> object) {
		this(object.alg, object.tensor);
	}

	protected Relation(BoolAlgebra<ELEM> alg, Tensor<ELEM> tensor) {
		super(alg, tensor);
		assert 1 <= tensor.getOrder();

		size = tensor.getDim(0);
		for (int i = 1; i < tensor.getOrder(); i++)
			assert tensor.getDim(i) == size;
	}

	protected static int[] createShape(int size, int arity) {
		assert size > 1 && arity >= 0;

		int[] shape = new int[arity];
		for (int i = 0; i < arity; i++)
			shape[i] = size;
		return shape;
	}

	public static <ELEM> Relation<ELEM> makeEqual(final BoolAlgebra<ELEM> alg,
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

	public static <ELEM> Relation<ELEM> makeNotEqual(
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

	public static <ELEM> Relation<ELEM> makeLessThan(
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

	public static <ELEM> Relation<ELEM> makeLessThanOrEqual(
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

	protected void checkSize(Relation<ELEM> rel) {
		assert alg == rel.alg && size == rel.size;
	}

	protected void checkArity(Relation<ELEM> rel) {
		checkSize(rel);
		assert getRelArity() == rel.getRelArity();
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
		int[] map = new int[getRelArity()];
		for (int i = 0; i < map.length; i++)
			map[i] = map.length - 1 - i;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> rotated() {
		int[] map = new int[getRelArity()];
		for (int i = 0; i < map.length - 1; i++)
			map[i] = i + 1;
		map[map.length - 1] = 0;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> composeRelation(Relation<ELEM> rel) {
		return rotated().composeRelationHead(rel);
	}

	public Relation<ELEM> composeRelationHead(Relation<ELEM> rel) {
		checkSize(rel);
		assert getRelArity() + rel.getRelArity() >= 3;

		int[] shape = createShape(size, getRelArity() + rel.getRelArity() - 1);

		int[] map = new int[getRelArity()];
		for (int i = 1; i < map.length; i++)
			map[i] = i;

		Tensor<ELEM> tmp = Tensor.reshape(tensor, shape, map);

		map = new int[rel.getRelArity()];
		for (int i = 1; i < map.length; i++)
			map[i] = getRelArity() + i - 1;

		tmp = Tensor.map2(alg.AND, tmp, Tensor.reshape(rel.tensor, shape, map));
		tmp = Tensor.fold(alg.ANY, 1, tmp);

		return new Relation<ELEM>(alg, tmp);
	}

	public Relation<ELEM> diagonal() {
		int[] shape = new int[] { size };
		int[] map = new int[getRelArity()];

		Tensor<ELEM> tmp = Tensor.reshape(tensor, shape, map);
		return new Relation<ELEM>(alg, tmp);
	}

	public ELEM isFull() {
		Tensor<ELEM> tmp = Tensor.fold(alg.ALL, getRelArity(), tensor);
		return tmp.get();
	}

	public ELEM isEmpty() {
		Tensor<ELEM> tmp = Tensor.fold(alg.ANY, getRelArity(), tensor);
		return alg.not(tmp.get());
	}

	public ELEM isEqual(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.EQU, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, getRelArity(), tmp);
		return tmp.get();
	}

	public ELEM isSubset(Relation<ELEM> rel) {
		checkArity(rel);

		Tensor<ELEM> tmp = Tensor.map2(alg.LEQ, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, getRelArity(), tmp);
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
		assert tensor.getOrder() == 2;
		// mask out diagonal to get fewer literals
		Relation<ELEM> rel = intersection(makeNotEqual(alg, size));
		return rel.composeRelation(rel).isSubset(this);
	}

	public ELEM isAntiSymmetric() {
		assert tensor.getOrder() == 2;
		Relation<ELEM> rel = intersection(makeNotEqual(alg, size));
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
