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

import java.io.PrintStream;
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

	public static <ELEM> Tensor<ELEM> stack(List<Tensor<ELEM>> args) {
		assert args.size() >= 1;
		for (int i = 0; i < args.size(); i++)
			assert Arrays.equals(args.get(0).shape, args.get(i).shape);

		int argSize = args.size();
		int subSize = getSize(args.get(0).shape);

		int[] shape = new int[1 + args.get(0).getOrder()];
		shape[0] = args.size();
		System.arraycopy(args.get(0).shape, 0, shape, 1, shape.length - 1);

		Tensor<ELEM> tensor = new Tensor<ELEM>(shape);

		int pos = 0;
		for (int i = 0; i < subSize; i++) {
			for (int j = 0; j < argSize; j++)
				tensor.elems[pos++] = args.get(j).elems[i];
		}

		return tensor;
	}

	public static <ELEM1, ELEM2> Tensor<ELEM2> fold(Tensor<ELEM1> arg,
			int proj, Func1<ELEM2, Iterable<ELEM1>> func) {

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

	@SafeVarargs
	public static <ELEM> Tensor<ELEM> reduce(Func1<ELEM, Iterable<ELEM>> prod,
			Func1<ELEM, Iterable<ELEM>> sum, String names, Named<ELEM>... parts) {
		assert parts.length > 0;

		TreeMap<Character, Integer> dims = new TreeMap<Character, Integer>();
		for (Named<ELEM> part : parts) {
			for (int i = 0; i < part.names.length(); i++) {
				int dim = part.tensor.shape[i];
				Integer d = dims.put(part.names.charAt(i), dim);

				assert d == null || d.intValue() == dim;
			}
		}

		ArrayList<Character> keys = new ArrayList<Character>(dims.keySet());
		for (int i = 0; i < names.length(); i++) {
			Character c = names.charAt(i);
			keys.remove(c);
			keys.add(c);
		}

		ArrayList<Tensor<ELEM>> tensors = new ArrayList<Tensor<ELEM>>();

		int[] shape = new int[dims.size()];
		for (int i = 0; i < shape.length; i++)
			shape[i] = dims.get(keys.get(i));

		for (Named<ELEM> part : parts) {
			int[] map = new int[part.names.length()];
			for (int i = 0; i < map.length; i++)
				map[i] = keys.indexOf(part.names.charAt(i));

			tensors.add(Tensor.reshape(part.tensor, shape, map));
		}

		Tensor<ELEM> tensor = Tensor.stack(tensors);
		tensor = Tensor.fold(tensor, 1, prod);
		tensor = Tensor.fold(tensor, keys.size() - names.length(), sum);

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

	public static <ELEM> void print(List<Tensor<ELEM>> tensors,
			PrintStream stream) {
		if (tensors == null)
			stream.println("Null");
		else
			for (int i = 0; i < tensors.size(); i++) {
				stream.print(i + " = ");
				stream.println(tensors.get(i));
			}
	}

	public static void main(String[] args) {
		Tensor<Integer> m1 = Tensor.matrix(new int[] { 2, 3 },
				Arrays.asList(1, 2, 3, 4, 5, 6));
		System.out.println(m1);

		Tensor<Integer> m2 = Tensor.matrix(new int[] { 3, 3 },
				Arrays.asList(0, 1, 0, 1, 0, 0, 0, 0, 1));
		System.out.println(m2);

		Tensor<Integer> m3 = Tensor.reduce(Func1.INT_PROD, Func1.INT_SUM, "ac",
				m1.named("ab"), m2.named("bc"));
		System.out.println(m3);

		m1 = Tensor.vector(Arrays.asList(1, 2, 3));
		System.out.println(m1);

		m2 = Tensor.vector(Arrays.asList(10, 20));
		System.out.println(m2);

		m3 = Tensor.reduce(Func1.INT_PROD, Func1.INT_SUM, "ab", m1.named("a"),
				m2.named("b"));
		System.out.println(m3);
	}
}
