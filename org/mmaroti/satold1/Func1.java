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

package org.mmaroti.satold1;

public abstract class Func1<ARG, RET> {
	public abstract RET apply1(ARG arg);

	public static Func1<Boolean, Boolean> BOOLEAN_NOT = new Func1<Boolean, Boolean>() {
		@Override
		public Boolean apply1(Boolean arg) {
			return !arg;
		}
	};

	public static Func1<Boolean, Integer> BOOLEAN_INT = new Func1<Boolean, Integer>() {
		@Override
		public Integer apply1(Boolean arg) {
			return arg ? 1 : 0;
		}
	};

	public static Func1<Integer, Integer> INTEGER_NEG = new Func1<Integer, Integer>() {
		@Override
		public Integer apply1(Integer arg) {
			return -arg;
		}
	};

	public static Func1<Boolean, BoolTerm> BOOLTERM_LIFT = new Func1<Boolean, BoolTerm>() {
		@Override
		public BoolTerm apply1(Boolean arg) {
			return BoolTerm.lift(arg);
		}
	};

	public static Func1<BoolTerm, BoolTerm> BOOLTERM_NOT = new Func1<BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply1(BoolTerm arg) {
			return arg.not();
		}
	};
}
