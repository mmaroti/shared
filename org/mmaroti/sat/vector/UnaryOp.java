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

package org.mmaroti.sat.vector;

public abstract class UnaryOp<ELEM, VECTOR> {
	public final VectorAlg<ELEM, VECTOR> alg;

	public UnaryOp(VectorAlg<ELEM, VECTOR> alg) {
		this.alg = alg;
	}

	public abstract ELEM slowApply(ELEM elem);

	public void apply(VECTOR src, int srcPos, VECTOR dst, int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		for (int i = 0; i < count; i++)
			alg.slowSet(dst, dstPos + i,
					slowApply(alg.slowGet(src, srcPos + i)));
	}
}
