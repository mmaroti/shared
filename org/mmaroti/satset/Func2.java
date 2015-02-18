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

package org.mmaroti.satset;

public abstract class Func2<ARG1, ARG2, RET> extends
		Func1<ARG1, Func1<ARG2, RET>> {

	public abstract RET apply2(ARG1 arg1, ARG2 arg2);

	@Override
	public Func1<ARG2, RET> apply1(final ARG1 arg1) {
		return new Func1<ARG2, RET>() {
			@Override
			public RET apply1(ARG2 arg2) {
				return apply2(arg1, arg2);
			}
		};
	}

	public static Func1<Boolean, Func1<Boolean, Boolean>> BOOLEAN_AND = new Func2<Boolean, Boolean, Boolean>() {
		@Override
		public Boolean apply2(Boolean arg1, Boolean arg2) {
			return arg1 && arg2;
		}
	};

	public static Func1<Boolean, Func1<Boolean, Boolean>> BOOLEAN_OR = new Func2<Boolean, Boolean, Boolean>() {
		@Override
		public Boolean apply2(Boolean arg1, Boolean arg2) {
			return arg1 || arg2;
		}
	};

	public static Func1<Integer, Func1<Integer, Integer>> INTEGER_ADD = new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer apply2(Integer arg1, Integer arg2) {
			return arg1 + arg2;
		}
	};

	public static Func1<Integer, Func1<Integer, Integer>> INTEGER_MUL = new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer apply2(Integer arg1, Integer arg2) {
			return arg1 * arg2;
		}
	};

	public static <ELEM> Func1<ELEM, Func1<ELEM, Boolean>> OBJECT_EQU() {
		return new Func2<ELEM, ELEM, Boolean>() {
			@Override
			public Boolean apply2(ELEM arg1, ELEM arg2) {
				return arg1.equals(arg2);
			}
		};
	}

	public static Func1<BoolTerm, Func1<BoolTerm, BoolTerm>> BOOLTERM_AND = new Func2<BoolTerm, BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply2(BoolTerm arg1, BoolTerm arg2) {
			return arg1.and(arg2);
		}
	};

	public static Func1<BoolTerm, Func1<BoolTerm, BoolTerm>> BOOLTERM_OR = new Func2<BoolTerm, BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply2(BoolTerm arg1, BoolTerm arg2) {
			return arg1.or(arg2);
		}
	};

	public static Func1<BoolTerm, Func1<BoolTerm, BoolTerm>> BOOLTERM_XOR = new Func2<BoolTerm, BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply2(BoolTerm arg1, BoolTerm arg2) {
			return arg1.xor(arg2);
		}
	};

	public static Func1<BoolTerm, Func1<BoolTerm, BoolTerm>> BOOLTERM_EQU = new Func2<BoolTerm, BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply2(BoolTerm arg1, BoolTerm arg2) {
			return arg1.equ(arg2);
		}
	};

	public static Func1<BoolTerm, Func1<BoolTerm, BoolTerm>> BOOLTERM_LEQ = new Func2<BoolTerm, BoolTerm, BoolTerm>() {
		@Override
		public BoolTerm apply2(BoolTerm arg1, BoolTerm arg2) {
			return arg1.leq(arg2);
		}
	};
}
