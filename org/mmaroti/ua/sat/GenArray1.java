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

public class GenArray1<ELEM> {
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

	private GenArray1(final int[] shape, final int[] steps, final int offset,
			final ELEM[] elems) {
		assert shape.length == steps.length;

		this.shape = shape;
		this.steps = steps;
		this.offset = offset;
		this.elems = elems;
	}

	public static <ELEM> GenArray1<ELEM> generate(final int[] shape,
			final Func<ELEM, int[]> func) {
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
			elems[pos] = func.call(index);

			pos += 1;
			for (int i = 0; i < index.length && ++index[i] >= shape[i]; i++)
				index[i] = 0;
		}

		return new GenArray1<ELEM>(shape, steps, 0, elems);
	}

	public static <ELEM> GenArray1<ELEM> constant(final int[] shape,
			final ELEM elem) {
		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[1];
		elems[0] = elem;

		return new GenArray1<ELEM>(shape, new int[shape.length], 0, elems);
	}

	public static <ELEM> GenArray1<ELEM> vector(final List<ELEM> elems) {
		int size = elems.size();

		@SuppressWarnings("unchecked")
		ELEM[] es = (ELEM[]) new Object[size];

		int pos = 0;
		for (ELEM elem : elems)
			es[pos++] = elem;

		return new GenArray1<ELEM>(new int[] { size }, new int[] { 1 }, 0, es);
	}

	public static <ELEM> GenArray1<ELEM> vector(final ELEM[] elems) {
		return new GenArray1<ELEM>(new int[] { elems.length }, new int[] { 1 },
				0, elems);
	}

	public static <ELEM> GenArray1<ELEM> matrix(final int[] shape,
			final List<ELEM> elems) {
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

		return new GenArray1<ELEM>(shape, steps, 0, es);
	}

	public static <ELEM> GenArray1<ELEM> reshape(GenArray1<ELEM> arg,
			int[] shape, int[] map) {
		assert arg.shape.length == map.length;

		int[] steps = new int[shape.length];
		for (int i = 0; i < map.length; i++) {
			assert 0 <= map[i] && map[i] < shape.length;
			assert shape[map[i]] == arg.shape[i];

			steps[map[i]] += arg.steps[i];
		}

		return new GenArray1<ELEM>(shape, steps, arg.offset, arg.elems);
	}

	public static <ELEM, ELEM1> GenArray1<ELEM> map(Func<ELEM, ELEM1> fun,
			GenArray1<ELEM1> arg) {
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

		return new GenArray1<ELEM>(shape, steps, 0, elems);
	}

	public static <ELEM, ELEM1, ELEM2> GenArray1<ELEM> map2(
			Func2<ELEM, ELEM1, ELEM2> fun, GenArray1<ELEM1> arg1,
			GenArray1<ELEM2> arg2) {
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

		return new GenArray1<ELEM>(shape, steps, 0, elems);
	}

	public static <ELEM> GenArray1<ELEM> fold(Func2<ELEM, ELEM, ELEM> func,
			List<GenArray1<ELEM>> args) {
		assert args.size() >= 1;

		if (args.size() == 1)
			return args.get(0);

		int[] shape = args.get(0).shape;
		int[] steps = new int[shape.length];

		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			boolean nontriv = false;
			for (GenArray1<ELEM> arg : args) {
				assert arg.shape.length == shape.length;
				assert shape[i] == arg.shape[i];

				nontriv |= arg.steps[i] != 0;
			}

			if (nontriv) {
				steps[i] = size;
				size *= shape[i];
			}
		}

		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[size];

		int[] index = new int[steps.length];
		int pos = 0;
		while (pos < size) {
			ELEM elem = args.get(0).get(index);
			for (int j = 1; j < args.size(); j++)
				elem = func.call(elem, args.get(j).get(index));

			elems[pos] = elem;
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

		return new GenArray1<ELEM>(shape, steps, 0, elems);
	}

	public static <ELEM> GenArray1<ELEM> stack(List<GenArray1<ELEM>> args) {
		assert args.size() >= 1;

		int length = args.get(0).shape.length;
		int[] shape = new int[1 + length];
		int[] steps = new int[1 + length];

		shape[0] = args.size();
		for (int i = 0; i < length; i++)
			shape[1 + i] = args.get(0).shape[i];

		steps[0] = 1;
		int size = args.size();
		for (int i = 0; i < length; i++) {
			boolean nontriv = false;
			for (GenArray1<ELEM> arg : args) {
				assert arg.shape.length == length;
				assert arg.shape[i] == shape[1 + i];

				nontriv |= arg.steps[i] != 0;
			}

			if (nontriv) {
				steps[1 + i] = size;
				size *= shape[1 + i];
			}
		}

		@SuppressWarnings("unchecked")
		ELEM[] elems = (ELEM[]) new Object[size];

		int[] index = new int[length];
		int pos = 0;
		while (pos < size) {
			for (GenArray1<ELEM> arg : args)
				elems[pos++] = arg.get(index);

			for (int i = 0; i < length; i++) {
				if (steps[1 + i] == 0)
					;
				else if (++index[i] >= shape[1 + i])
					index[i] = 0;
				else
					break;
			}
		}

		return new GenArray1<ELEM>(shape, steps, 0, elems);

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

	public static void main(String[] args) {
		GenArray1<Integer> array = GenArray1.generate(new int[] { 3, 3 },
				new Func<Integer, int[]>() {
					@Override
					public Integer call(int[] index) {
						return index[0] + index[1];
					}
				});
		System.out.println(array);

		array = GenArray1.reshape(array, new int[] { 2, 3, 2 },
				new int[] { 1, 1 });
		System.out.println(array);

		array = GenArray1.map(Func.INT_ID, array);
		System.out.println(array);

		array = GenArray1.reshape(array, new int[] { 2, 3 },
				new int[] { 0, 1, 0 });
		System.out.println(array);

		// array = GenArray.vector(new Integer[] { 1, 2, 3 });
		// System.out.println(array);

		// array = GenArray.fold(Func2.INT_ADD, Arrays.asList(array, array,
		// array));
		// System.out.println(array);

		array = GenArray1.stack(Arrays.asList(array, array));
		System.out.println(array);
	}
}
