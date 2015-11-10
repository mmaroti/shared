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

public abstract class BinaryOp<ELEM, VECTOR> {
	public final VectorAlg<ELEM, VECTOR> alg;

	public BinaryOp(VectorAlg<ELEM, VECTOR> alg) {
		this.alg = alg;
	}

	public abstract ELEM slowApply(ELEM elem1, ELEM elem2);

	public void apply(VECTOR src1, int src1Pos, VECTOR src2, int src2Pos,
			VECTOR dst, int dstPos, int count) {
		assert alg.checkRange(src1, src1Pos, count);
		assert alg.checkRange(src2, src2Pos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src1 != dst && src2 != dst;

		for (int i = 0; i < count; i++) {
			ELEM elem1 = alg.slowGet(src1, src1Pos + i);
			ELEM elem2 = alg.slowGet(src2, src2Pos + i);
			alg.slowSet(dst, dstPos + i, slowApply(elem1, elem2));
		}
	}

	public void apply(VECTOR src, int srcPos, VECTOR dst, int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		for (int i = 0; i < count; i++) {
			ELEM a = alg.slowGet(dst, dstPos + i);
			ELEM b = alg.slowGet(src, srcPos + i);
			alg.slowSet(dst, dstPos + i, slowApply(a, b));
		}
	}

	public void apply(VECTOR src, int srcPos, ELEM elem, VECTOR dst,
			int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		for (int i = 0; i < count; i++) {
			ELEM a = alg.slowGet(src, srcPos + i);
			alg.slowSet(dst, dstPos + i, slowApply(a, elem));
		}
	}

	public void apply(ELEM elem, VECTOR dst, int dstPos, int count) {
		assert alg.checkRange(dst, dstPos, count);

		for (int i = 0; i < count; i++) {
			ELEM a = alg.slowGet(dst, dstPos + i);
			alg.slowSet(dst, dstPos + i, slowApply(a, elem));
		}
	}
}
