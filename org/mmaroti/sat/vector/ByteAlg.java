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

public class ByteAlg extends VectorAlg<Byte, byte[]> {
	private ByteAlg() {
	}

	@Override
	public byte[] create(int size) {
		return new byte[size];
	}

	@Override
	public int getSize(byte[] vector) {
		return vector.length;
	}

	@Override
	public Byte slowGet(byte[] vector, int index) {
		assert 0 <= index && index < getSize(vector);
		return vector[index];
	}

	@Override
	public void slowSet(byte[] vector, int index, Byte elem) {
		assert 0 <= index && index < getSize(vector);
		vector[index] = elem;
	}

	public static final ByteAlg INSTANCE = new ByteAlg();
}
