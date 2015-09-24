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

import java.util.*;

public class GenArray<ELEM> {
	private final int[] shape;
	private final int[] steps;
	private final int offset;
	private final ELEM[] elems;

	public int[] getShape() {
		return shape;
	}

	public int getOrder() {
		return shape.length;
	}

	public ELEM get(int[] index) {
		assert index.length == steps.length;

		int pos = offset;
		for (int i = 0; i < steps.length; i++)
			pos += steps[i] * index[i];

		return elems[pos];
	}

	private GenArray(final int[] shape, final int[] steps, final int offset,
			final ELEM[] elems) {
		assert shape.length == steps.length;

		this.shape = shape;
		this.steps = steps;
		this.offset = offset;
		this.elems = elems;
	}

	public interface Gen<ELEM> {
		public ELEM elem(final int[] index);
	}

	public static <ELEM> GenArray<ELEM> generate(final int[] shape,
			final Gen<ELEM> gen) {
		int[] steps = new int[shape.length];

		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i];
			steps[i] = size;
			size *= shape[i];
		}

		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[size];

		int[] index = new int[shape.length];
		int pos = 0;
		while (pos < size) {
			elems[pos] = gen.elem(index);

			pos += 1;
			for (int i = 0; i < index.length && ++index[i] >= shape[i]; i++)
				index[i] = 0;
		}

		return new GenArray<ELEM>(shape, steps, 0, elems);
	}

	public static <ELEM> GenArray<ELEM> constant(final int[] shape,
			final ELEM elem) {
		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[1];
		elems[0] = elem;

		return new GenArray<ELEM>(shape, new int[shape.length], 0, elems);
	}

	public static <ELEM> GenArray<ELEM> vector(final Collection<ELEM> elems) {
		int size = elems.size();

		@SuppressWarnings("unchecked")
		ELEM[] es = (ELEM[]) new Object[size];

		int pos = 0;
		for (ELEM elem : elems)
			es[pos++] = elem;

		return new GenArray<ELEM>(new int[] { size }, new int[] { 1 }, 0, es);
	}

	public static <ELEM> GenArray<ELEM> vector(final ELEM[] elems) {
		return new GenArray<ELEM>(new int[] { elems.length }, new int[] { 1 },
				0, elems);
	}

	public static <ELEM> GenArray<ELEM> matrix(final int[] shape,
			final Collection<ELEM> elems) {
		int[] steps = new int[shape.length];

		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i];
			steps[i] = size;
			size *= shape[i];
		}
		assert size == elems.size();

		@SuppressWarnings("unchecked")
		ELEM[] es = (ELEM[]) new Object[size];

		int pos = 0;
		for (ELEM elem : elems)
			es[pos++] = elem;

		return new GenArray<ELEM>(shape, steps, 0, es);
	}

	public GenArray<ELEM> reshape(int[] shape2, int[] map) {
		assert shape.length == map.length;

		int[] steps2 = new int[shape2.length];

		for (int i = 0; i < map.length; i++) {
			assert 0 <= map[i] && map[i] < shape2.length;
			assert shape2[map[i]] == shape[i];

			steps2[map[i]] += steps[i];
		}

		return new GenArray<ELEM>(shape2, steps2, offset, elems);
	}

	public interface Fun<ELEM, ELEM1> {
		public ELEM call(ELEM1 elem);
	}

	public static <ELEM, ELEM1> GenArray<ELEM> map(Fun<ELEM, ELEM1> fun,
			GenArray<ELEM1> arg) {
		int[] shape = arg.shape;
		int[] steps = new int[shape.length];

		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i];

			if (arg.steps[i] != 0) {
				steps[i] = size;
				size *= shape[i];
			}
		}

		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[size];

		int[] index = new int[steps.length];
		int pos = 0;
		while (pos < size) {
			elems[pos] = fun.call(arg.get(index));

			pos += 1;
			for (int i = 0; i < index.length; i++) {
				if (steps[i] == 0)
					;
				else if (++index[i] >= shape[i])
					index[i] = 0;
				else
					break;
			}
		}

		return new GenArray<ELEM>(shape, steps, 0, elems);
	}

	public interface Fun2<ELEM, ELEM1, ELEM2> {
		public ELEM call(ELEM1 elem1, ELEM2 elem2);
	}

	public static <ELEM, ELEM1, ELEM2> GenArray<ELEM> map(
			Fun2<ELEM, ELEM1, ELEM2> fun, GenArray<ELEM1> arg1,
			GenArray<ELEM2> arg2) {
		assert arg1.shape.length == arg2.shape.length;

		int[] shape = arg1.shape;
		int[] steps = new int[shape.length];

		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i] && shape[i] == arg2.shape[i];

			if (arg1.steps[i] != 0 || arg2.steps[i] != 0) {
				steps[i] = size;
				size *= shape[i];
			}
		}

		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[size];

		int[] index = new int[steps.length];
		int pos = 0;
		while (pos < size) {
			elems[pos] = fun.call(arg1.get(index), arg2.get(index));

			pos += 1;
			for (int i = 0; i < index.length; i++) {
				if (steps[i] == 0)
					;
				else if (++index[i] >= shape[i])
					index[i] = 0;
				else
					break;
			}
		}

		return new GenArray<ELEM>(shape, steps, 0, elems);
	}

	public interface Monoid<ELEM> extends Fun2<ELEM, ELEM, ELEM> {
		public ELEM unit();
	}

	public static <ELEM> GenArray<ELEM> map(Monoid<ELEM> monoid,
			GenArray<ELEM>[] args) {
		return null;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("Array [");
		for (int i = 0; i < shape.length; i++) {
			if (i != 0)
				str.append(',');
			str.append(shape[i]);
		}
		str.append("] [");
		for (int i = 0; i < shape.length; i++) {
			if (i != 0)
				str.append(',');
			str.append(steps[i]);
		}
		str.append("] [");
		for (int i = 0; i < elems.length; i++) {
			if (i != 0)
				str.append(',');
			str.append(elems[i]);
		}
		str.append("]");

		return str.toString();
	}

	public final static Fun<Integer, Integer> INT_ID = new Fun<Integer, Integer>() {
		@Override
		public Integer call(Integer elem) {
			return elem;
		}
	};

	public final static Fun<Integer, Integer> INT_NEG = new Fun<Integer, Integer>() {
		@Override
		public Integer call(Integer elem) {
			return -elem;
		}
	};

	public final static Fun<Boolean, Boolean> BOOL_ID = new Fun<Boolean, Boolean>() {
		@Override
		public Boolean call(Boolean elem) {
			return elem;
		}
	};

	public final static Fun<Boolean, Boolean> BOOL_NEG = new Fun<Boolean, Boolean>() {
		@Override
		public Boolean call(Boolean elem) {
			return !elem;
		}
	};

	public final static Monoid<Integer> INT_ADD = new Monoid<Integer>() {
		@Override
		public Integer unit() {
			return 0;
		}

		@Override
		public Integer call(Integer elem1, Integer elem2) {
			return elem1 + elem2;
		}
	};

	public final static Monoid<Integer> INT_MUL = new Monoid<Integer>() {
		@Override
		public Integer unit() {
			return 1;
		}

		@Override
		public Integer call(Integer elem1, Integer elem2) {
			return elem1 * elem2;
		}
	};

	public final static Fun2<Boolean, Integer, Integer> INT_EQ = new Fun2<Boolean, Integer, Integer>() {
		@Override
		public Boolean call(Integer elem1, Integer elem2) {
			return elem1.intValue() == elem2.intValue();
		}
	};

	public final static Fun2<Boolean, Integer, Integer> INT_LEQ = new Fun2<Boolean, Integer, Integer>() {
		@Override
		public Boolean call(Integer elem1, Integer elem2) {
			return elem1.intValue() <= elem2.intValue();
		}
	};

	public final static Monoid<Boolean> BOOL_AND = new Monoid<Boolean>() {
		@Override
		public Boolean unit() {
			return Boolean.TRUE;
		}

		@Override
		public Boolean call(Boolean elem1, Boolean elem2) {
			return elem1 && elem2;
		}
	};

	public final static Monoid<Boolean> BOOL_OR = new Monoid<Boolean>() {
		@Override
		public Boolean unit() {
			return Boolean.FALSE;
		}

		@Override
		public Boolean call(Boolean elem1, Boolean elem2) {
			return elem1 || elem2;
		}
	};

	public final static Monoid<Boolean> BOOL_ADD = new Monoid<Boolean>() {
		@Override
		public Boolean unit() {
			return Boolean.FALSE;
		}

		@Override
		public Boolean call(Boolean elem1, Boolean elem2) {
			return elem1 ? !elem2 : elem2;
		}
	};

	public final static Fun2<Boolean, Boolean, Boolean> BOOL_IMP = new Fun2<Boolean, Boolean, Boolean>() {
		@Override
		public Boolean call(Boolean elem1, Boolean elem2) {
			return !elem1 || elem2;
		}
	};

	public static void main(String[] args) {
		GenArray<Integer> array = GenArray.generate(new int[] { 3, 3 },
				new Gen<Integer>() {
					@Override
					public Integer elem(int[] index) {
						return index[0] + index[1];
					}
				});
		System.out.println(array);

		array = array.reshape(new int[] { 2, 3, 2 }, new int[] { 1, 1 });
		System.out.println(array);

		// array = GenArray.map(INT_ID, array);
		// System.out.println(array);

		// array = array.reshape(new int[] { 2, 3 }, new int[] { 0, 1, 0 });
		// System.out.println(array);

		// array = GenArray.vector(new Integer[] { 1, 2, 3 });
		// System.out.println(array);

		array = GenArray.map(INT_ADD, array, array);
		System.out.println(array);
	}
}
