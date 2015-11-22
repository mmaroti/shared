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

public final class PartialOrder<BOOL> {
	private final BoolAlgebra<BOOL> alg;
	private final Tensor<BOOL> tensor;

	public BoolAlgebra<BOOL> getAlg() {
		return alg;
	}

	public Tensor<BOOL> getTensor() {
		return tensor;
	}

	public int getSize() {
		return tensor.getDim(0);
	}

	public PartialOrder(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		assert tensor.getOrder() == 2;
		assert tensor.getDim(0) == tensor.getDim(1);

		this.alg = alg;
		this.tensor = tensor;

		if (alg == BoolAlgebra.INSTANCE)
			assert (Boolean) isPartialOrder();
	}

	public static <BOOL> PartialOrder<BOOL> chain(BoolAlgebra<BOOL> alg,
			int size) {
		return Relation.makeLessOrEqual(alg, size).asPartialOrder();
	}

	public static PartialOrder<Boolean> chain(int size) {
		return chain(BoolAlgebra.INSTANCE, size);
	}

	public static <BOOL> PartialOrder<BOOL> antiChain(BoolAlgebra<BOOL> alg,
			int size) {
		return Relation.makeEqual(alg, size).asPartialOrder();
	}

	public static PartialOrder<Boolean> antiChain(int size) {
		return antiChain(BoolAlgebra.INSTANCE, size);
	}

	public static PartialOrder<Boolean> powerset(int base) {
		assert 0 <= base && base <= 30;

		int size = 1 << base;
		Tensor<Boolean> tmp = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						int a = elem1, b = elem2;
						return (a & b) == a;
					}
				});

		return new PartialOrder<Boolean>(BoolAlgebra.INSTANCE, tmp);
	}

	public Relation<BOOL> asRelation() {
		return new Relation<BOOL>(alg, tensor);
	}

	public BOOL isPartialOrder() {
		return asRelation().isPartialOrder();
	}

	public PartialOrder<BOOL> invert() {
		return asRelation().revert().asPartialOrder();
	}

	public PartialOrder<BOOL> intersect(PartialOrder<BOOL> ord) {
		return asRelation().intersect(ord.asRelation()).asPartialOrder();
	}

	public Relation<BOOL> covers() {
		Relation<BOOL> tmp = Relation.makeNotEqual(alg, getSize());
		tmp = tmp.intersect(asRelation());
		return tmp.subtract(tmp.compose(tmp));
	}

	public PartialOrder<BOOL> product(PartialOrder<BOOL> ord) {
		return asRelation().product(ord.asRelation()).asPartialOrder();
	}

	public Relation<BOOL> downsetOf(Relation<BOOL> rel) {
		return asRelation().compose(rel);
	}

	public BOOL isDownset(Relation<BOOL> rel) {
		return rel.isSubsetOf(downsetOf(rel));
	}

	public Relation<BOOL> upsetOf(Relation<BOOL> rel) {
		return rel.compose(asRelation());
	}

	public BOOL isUpset(Relation<BOOL> rel) {
		return rel.isSubsetOf(upsetOf(rel));
	}

	public BOOL isAntiChain(Relation<BOOL> rel) {
		assert rel.getArity() == 1;

		Relation<BOOL> tmp = Relation.makeNotEqual(alg, getSize());
		tmp = tmp.intersect(asRelation());
		tmp = rel.compose(tmp).intersect(rel);
		return tmp.isEmpty();
	}

	public static <BOOL> PartialOrder<BOOL> lift(BoolAlgebra<BOOL> alg,
			PartialOrder<Boolean> rel) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, rel.tensor);
		return new PartialOrder<BOOL>(alg, tensor);
	}
}
