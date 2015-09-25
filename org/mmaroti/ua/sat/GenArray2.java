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

public class GenArray2<ELEM> implements Iterable<ELEM> {
	private final int[] shape;
	private final ELEM[] elems;

	public int[] getShape() {
		return shape;
	}

	public int getOrder() {
		return shape.length;
	}

	public static class Iter<ELEM> implements Iterator<ELEM> {
		private final ELEM[] elems;
		private int index;

		Iter(ELEM[] array) {
			this.elems = array;
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < elems.length;
		}

		@Override
		public ELEM next() {
			return elems[index++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};

	@Override
	public Iterator<ELEM> iterator() {
		return new Iter<ELEM>(elems);
	}

	public ELEM getElem(int[] index) {
		assert index.length == shape.length;

		int pos = 0;
		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			pos += size * index[i];
			size *= shape[i];
		}

		return elems[pos];
	}

	@SuppressWarnings("unchecked")
	private GenArray2(final int[] shape) {
		this.shape = shape;
		this.elems = (ELEM[]) new Object[getSize(shape)];
	}

	private static int getSize(int[] shape) {
		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i];
			size *= shape[i];
		}
		return size;
	}

	public static <ELEM> GenArray2<ELEM> generate(final int[] shape,
			final Func<ELEM, int[]> func) {
		GenArray2<ELEM> array = new GenArray2<ELEM>(shape);

		int[] index = new int[shape.length];
		int pos = 0;
		outer: for (;;) {
			array.elems[pos++] = func.call(index);
			for (int i = 0; i < index.length; i++) {
				if (++index[i] >= shape[i])
					index[i] = 0;
				else
					continue outer;
			}
			break;
		}

		return array;
	}

	public static <ELEM> GenArray2<ELEM> constant(final int[] shape,
			final ELEM elem) {
		GenArray2<ELEM> array = new GenArray2<ELEM>(shape);
		Arrays.fill(array.elems, elem);

		return array;
	}

	public static <ELEM> GenArray2<ELEM> vector(final List<ELEM> elems) {
		GenArray2<ELEM> array = new GenArray2<ELEM>(new int[] { elems.size() });

		int pos = 0;
		for (ELEM elem : elems)
			array.elems[pos++] = elem;

		return array;
	}

	public static <ELEM> GenArray2<ELEM> matrix(final int[] shape,
			final List<ELEM> elems) {
		GenArray2<ELEM> array = new GenArray2<ELEM>(shape);
		assert array.elems.length == elems.size();

		int pos = 0;
		for (ELEM elem : elems)
			array.elems[pos++] = elem;

		return array;
	}

	public static <ELEM> GenArray2<ELEM> reshape(final GenArray2<ELEM> arg,
			final int[] shape, final int[] map) {
		assert arg.getOrder() == map.length;
		final int[] index = new int[map.length];

		return GenArray2.generate(shape, new Func<ELEM, int[]>() {
			@Override
			public ELEM call(int[] elem) {
				for (int i = 0; i < index.length; i++)
					index[i] = elem[map[i]];
				return arg.getElem(index);
			}
		});
	}

	public static <ELEM, ELEM1> GenArray2<ELEM> map(Func<ELEM, ELEM1> func,
			GenArray2<ELEM1> arg) {
		GenArray2<ELEM> array = new GenArray2<ELEM>(arg.shape);

		for (int i = 0; i < array.elems.length; i++)
			array.elems[i] = func.call(arg.elems[i]);

		return array;
	}

	public static <ELEM, ELEM1, ELEM2> GenArray2<ELEM> map2(
			Func2<ELEM, ELEM1, ELEM2> func, GenArray2<ELEM1> arg1,
			GenArray2<ELEM2> arg2) {
		assert Arrays.equals(arg1.shape, arg2.shape);
		GenArray2<ELEM> array = new GenArray2<ELEM>(arg1.shape);

		for (int i = 0; i < array.elems.length; i++)
			array.elems[i] = func.call(arg1.elems[i], arg2.elems[i]);

		return array;
	}

	public static <ELEM> GenArray2<ELEM> stack(List<GenArray2<ELEM>> args) {
		assert args.size() >= 1;
		for (int i = 0; i < args.size(); i++)
			assert Arrays.equals(args.get(0).shape, args.get(i).shape);

		int argSize = args.size();
		int subSize = getSize(args.get(0).shape);

		int[] shape = new int[1 + args.get(0).getOrder()];
		shape[0] = args.size();
		System.arraycopy(args.get(0).shape, 0, shape, 1, shape.length - 1);

		GenArray2<ELEM> array = new GenArray2<ELEM>(shape);

		int pos = 0;
		for (int i = 0; i < subSize; i++) {
			for (int j = 0; j < argSize; j++)
				array.elems[pos++] = args.get(j).elems[i];
		}

		return array;
	}

	public static <ELEM1, ELEM2> GenArray2<ELEM2> collapse(
			GenArray2<ELEM1> arg, int proj, Func<ELEM2, GenArray2<ELEM1>> func) {

		int[] shape1 = new int[proj];
		System.arraycopy(arg.shape, 0, shape1, 0, proj);
		GenArray2<ELEM1> array1 = new GenArray2<ELEM1>(shape1);

		int[] shape2 = new int[arg.getOrder() - proj];
		System.arraycopy(arg.shape, proj, shape2, 0, shape2.length);
		GenArray2<ELEM2> array2 = new GenArray2<ELEM2>(shape2);

		int pos = 0;
		for (int i = 0; i < array2.elems.length; i++) {
			System.arraycopy(arg.elems, pos, array1.elems, 0,
					array1.elems.length);

			array2.elems[i] = func.call(array1);
			pos += array1.elems.length;
		}

		return array2;
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
		for (int i = 0; i < elems.length; i++) {
			if (i != 0)
				str.append(',');
			str.append(elems[i]);
		}
		str.append("]");

		return str.toString();
	}

	public static void main(String[] args) {
		GenArray2<Integer> array = GenArray2.generate(new int[] { 3, 3 },
				new Func<Integer, int[]>() {
					@Override
					public Integer call(int[] index) {
						return index[0] + index[1];
					}
				});
		System.out.println(array);

		array = GenArray2.reshape(array, new int[] { 2, 3, 2 }, new int[] { 1,
				1 });
		System.out.println(array);

		array = GenArray2.map(Func.INT_ID, array);
		System.out.println(array);

		array = GenArray2.reshape(array, new int[] { 2, 3 }, new int[] { 0, 1,
				0 });
		System.out.println(array);

		// array = GenArray.vector(new Integer[] { 1, 2, 3 });
		// System.out.println(array);

		// array = GenArray.fold(Func2.INT_ADD, Arrays.asList(array, array,
		// array));
		// System.out.println(array);

		array = GenArray2.stack(Arrays.asList(array, array));
		System.out.println(array);

		array = GenArray2.reshape(array, new int[] { 3, 2 }, new int[] { 1, 1,
				0 });
		System.out.println(array);

		array = GenArray2.collapse(array, 1,
				new Func<Integer, GenArray2<Integer>>() {
					@Override
					public Integer call(GenArray2<Integer> elems) {
						int sum = 0;
						for (Integer elem : elems)
							sum += elem;
						return sum;
					}
				});
		System.out.println(array);
	}
}
