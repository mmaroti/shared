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

public class BooleanAlg extends VectorAlg<Boolean, long[]> {
	private BooleanAlg() {
	}

	@Override
	public long[] create(int size) {
		assert (size & 63) == 0;
		return new long[size >>> 6];
	}

	@Override
	public int getSize(long[] vector) {
		return vector.length << 6;
	}

	@Override
	public Boolean slowGet(long[] vector, int index) {
		assert 0 <= index && index < getSize(vector);

		int idx = index >>> 6;
		long bit = 1L << index;
		return (vector[idx] & bit) != 0;
	}

	@Override
	public void slowSet(long[] vector, int index, Boolean elem) {
		assert 0 <= index && index < getSize(vector);

		int idx = index >>> 6;
		long bit = 1L << index;
		if (elem)
			vector[idx] |= bit;
		else
			vector[idx] &= ~bit;
	}

	public static final BooleanAlg INSTANCE = new BooleanAlg();

	public static long getBits(long[] vector, int pos, int count) {
		assert INSTANCE.checkRange(vector, pos, count) && count < 64;

		int wordIdx = pos >>> 6;
		int offset = pos & 63;

		long a = vector[wordIdx] >>> offset;
		long mask = (1L << count) - 1L;

		if (offset + count <= 64)
			return a & mask;
		else {
			long b = vector[wordIdx + 1] << -offset;
			return (a | b) & mask;
		}
	}

	public static void setBits(long[] vector, int pos, int count, long value) {
		assert INSTANCE.checkRange(vector, pos, count) && count < 64;

		int wordIdx = pos >>> 6;
		int offset = pos & 63;

		long mask = (1L << count) - 1L;
		assert (mask & value) == value;

		long a = vector[wordIdx];
		a &= ~(mask << offset);
		a |= value << offset;
		vector[wordIdx] = a;

		if (offset + count > 64) {
			a = vector[wordIdx + 1];
			a &= ~(mask >>> -offset);
			a |= value >>> -offset;
			vector[wordIdx + 1] = a;
		}
	}
}
