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

public final class ByteMin extends BinaryOp<Byte, byte[]> {
	private ByteMin() {
		super(ByteAlg.INSTANCE);
	}

	@Override
	public Byte slowApply(Byte elem1, Byte elem2) {
		byte a = elem1;
		byte b = elem2;
		return a <= b ? a : b;
	}

	@Override
	public void apply(byte[] src1, int src1Pos, byte[] src2, int src2Pos,
			byte[] dst, int dstPos, int count) {
		assert alg.checkRange(src1, src1Pos, count);
		assert alg.checkRange(src2, src2Pos, count);
		assert alg.checkRange(dst, dstPos, count);

		for (int i = 0; i < count; i++) {
			byte a = src1[src1Pos + i];
			byte b = src2[src2Pos + i];
			dst[dstPos + i] = a <= b ? a : b;
		}
	}

	@Override
	public void apply(byte[] src, int srcPos, byte[] dst, int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		for (int i = 0; i < count; i++) {
			byte a = dst[dstPos + i];
			byte b = src[srcPos + i];
			dst[dstPos + i] = a <= b ? a : b;
		}
	}

	@Override
	public void apply(byte[] src, int srcPos, Byte elem, byte[] dst,
			int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		byte b = elem;
		for (int i = 0; i < count; i++) {
			byte a = src[srcPos + i];
			dst[dstPos + i] = a <= b ? a : b;
		}
	}

	@Override
	public void apply(Byte elem, byte[] dst, int dstPos, int count) {
		assert alg.checkRange(dst, dstPos, count);

		byte b = elem;
		for (int i = 0; i < count; i++) {
			byte a = dst[dstPos + i];
			dst[dstPos + i] = a <= b ? a : b;
		}
	}

	public static final ByteMin INSTANCE = new ByteMin();
}
