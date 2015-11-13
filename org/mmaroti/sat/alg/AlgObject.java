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

public class AlgObject<BOOL> {
	protected final BoolAlgebra<BOOL> alg;
	protected final Tensor<BOOL> tensor;

	public BoolAlgebra<BOOL> getBoolAlg() {
		return alg;
	}

	public Tensor<BOOL> getTensor() {
		return tensor;
	}

	public int getLastDim() {
		return tensor.getDim(tensor.getOrder() - 1);
	}

	public AlgObject(AlgObject<BOOL> object) {
		this.alg = object.alg;
		this.tensor = object.tensor;
	}

	protected AlgObject(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		this.alg = alg;
		this.tensor = tensor;
	}
}
