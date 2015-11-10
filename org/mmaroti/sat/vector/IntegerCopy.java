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

public final class IntegerCopy extends UnaryOp<Integer, int[]> {
	private IntegerCopy() {
		super(IntegerAlg.INSTANCE);
	}

	@Override
	public Integer slowApply(Integer elem) {
		return elem;
	}

	@Override
	public void apply(int[] src, int srcPos, int[] dst, int dstPos, int count) {
		System.arraycopy(src, srcPos, dst, dstPos, count);
	}

	public static final IntegerCopy INSTANCE = new IntegerCopy();
}
