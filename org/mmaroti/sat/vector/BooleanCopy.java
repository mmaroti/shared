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

public final class BooleanCopy extends UnaryOp<Boolean, long[]> {
	private BooleanCopy() {
		super(BooleanAlg.INSTANCE);
	}

	@Override
	public Boolean slowApply(Boolean elem) {
		return elem;
	}

	// @Override
	public void apply(long[] src, int srcPos, long[] dst, int dstPos, int count) {
		assert alg.checkRange(src, srcPos, count);
		assert alg.checkRange(dst, dstPos, count);
		assert src != dst;

		int tmp = 64 - (dstPos & 63);
		if (tmp != 64 && count > tmp) {
			long a = BooleanAlg.getBits(src, srcPos, tmp);
			BooleanAlg.setBits(dst, dstPos, tmp, a);

			count -= tmp;
			srcPos += tmp;
			dstPos += tmp;
		}
		assert (dstPos & 63) == 0 || count < 64;

		if ((srcPos & 63) == 0) {
			System.arraycopy(src, srcPos >>> 6, dst, dstPos >>> 6, count >>> 6);
		} else {
			super.apply(src, srcPos, dst, dstPos, count & ~63);
		}

		srcPos += count & ~63;
		dstPos += count & ~63;
		count &= 63;

		if (count != 0) {
			long a = BooleanAlg.getBits(src, srcPos, count);
			BooleanAlg.setBits(dst, dstPos, count, a);
		}
	}

	public static final BooleanCopy INSTANCE = new BooleanCopy();
}
