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

import java.math.BigInteger;
import java.util.List;

public abstract class Tuple<ELEM> {
	public abstract int[] shape();

	public abstract ELEM get(int[] index);

	public int arity() {
		return shape().length;
	}

	public BigInteger size() {
		int[] s = shape();
		BigInteger a = BigInteger.ONE;
		for (int i = 0; i < s.length; i++)
			a = a.multiply(BigInteger.valueOf(s[i]));
		return a;
	}

	public String toString() {
		int[] shape = shape();
		if (shape.length == 0)
			return get(shape).toString();
		else if (shape.length == 1) {
			String s = "[";
			int[] index = new int[1];
			for (index[0] = 0; index[0] < shape[0]; index[0]++) {
				if (index[0] != 0)
					s += ",";
				s += get(index).toString();
			}
			s += "]";
			return s;
		} else
			return null;
	}

	public static <ELEM> Tuple<ELEM> scalar(final ELEM elem) {
		final int[] shape = new int[0];

		return new Tuple<ELEM>() {
			@Override
			public int[] shape() {
				return shape;
			}

			@Override
			public ELEM get(int[] index) {
				assert index.length == 0;
				return elem;
			}
		};
	}

	public static <ELEM> Tuple<ELEM> vector(final List<ELEM> elems) {
		final int[] shape = new int[] { elems.size() };

		return new Tuple<ELEM>() {
			@Override
			public int[] shape() {
				return shape;
			}

			@Override
			public ELEM get(int[] index) {
				assert index.length == 1;
				return elems.get(index[0]);
			}
		};
	}

	public static Tuple<Integer> range(final int start, int end) {
		if (end < start)
			throw new IllegalArgumentException();

		final int[] shape = new int[] { end - start };

		return new Tuple<Integer>() {
			@Override
			public int[] shape() {
				return shape;
			}

			@Override
			public Integer get(int[] index) {
				assert index.length == 1 && 0 <= index[0]
						&& index[0] < shape[0];

				return start + index[0];
			}
		};
	}

	public static abstract class Func<ARG, RET> {
		public abstract RET apply(ARG arg);
	}

	public static <ARG, RET> Tuple<RET> apply(final Tuple<Func<ARG, RET>> func,
			final Tuple<ARG> arg) {
		int[] fs = func.shape();
		int[] as = arg.shape();

		final int[] idx = new int[Math.min(fs.length, as.length)];

		for (int i = 0; i < idx.length; i++)
			if (fs[i] != as[i])
				throw new IllegalArgumentException();

		if (fs.length <= as.length) {
			return new Tuple<RET>() {
				@Override
				public int[] shape() {
					return arg.shape();
				}

				@Override
				public RET get(int[] index) {
					System.arraycopy(index, 0, idx, 0, idx.length);

					return func.get(idx).apply(arg.get(index));
				}
			};
		} else {
			return new Tuple<RET>() {
				@Override
				public int[] shape() {
					return func.shape();
				}

				@Override
				public RET get(int[] index) {
					System.arraycopy(index, 0, idx, 0, idx.length);

					return func.get(index).apply(arg.get(idx));
				}
			};
		}
	}

	public static abstract class Func2<ARG1, ARG2, RET> extends
			Func<ARG1, Func<ARG2, RET>> {

		public abstract RET apply2(ARG1 arg1, ARG2 arg2);

		@Override
		public Func<ARG2, RET> apply(final ARG1 arg1) {
			return new Func<ARG2, RET>() {
				@Override
				public RET apply(ARG2 arg2) {
					return apply2(arg1, arg2);
				}
			};
		}
	}

	public static Tuple<Func<Boolean, Boolean>> NOT = scalar((Func<Boolean, Boolean>) new Func<Boolean, Boolean>() {
		@Override
		public Boolean apply(Boolean arg) {
			return !arg;
		}
	});

	public static Tuple<Func<Boolean, Func<Boolean, Boolean>>> AND = scalar((Func<Boolean, Func<Boolean, Boolean>>) new Func2<Boolean, Boolean, Boolean>() {
		@Override
		public Boolean apply2(Boolean arg1, Boolean arg2) {
			return arg1 && arg2;
		}
	});

	public static Tuple<Func<Boolean, Func<Boolean, Boolean>>> OR = scalar((Func<Boolean, Func<Boolean, Boolean>>) new Func2<Boolean, Boolean, Boolean>() {
		@Override
		public Boolean apply2(Boolean arg1, Boolean arg2) {
			return arg1 || arg2;
		}
	});

	public static Tuple<Func<Integer, Integer>> NEG = scalar((Func<Integer, Integer>) new Func<Integer, Integer>() {
		@Override
		public Integer apply(Integer arg) {
			return -arg;
		}
	});

	public static Tuple<Func<Integer, Func<Integer, Integer>>> ADD = scalar((Func<Integer, Func<Integer, Integer>>) new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer apply2(Integer arg1, Integer arg2) {
			return arg1 + arg2;
		}
	});

	public static Tuple<Func<Integer, Func<Integer, Integer>>> MUL = scalar((Func<Integer, Func<Integer, Integer>>) new Func2<Integer, Integer, Integer>() {
		@Override
		public Integer apply2(Integer arg1, Integer arg2) {
			return arg1 * arg2;
		}
	});

	public static <ELEM> Tuple<Func<ELEM, Func<ELEM, Boolean>>> EQU() {
		return scalar((Func<ELEM, Func<ELEM, Boolean>>) new Func2<ELEM, ELEM, Boolean>() {
			@Override
			public Boolean apply2(ELEM arg1, ELEM arg2) {
				return arg1.equals(arg2);
			}
		});
	}

	public static void main(String[] args) {
		Tuple<Integer> a = apply(apply(MUL, range(0, 5)), range(0, 5));
		System.out.println(a);
	}
}
