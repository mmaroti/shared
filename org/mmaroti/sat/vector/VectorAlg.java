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

public abstract class VectorAlg<ELEM, VECTOR> {
	public abstract VECTOR create(int size);

	public abstract int getSize(VECTOR vector);

	public boolean checkRange(VECTOR vector, int pos, int count) {
		return 0 <= pos && pos + count <= getSize(vector) && count >= 0;
	}

	public abstract ELEM slowGet(VECTOR vector, int index);

	public abstract void slowSet(VECTOR vector, int index, ELEM elem);
}
