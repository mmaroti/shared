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

package org.mmaroti.sat.core;

import java.io.*;
import java.util.*;

public class Tensor<ELEM> implements Iterable<ELEM> {
	private final int[] shape;
	private final ELEM[] elems;

	public int[] getShape() {
		return shape;
	}

	public int getOrder() {
		return shape.length;
	}

	public int getDim(int index) {
		return shape[index];
	}

	private static class Iter<ELEM> implements Iterator<ELEM> {
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

	public ELEM get() {
		assert elems.length == 1;
		return elems[0];
	}

	@SuppressWarnings("unchecked")
	private Tensor(final int[] shape) {
		this.shape = shape;
		this.elems = (ELEM[]) new Object[getSize(shape)];
	}

	public static int getSize(int[] shape) {
		int size = 1;
		for (int i = 0; i < shape.length; i++) {
			assert 0 <= shape[i];
			size *= shape[i];
		}
		return size;
	}

	public static <ELEM> Tensor<ELEM> generate(final int[] shape,
			final Func0<ELEM> func) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);

		for (int i = 0; i < tensor.elems.length; i++)
			tensor.elems[i] = func.call();

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> generate(final int[] shape,
			final Func1<ELEM, int[]> func) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);

		if (tensor.elems.length > 0) {
			int[] index = new int[shape.length];
			int pos = 0;
			outer: for (;;) {
				tensor.elems[pos++] = func.call(index);
				for (int i = 0; i < index.length; i++) {
					if (++index[i] >= shape[i])
						index[i] = 0;
					else
						continue outer;
				}
				break;
			}
		}

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> generate(int dim,
			final Func1<ELEM, Integer> func) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(new int[] { dim });

		for (int i = 0; i < dim; i++)
			tensor.elems[i] = func.call(i);

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> generate(int dim1, int dim2,
			final Func2<ELEM, Integer, Integer> func) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(new int[] { dim1, dim2 });

		int pos = 0;
		for (int i = 0; i < dim1; i++)
			for (int j = 0; j < dim2; j++)
				tensor.elems[pos++] = func.call(j, i);

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> constant(final int[] shape,
			final ELEM elem) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);
		Arrays.fill(tensor.elems, elem);

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> scalar(final ELEM elem) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(new int[0]);
		tensor.elems[0] = elem;

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> vector(final List<ELEM> elems) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(new int[] { elems.size() });

		int pos = 0;
		for (ELEM elem : elems)
			tensor.elems[pos++] = elem;

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> matrix(final int[] shape,
			final List<ELEM> elems) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);
		assert tensor.elems.length == elems.size();

		int pos = 0;
		for (ELEM elem : elems)
			tensor.elems[pos++] = elem;

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> reshape(final Tensor<ELEM> arg,
			final int[] shape, final int[] map) {
		assert arg.getOrder() == map.length;
		final int[] index = new int[map.length];

		return Tensor.generate(shape, new Func1<ELEM, int[]>() {
			@Override
			public ELEM call(int[] elem) {
				for (int i = 0; i < index.length; i++)
					index[i] = elem[map[i]];
				return arg.getElem(index);
			}
		});
	}

	public static <ELEM, ELEM1> Tensor<ELEM> map(Func1<ELEM, ELEM1> func,
			Tensor<ELEM1> arg) {
		Tensor<ELEM> tensor = new Tensor<ELEM>(arg.shape);

		for (int i = 0; i < tensor.elems.length; i++)
			tensor.elems[i] = func.call(arg.elems[i]);

		return tensor;
	}

	public static <ELEM, ELEM1, ELEM2> Tensor<ELEM> map2(
			Func2<ELEM, ELEM1, ELEM2> func, Tensor<ELEM1> arg1,
			Tensor<ELEM2> arg2) {
		assert Arrays.equals(arg1.shape, arg2.shape);
		Tensor<ELEM> tensor = new Tensor<ELEM>(arg1.shape);

		for (int i = 0; i < tensor.elems.length; i++)
			tensor.elems[i] = func.call(arg1.elems[i], arg2.elems[i]);

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> concat(int[] commonShape,
			List<Tensor<ELEM>> list) {
		for (Tensor<ELEM> tensor : list)
			assert Arrays.equals(commonShape, tensor.shape);

		int count = list.size();
		int size = getSize(commonShape);

		int[] shape = new int[commonShape.length + 1];
		System.arraycopy(commonShape, 0, shape, 0, commonShape.length);
		shape[commonShape.length] = list.size();

		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);

		int pos = 0;
		for (int i = 0; i < count; i++) {
			System.arraycopy(list.get(i).elems, 0, tensor.elems, pos, size);
			pos += size;
		}

		return tensor;
	}

	public static <ELEM> Tensor<ELEM> concat(List<Tensor<ELEM>> list) {
		assert list.size() >= 1;
		return concat(list.get(0).shape, list);
	}

	public static <ELEM> List<Tensor<ELEM>> unconcat(Tensor<ELEM> tensor) {
		assert tensor.getOrder() >= 1;

		int[] shape = new int[tensor.getOrder() - 1];
		System.arraycopy(tensor.getShape(), 0, shape, 0, shape.length);
		int size = getSize(shape);
		int last = tensor.getDim(shape.length);

		List<Tensor<ELEM>> list = new ArrayList<Tensor<ELEM>>();
		for (int i = 0; i < last; i++) {
			Tensor<ELEM> t = new Tensor<ELEM>(shape);
			System.arraycopy(tensor.elems, i * size, t.elems, 0, size);
			list.add(t);
		}

		return list;
	}

	public static <ELEM1, ELEM2> Tensor<ELEM2> fold(
			Func1<ELEM2, Iterable<ELEM1>> func, int proj, Tensor<ELEM1> arg) {

		int[] shape1 = new int[proj];
		System.arraycopy(arg.shape, 0, shape1, 0, proj);
		Tensor<ELEM1> tensor1 = new Tensor<ELEM1>(shape1);

		int[] shape2 = new int[arg.getOrder() - proj];
		System.arraycopy(arg.shape, proj, shape2, 0, shape2.length);
		Tensor<ELEM2> tensor2 = new Tensor<ELEM2>(shape2);

		int pos = 0;
		for (int i = 0; i < tensor2.elems.length; i++) {
			System.arraycopy(arg.elems, pos, tensor1.elems, 0,
					tensor1.elems.length);

			tensor2.elems[i] = func.call(tensor1);
			pos += tensor1.elems.length;
		}

		return tensor2;
	}

	public static class Named<ELEM> {
		public final Tensor<ELEM> tensor;
		public final String names;

		public Named(Tensor<ELEM> tensor, String names) {
			assert tensor.getOrder() == names.length();

			this.tensor = tensor;
			this.names = names;
		}
	};

	public Named<ELEM> named(String names) {
		return new Named<ELEM>(this, names);
	}

	public static <ELEM, ELEM1, ELEM2> Tensor<ELEM> reduce(
			Func1<ELEM, Iterable<ELEM>> sum, String names,
			Func2<ELEM, ELEM1, ELEM2> prod, Named<ELEM1> part1,
			Named<ELEM2> part2) {

		TreeMap<Character, Integer> dims = new TreeMap<Character, Integer>();

		for (int i = 0; i < part1.names.length(); i++) {
			int dim = part1.tensor.shape[i];
			Integer d = dims.put(part1.names.charAt(i), dim);
			assert d == null || d.intValue() == dim;
		}

		for (int i = 0; i < part2.names.length(); i++) {
			int dim = part2.tensor.shape[i];
			Integer d = dims.put(part2.names.charAt(i), dim);
			assert d == null || d.intValue() == dim;
		}

		ArrayList<Character> keys = new ArrayList<Character>(dims.keySet());
		for (int i = 0; i < names.length(); i++) {
			Character c = names.charAt(i);
			keys.remove(c);
			keys.add(c);
		}

		int[] shape = new int[dims.size()];
		for (int i = 0; i < shape.length; i++)
			shape[i] = dims.get(keys.get(i));

		int[] map = new int[part1.names.length()];
		for (int i = 0; i < map.length; i++)
			map[i] = keys.indexOf(part1.names.charAt(i));
		Tensor<ELEM1> arg1 = Tensor.reshape(part1.tensor, shape, map);

		map = new int[part2.names.length()];
		for (int i = 0; i < map.length; i++)
			map[i] = keys.indexOf(part2.names.charAt(i));
		Tensor<ELEM2> arg2 = Tensor.reshape(part2.tensor, shape, map);

		Tensor<ELEM> tensor = Tensor.map2(prod, arg1, arg2);
		tensor = Tensor.fold(sum, keys.size() - names.length(), tensor);

		return tensor;
	};

	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("Tensor [");
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

	public static <ELEM> void print(Tensor<ELEM> tensor, PrintStream stream) {
		if (tensor == null)
			stream.println("Null");
		else
			stream.println(tensor);
	}

	public static <ELEM> void print(Map<String, Tensor<ELEM>> tensors,
			PrintStream stream) {
		if (tensors == null)
			stream.println("Null");
		else {
			if (!(tensors instanceof TreeMap))
				tensors = new TreeMap<String, Tensor<ELEM>>(tensors);

			for (String key : tensors.keySet()) {
				stream.print(key + " = ");
				stream.println(tensors.get(key));
			}
		}
	}

	public static <ELEM> void print(List<Map<String, Tensor<ELEM>>> list,
			PrintStream stream) {
		for (Map<String, Tensor<ELEM>> elem : list)
			print(elem, stream);
	}

	public static void main(String[] args) {
		Tensor<Integer> m1 = Tensor.matrix(new int[] { 2, 3 },
				Arrays.asList(1, 2, 3, 4, 5, 6));
		System.out.println(m1);

		Tensor<Integer> m2 = Tensor.matrix(new int[] { 3, 3 },
				Arrays.asList(0, 1, 0, 1, 0, 0, 0, 0, 1));
		System.out.println(m2);

		Tensor<Integer> m3 = Tensor.reduce(Func1.INT_SUM, "ac", Func2.INT_MUL,
				m1.named("ab"), m2.named("bc"));
		System.out.println(m3);

		m1 = Tensor.vector(Arrays.asList(1, 2, 3));
		System.out.println(m1);

		m2 = Tensor.vector(Arrays.asList(10, 20));
		System.out.println(m2);

		m3 = Tensor.reduce(Func1.INT_SUM, "ab", Func2.INT_MUL, m1.named("a"),
				m2.named("b"));
		System.out.println(m3);
	}
}
