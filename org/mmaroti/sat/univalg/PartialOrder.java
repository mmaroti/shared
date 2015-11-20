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

	public PartialOrder(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		assert tensor.getOrder() == 2;
		assert tensor.getDim(0) == tensor.getDim(1);

		this.alg = alg;
		this.tensor = tensor;

		if (getAlg() == BoolAlgebra.INSTANCE)
			assert (Boolean) isPartialOrder();
	}

	public Relation<BOOL> asRelation() {
		return new Relation<BOOL>(alg, tensor);
	}

	public BOOL isPartialOrder() {
		return asRelation().isPartialOrder();
	}

	public PartialOrder<BOOL> invert() {
		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), new int[] { 1, 0 });
		return new PartialOrder<BOOL>(alg, tmp);
	}

	private void checkSize(PartialOrder<BOOL> ord) {
		assert getAlg() == ord.getAlg();
		assert getSize() == ord.getSize();
	}

	public PartialOrder<BOOL> intersect(PartialOrder<BOOL> ord) {
		checkSize(ord);
		Tensor<BOOL> tmp = Tensor.map2(alg.AND, tensor, ord.tensor);
		return new PartialOrder<BOOL>(alg, tmp);
	}

	public Relation<BOOL> covers() {
		Relation<BOOL> tmp = Relation.makeNotEqual(alg, getSize());
		tmp = tmp.intersect(asRelation());
		return tmp.subtract(tmp.compose(tmp));
	}
}
