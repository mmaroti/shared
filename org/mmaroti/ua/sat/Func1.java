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

public abstract class Func1<ELEM, ELEM1> {
	public abstract ELEM call(ELEM1 elem);

	public <ELEM2> Func1<ELEM, ELEM2> combine(final Func1<ELEM1, ELEM2> fun) {
		final Func1<ELEM, ELEM1> me = this;
		return new Func1<ELEM, ELEM2>() {
			@Override
			public ELEM call(ELEM2 elem) {
				return me.call(fun.call(elem));
			}
		};
	}

	public <ELEM2, ELEM3> Func2<ELEM, ELEM2, ELEM3> combine(
			final Func2<ELEM1, ELEM2, ELEM3> fun) {
		final Func1<ELEM, ELEM1> me = this;
		return new Func2<ELEM, ELEM2, ELEM3>() {
			@Override
			public ELEM call(ELEM2 elem2, ELEM3 elem3) {
				return me.call(fun.call(elem2, elem3));
			}
		};
	}

	public static <ELEM, ELEM1> Func1<ELEM, ELEM1> constant(final ELEM elem) {
		return new Func1<ELEM, ELEM1>() {
			@Override
			public ELEM call(ELEM1 a) {
				return elem;
			}
		};
	}

	public static <ELEM> Func1<ELEM, Iterable<ELEM>> reducer(final ELEM unit,
			final Func2<ELEM, ELEM, ELEM> prod) {
		return new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				ELEM value = unit;
				for (ELEM elem : elems)
					value = prod.call(value, elem);
				return value;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public final static Func1 OBJ_ID = new Func1() {
		@Override
		public Object call(Object elem) {
			return elem;
		}
	};

	@SuppressWarnings("unchecked")
	public final static Func1<Integer, Integer> INT_ID = OBJ_ID;

	@SuppressWarnings("unchecked")
	public final static Func1<Boolean, Boolean> BOOL_ID = OBJ_ID;

	@SuppressWarnings("unchecked")
	public final static Func1<Double, Double> REAL_ID = OBJ_ID;

	public final static Func1<Integer, Integer> INT_NEG = new Func1<Integer, Integer>() {
		@Override
		public Integer call(Integer elem) {
			return -elem;
		}
	};

	public final static Func1<Boolean, Boolean> BOOL_NEG = new Func1<Boolean, Boolean>() {
		@Override
		public Boolean call(Boolean elem) {
			return !elem;
		}
	};

	public final static Func1<Double, Double> REAL_NEG = new Func1<Double, Double>() {
		@Override
		public Double call(Double elem) {
			return -elem;
		}
	};

	public final static Func1<Integer, Iterable<Integer>> INT_SUM = new Func1<Integer, Iterable<Integer>>() {
		@Override
		public Integer call(Iterable<Integer> elems) {
			int sum = 0;
			for (Integer elem : elems)
				sum += elem;
			return sum;
		}
	};

	public final static Func1<Integer, Iterable<Integer>> INT_PROD = new Func1<Integer, Iterable<Integer>>() {
		@Override
		public Integer call(Iterable<Integer> elems) {
			int prod = 1;
			for (Integer elem : elems)
				prod *= elem;
			return prod;
		}
	};

	public final static Func1<Double, Iterable<Double>> REAL_SUM = new Func1<Double, Iterable<Double>>() {
		@Override
		public Double call(Iterable<Double> elems) {
			double sum = 0;
			for (Double elem : elems)
				sum += elem;
			return sum;
		}
	};

	public final static Func1<Double, Iterable<Double>> REAL_PROD = new Func1<Double, Iterable<Double>>() {
		@Override
		public Double call(Iterable<Double> elems) {
			double prod = 1.0;
			for (Double elem : elems)
				prod *= elem;
			return prod;
		}
	};

	public final static Func1<Boolean, Iterable<Boolean>> BOOL_SUM = new Func1<Boolean, Iterable<Boolean>>() {
		@Override
		public Boolean call(Iterable<Boolean> elems) {
			boolean sum = false;
			for (Boolean elem : elems)
				sum ^= elem;
			return sum;
		}
	};

	public final static Func1<Boolean, Iterable<Boolean>> BOOL_ANY = new Func1<Boolean, Iterable<Boolean>>() {
		@Override
		public Boolean call(Iterable<Boolean> elems) {
			boolean any = false;
			for (Boolean elem : elems)
				any |= elem;
			return any;
		}
	};

	public final static Func1<Boolean, Iterable<Boolean>> BOOL_ALL = new Func1<Boolean, Iterable<Boolean>>() {
		@Override
		public Boolean call(Iterable<Boolean> elems) {
			boolean all = true;
			for (Boolean elem : elems)
				all &= elem;
			return all;
		}
	};

	public final static Func1<Boolean, Iterable<Boolean>> BOOL_ONE = new Func1<Boolean, Iterable<Boolean>>() {
		@Override
		public Boolean call(Iterable<Boolean> elems) {
			boolean one = false;
			for (Boolean elem : elems) {
				if (elem) {
					if (one)
						return false;

					one = true;
				}
			}
			return one;
		}
	};
}
