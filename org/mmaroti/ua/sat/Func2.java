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

package org.mmaroti.ua.sat;

public abstract class Func2<ELEM, ELEM1, ELEM2> {
	public abstract ELEM call(ELEM1 elem1, ELEM2 elem2);

	public <ELEM3> Func1<ELEM, ELEM3> combine(final Func1<ELEM1, ELEM3> fun1,
			final Func1<ELEM2, ELEM3> fun2) {
		final Func2<ELEM, ELEM1, ELEM2> me = this;
		return new Func1<ELEM, ELEM3>() {
			@Override
			public ELEM call(ELEM3 elem3) {
				return me.call(fun1.call(elem3), fun2.call(elem3));
			}
		};
	}

	public <ELEM3, ELEM4> Func2<ELEM, ELEM3, ELEM4> combine(
			final Func2<ELEM1, ELEM3, ELEM4> fun1,
			final Func2<ELEM2, ELEM3, ELEM4> fun2) {
		final Func2<ELEM, ELEM1, ELEM2> me = this;
		return new Func2<ELEM, ELEM3, ELEM4>() {
			@Override
			public ELEM call(ELEM3 elem3, ELEM4 elem4) {
				return me
						.call(fun1.call(elem3, elem4), fun2.call(elem3, elem4));
			}
		};
	}

	public static <ELEM, ELEM1, ELEM2> Func2<ELEM, ELEM1, ELEM2> constant(
			final ELEM elem) {
		return new Func2<ELEM, ELEM1, ELEM2>() {
			@Override
			public ELEM call(ELEM1 a1, ELEM2 a2) {
				return elem;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public final static Func2 OBJ_EQ = new Func2() {
		@Override
		public Boolean call(Object elem1, Object elem2) {
			assert elem1 != null && elem2 != null;
			return elem1.equals(elem2);
		}
	};

	@SuppressWarnings("unchecked")
	public final static Func2<Boolean, Integer, Integer> INT_EQ = OBJ_EQ;

	@SuppressWarnings("unchecked")
	public final static Func2<Boolean, Double, Double> REAL_EQ = OBJ_EQ;

	@SuppressWarnings("rawtypes")
	public final static Func2 OBJ_FST = new Func2() {
		@Override
		public Object call(Object elem1, Object elem2) {
			return elem1;
		}
	};

	@SuppressWarnings("unchecked")
	public final static Func2<Integer, Integer, Integer> INT_FST = OBJ_FST;

	@SuppressWarnings("unchecked")
	public final static Func2<Double, Double, Double> REAL_FST = OBJ_FST;

	@SuppressWarnings("rawtypes")
	public final static Func2 OBJ_SND = new Func2() {
		@Override
		public Object call(Object elem1, Object elem2) {
			return elem2;
		}
	};

	@SuppressWarnings("unchecked")
	public final static Func2<Integer, Integer, Integer> INT_SND = OBJ_SND;

	@SuppressWarnings("unchecked")
	public final static Func2<Double, Double, Double> REAL_SND = OBJ_SND;

	public final static Func2<Integer, Integer, Integer> INT_ADD = new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer call(Integer elem1, Integer elem2) {
			return elem1 + elem2;
		}
	};

	public final static Func2<Integer, Integer, Integer> INT_MUL = new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer call(Integer elem1, Integer elem2) {
			return elem1 * elem2;
		}
	};

	public final static Func2<Boolean, Integer, Integer> INT_LEQ = new Func2<Boolean, Integer, Integer>() {
		@Override
		public Boolean call(Integer elem1, Integer elem2) {
			return elem1.intValue() <= elem2.intValue();
		}
	};
	public final static Func2<Double, Double, Double> REAL_ADD = new Func2<Double, Double, Double>() {
		@Override
		public Double call(Double elem1, Double elem2) {
			return elem1 + elem2;
		}
	};

	public final static Func2<Double, Double, Double> REAL_MUL = new Func2<Double, Double, Double>() {
		@Override
		public Double call(Double elem1, Double elem2) {
			return elem1 * elem2;
		}
	};

	public final static Func2<Boolean, Double, Double> REAL_LEQ = new Func2<Boolean, Double, Double>() {
		@Override
		public Boolean call(Double elem1, Double elem2) {
			return elem1.intValue() <= elem2.intValue();
		}
	};
}
