/**
 *	Copyright (C) Miklos Maroti, 2016
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

package org.mmaroti.pratt;

public abstract class Expr {
	public static class Binary extends Expr {
		public final String name;
		public final Expr left;
		public final Expr right;

		public Binary(String name, Expr left, Expr right) {
			this.name = name;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "(" + left + " " + name + " " + right + ")";
		}
	}

	public static class IntLiteral extends Expr {
		public final int value;

		public IntLiteral(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}

	public static class Identifier extends Expr {
		public final String value;

		public Identifier(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
