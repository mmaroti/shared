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

public final class IntegerMin extends BinaryOp<Integer, int[]> {
	private IntegerMin() {
		super(IntegerAlg.INSTANCE);
	}

	@Override
	public Integer slowApply(Integer elem1, Integer elem2) {
		return Math.min(elem1, elem2);
	}

	@Override
	public void apply(int[] src1, int src1Pos, int[] src2, int src2Pos,
			int[] dst, int dstPos, int count) {
		assert alg.checkRange(src1, src1Pos, count);
		assert alg.checkRange(src2, src2Pos, count);
		assert alg.checkRange(dst, dstPos, count);

		for (int i = 0; i < count; i++)
			dst[dstPos + i] = Math.min(src1[src1Pos + i], src2[src2Pos + i]);
	}

	@Override
	public void apply(int[] src, int srcPos, int[] dst, int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		for (int i = 0; i < count; i++)
			dst[dstPos + i] = Math.min(dst[dstPos + i], src[srcPos + i]);
	}

	@Override
	public void apply(int[] src, int srcPos, Integer elem, int[] dst,
			int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		int b = elem;
		for (int i = 0; i < count; i++)
			dst[dstPos + i] = Math.min(src[srcPos + i], b);
	}

	@Override
	public void apply(Integer elem, int[] dst, int dstPos, int count) {
		assert alg.checkRange(dst, dstPos, count);

		int b = elem;
		for (int i = 0; i < count; i++)
			dst[dstPos + i] = Math.min(dst[dstPos + i], b);
	}
	
	public static final IntegerMin INSTANCE = new IntegerMin();
}
