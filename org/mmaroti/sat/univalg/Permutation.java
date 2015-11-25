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

public final class Permutation<BOOL> {
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

	public Permutation(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		assert tensor.getOrder() == 2;
		assert tensor.getDim(0) == tensor.getDim(1);

		this.alg = alg;
		this.tensor = tensor;

		if (alg == BoolAlgebra.INSTANCE)
			assert (Boolean) isPermutation();
	}

	public BOOL isPermutation() {
		Operation<BOOL> op = asOperation();
		return alg.and(op.isOperation(), op.isSurjective());
	}

	public Operation<BOOL> asOperation() {
		return new Operation<BOOL>(alg, tensor);
	}

	public Relation<BOOL> asRelation() {
		return new Relation<BOOL>(alg, tensor);
	}

	public Permutation<BOOL> invert() {
		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), new int[] { 1, 0 });
		return new Permutation<BOOL>(alg, tmp);
	}

	public BOOL isOdd() {
		Relation<BOOL> tmp1 = Relation.lift(alg,
				Relation.makeLessThan(getSize()));
		tmp1 = tmp1.compose(asRelation());

		Relation<BOOL> tmp2 = Relation.lift(alg,
				Relation.makeGreaterThan(getSize()));
		tmp2 = asRelation().compose(tmp2);

		return tmp1.intersect(tmp2).isOddCard();
	}

	public BOOL isEven() {
		return alg.not(isOdd());
	}
}
