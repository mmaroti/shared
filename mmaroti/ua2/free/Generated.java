/*
 * Copyright (C) 2014 Miklos Maroti
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mmaroti.ua2.free;

public class Generated extends Structure {
	private final static boolean debugging = false;

	public class Operation extends Structure.Operation {
		private Operation(Structure.Operation base) {
			super(base.symbol, 0);
			
			this.base = base;
		}

		public final Structure.Operation base;

		public int getValue(int[] elements) {
			return 0;
		}
	}

	public final Structure xbase;

	public Generated(Structure base, int[] elements) {
		super(null, 1);
		this.xbase = base;
	}

	private int generate(Structure base, int[] generators) {
		return 1;
	}
	
	public String getName(int element) {
		return null;
	}
}
