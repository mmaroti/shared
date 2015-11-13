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

public class RelationAlg<BOOL, REL extends Tensor<BOOL>> {
	protected final BoolAlgebra<BOOL> alg;

	public RelationAlg(BoolAlgebra<BOOL> alg) {
		this.alg = alg;
	}

	@SuppressWarnings("unchecked")
	private REL cast(Tensor<BOOL> tensor) {
		return (REL) tensor;
	}

	public REL toRelation(Tensor<BOOL> tensor) {
		int size = tensor.getDim(0);
		for (int i = 1; i < tensor.getOrder(); i++)
			assert size == tensor.getDim(i);
		assert tensor.getOrder() >= 1 && size >= 0;

		return cast(tensor);
	}

	public int getSize(REL rel) {
		return rel.getDim(0);
	}

	public int getArity(REL rel) {
		return rel.getOrder();
	}

	public REL intersect(REL rel1, REL rel2) {
		assert rel1.getOrder() == rel2.getOrder();
		return cast(Tensor.map2(alg.AND, rel1, rel2));
	}

	public REL union(REL rel1, REL rel2) {
		assert rel1.getOrder() == rel2.getOrder();
		return cast(Tensor.map2(alg.OR, rel1, rel2));
	}

	public REL diagonal(REL rel) {
		int[] shape = new int[] { getSize(rel) };
		int[] map = new int[getArity(rel)];

		return cast(Tensor.reshape(rel, shape, map));
	}

	public REL invert(REL rel) {
		int[] map = new int[getArity(rel)];
		for (int i = 0; i < map.length; i++)
			map[i] = map.length - 1 - i;

		return cast(Tensor.reshape(rel, rel.getShape(), map));
	}

	public REL rotate(REL rel) {
		int[] map = new int[getArity(rel)];
		for (int i = 0; i < map.length - 1; i++)
			map[i] = i + 1;
		map[map.length - 1] = 0;

		return cast(Tensor.reshape(rel, rel.getShape(), map));
	}

	public BOOL isFull(REL rel) {
		Tensor<BOOL> tmp = Tensor.fold(alg.ALL, rel.getOrder(), rel);
		return tmp.get();
	}

	public BOOL isEmpty(REL rel) {
		Tensor<BOOL> tmp = Tensor.fold(alg.ANY, rel.getOrder(), rel);
		return alg.not(tmp.get());
	}

	public BOOL isSubsetOf(REL rel1, REL rel2) {
		assert getArity(rel1) == getArity(rel2)
				&& getSize(rel1) == getSize(rel2);

		Tensor<BOOL> tmp = Tensor.map2(alg.LEQ, rel1, rel2);
		return Tensor.fold(alg.ALL, tmp.getOrder(), tmp).get();
	}

	public BOOL isFunction(REL rel) {
		Tensor<BOOL> tmp = Tensor.fold(alg.ONE, 1, rel);
		return Tensor.fold(alg.ALL, tmp.getOrder(), tmp).get();
	}

	public BOOL isReflexive(REL rel) {
		return isFull(diagonal(rel));
	}

	public BOOL isSymmetric(REL rel) {
		return isSubsetOf(rel, rotate(rel));
	}

	public BOOL isTransitive(REL rel) {
		assert getArity(rel) == 2;
		// mask out diagonal to get fewer literals
		REL fwd = intersect(rel, makeNotEqual(getSize(rel)));
		return isSubsetOf(compose(fwd, fwd), rel);
	}

	public BOOL isAntiSymmetric(REL rel) {
		assert getArity(rel) == 2;
		rel = intersect(rel, makeNotEqual(getSize(rel)));
		rel = intersect(rel, invert(rel));
		return isEmpty(rel);
	}

	public BOOL isEquivalence(REL rel) {
		BOOL b = isReflexive(rel);
		b = alg.and(b, isSymmetric(rel));
		return alg.and(b, isTransitive(rel));
	}

	public BOOL isPartialOrder(REL rel) {
		BOOL b = isReflexive(rel);
		b = alg.and(b, isAntiSymmetric(rel));
		return alg.and(b, isTransitive(rel));
	}
}
