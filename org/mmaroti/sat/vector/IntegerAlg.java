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

public class IntegerAlg extends VectorAlg<Integer, int[]> {
	private IntegerAlg() {
	}

	@Override
	public int[] create(int size) {
		return new int[size];
	}

	@Override
	public int getSize(int[] vector) {
		return vector.length;
	}

	@Override
	public Integer slowGet(int[] vector, int index) {
		assert 0 <= index && index < getSize(vector);
		return vector[index];
	}

	@Override
	public void slowSet(int[] vector, int index, Integer elem) {
		assert 0 <= index && index < getSize(vector);
		vector[index] = elem;
	}

	public static final IntegerAlg INSTANCE = new IntegerAlg();
}
