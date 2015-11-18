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

import java.util.*;

public class Contract<ELEM> {
	private final Func1<ELEM, Iterable<ELEM>> sum;
	private final Func2<ELEM, ELEM, ELEM> prod;

	private LinkedList<Object> varOrder = new LinkedList<Object>();
	private LinkedList<Entry<ELEM>> entries = new LinkedList<Entry<ELEM>>();

	public Contract(Func1<ELEM, Iterable<ELEM>> sum,
			Func2<ELEM, ELEM, ELEM> prod) {
		this.sum = sum;
		this.prod = prod;
	}

	private static class Entry<ELEM> {
		public final Tensor<ELEM> tensor;
		public final List<Object> vars;

		public Entry(Tensor<ELEM> tensor, List<Object> vars) {
			this.tensor = tensor;
			this.vars = vars;
		}
	}

	public void add(Tensor<ELEM> tensor, List<Object> vars) {
		if (tensor.getOrder() != vars.size())
			throw new IllegalArgumentException("invalid tensor");

		for (Object v : vars) {
			varOrder.remove(v);
			varOrder.add(v);
		}

		entries.add(new Entry<ELEM>(tensor, vars));
	}

	public void add(Tensor<ELEM> tensor, int... vars) {
		List<Object> list = new ArrayList<Object>();
		for (int v : vars)
			list.add(new Integer(v));

		add(tensor, list);
	}

	public void add(Tensor<ELEM> tensor, String vars) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < vars.length(); i++)
			list.add(new Character(vars.charAt(i)));

		add(tensor, list);
	}

	private int[] getMap(Entry<ELEM> entry, List<Object> vars, int[] shape) {
		assert shape.length == vars.size();
		int[] map = new int[entry.vars.size()];

		int index = 0;
		for (Object v : entry.vars) {
			int pos = vars.indexOf(v);
			assert pos >= 0;

			int dim = entry.tensor.getDim(index);
			if (shape[pos] >= 0 && shape[pos] != dim)
				throw new IllegalStateException("variable dimension mismatch");

			map[index] = pos;
			shape[pos] = dim;
			index += 1;
		}

		return map;
	}

	private Entry<ELEM> norm(Entry<ELEM> entry) {
		List<Object> vars = new ArrayList<Object>(varOrder);
		vars.retainAll(entry.vars);

		if (vars.equals(entry.vars))
			return entry;

		int[] shape = new int[vars.size()];
		Arrays.fill(shape, -1);
		int[] map = getMap(entry, vars, shape);

		Tensor<ELEM> tensor = Tensor.reshape(entry.tensor, shape, map);
		return new Entry<ELEM>(tensor, vars);
	}

	private Entry<ELEM> join(Entry<ELEM> arg1, Entry<ELEM> arg2) {
		Set<Object> set = new HashSet<Object>();
		set.addAll(arg1.vars);
		set.addAll(arg2.vars);

		List<Object> vars = new LinkedList<Object>(varOrder);
		vars.retainAll(set);

		int[] shape = new int[vars.size()];
		Arrays.fill(shape, -1);
		int[] map1 = getMap(arg1, vars, shape);
		int[] map2 = getMap(arg2, vars, shape);

		Tensor<ELEM> tensor1 = Tensor.reshape(arg1.tensor, shape, map1);
		Tensor<ELEM> tensor2 = Tensor.reshape(arg2.tensor, shape, map2);
		Tensor<ELEM> tensor = Tensor.map2(prod, tensor1, tensor2);

		return new Entry<ELEM>(tensor, vars);
	}

	private Entry<ELEM> fold(Entry<ELEM> entry) {
		List<Object> fold = new ArrayList<Object>();
		List<Object> rest = new ArrayList<Object>();

		outer: for (Object v : entry.vars) {
			for (Entry<ELEM> e : entries)
				if (e != entry && e.vars.contains(v)) {
					rest.add(v);
					continue outer;
				}
			fold.add(v);
		}

		if (fold.isEmpty())
			return entry;

		int count = fold.size();
		fold.addAll(rest);
		assert fold.size() == entry.vars.size();

		Tensor<ELEM> tensor = entry.tensor;
		if (!fold.equals(entry.vars)) {
			int[] shape = new int[rest.size()];
			int[] map = new int[fold.size()];
		}

		tensor = Tensor.fold(sum, count, tensor);
		return new Entry<ELEM>(tensor, rest);
	}

	public Tensor<ELEM> get(List<Object> vars) {
		if (entries.isEmpty())
			throw new IllegalStateException("no tensor added");

		HashSet<Object> output = new HashSet<Object>(vars);
		if (output.size() != vars.size())
			throw new IllegalArgumentException("multiple output variables");
		else if (!varOrder.containsAll(output))
			throw new IllegalArgumentException("cannot request new variables");

		for (Object v : vars) {
			varOrder.remove(v);
			varOrder.add(v);
		}

		Entry<ELEM> entry = entries.removeFirst();
		entries.addFirst(fold(norm(entry)));
		while (entries.size() >= 2) {

		}

		return null;
	}

	public Tensor<ELEM> get(int... vars) {
		List<Object> list = new ArrayList<Object>();
		for (int v : vars)
			list.add(new Integer(v));

		return get(list);
	}

	public Tensor<ELEM> get(String vars) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < vars.length(); i++)
			list.add(new Character(vars.charAt(i)));

		return get(list);
	}
}
