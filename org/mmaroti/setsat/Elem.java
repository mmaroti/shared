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

package org.mmaroti.setsat;

public abstract class Elem<BOOL> {
	public static class Atom<BOOL> extends Elem<BOOL> {
		public final BOOL bool;

		public Atom(BOOL bool) {
			this.bool = bool;
		}
	}

	public static class Pair<BOOL, FIRST extends Elem<BOOL>, SECOND extends Elem<BOOL>>
			extends Elem<BOOL> {
		public final FIRST first;
		public final SECOND second;

		public Pair(FIRST first, SECOND second) {
			this.first = first;
			this.second = second;
		}
	}

	public static class Vector<BOOL, ELEM extends Elem<BOOL>> extends
			Elem<BOOL> {
		public final int size;
		public final ELEM elem;

		public Vector(int size, ELEM elem) {
			this.size = size;
			this.elem = elem;
		}
	}
}
