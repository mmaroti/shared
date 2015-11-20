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

	public static <ELEM> Contract<ELEM> logical(BoolAlgebra<ELEM> alg) {
		return new Contract<ELEM>(alg.ANY, alg.AND);
	}

	public static <ELEM> Contract<ELEM> linear(BoolAlgebra<ELEM> alg) {
		return new Contract<ELEM>(alg.SUM, alg.AND);
	}

	private static class Entry<ELEM> {
		public final Tensor<ELEM> tensor;
		public final List<?> vars;

		public Entry(Tensor<ELEM> tensor, List<?> vars) {
			this.tensor = tensor;
			this.vars = vars;
		}
	}

	public static List<Integer> range(int start, int end) {
		assert end >= start;

		List<Integer> list = new ArrayList<Integer>();
		for (int i = start; i < end; i++)
			list.add(i);

		return list;
	}

	public void add(Tensor<ELEM> tensor, List<?> vars) {
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

	private static <ELEM> int[] fillShape(Entry<ELEM> entry, List<Object> vars,
			int[] shape) {
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
		int[] map = fillShape(entry, vars, shape);

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
		int[] map1 = fillShape(arg1, vars, shape);
		int[] map2 = fillShape(arg2, vars, shape);

		Tensor<ELEM> tensor1 = Tensor.reshape(arg1.tensor, shape, map1);
		Tensor<ELEM> tensor2 = Tensor.reshape(arg2.tensor, shape, map2);
		Tensor<ELEM> tensor = Tensor.map2(prod, tensor1, tensor2);

		return new Entry<ELEM>(tensor, vars);
	}

	private Entry<ELEM> fold(Entry<ELEM> entry) {
		List<Object> vars = new ArrayList<Object>();
		List<Object> rest = new ArrayList<Object>();

		outer: for (Object v : entry.vars) {
			for (Entry<ELEM> e : entries)
				if (e != entry && e.vars.contains(v)) {
					rest.add(v);
					continue outer;
				}
			vars.add(v);
		}

		if (vars.isEmpty())
			return entry;

		int count = vars.size();
		vars.addAll(rest);
		assert vars.size() == entry.vars.size();

		Tensor<ELEM> tensor;
		if (!vars.equals(entry.vars)) {
			int[] shape = new int[vars.size()];
			Arrays.fill(shape, -1);
			int[] map = fillShape(entry, vars, shape);
			tensor = Tensor.reshape(entry.tensor, shape, map);
		} else
			tensor = entry.tensor;

		tensor = Tensor.fold(sum, count, tensor);
		return new Entry<ELEM>(tensor, rest);
	}

	public Tensor<ELEM> get(List<?> vars) {
		if (entries.isEmpty())
			throw new IllegalStateException("no tensor added");

		int a = varOrder.size();
		for (Object v : vars) {
			if (!varOrder.remove(v))
				throw new IllegalArgumentException("unknown variable");

			varOrder.add(v);
		}
		if (a != varOrder.size())
			throw new IllegalArgumentException("repeated variable");

		entries.add(new Entry<ELEM>(null, vars));

		Entry<ELEM> entry = entries.removeFirst();
		entries.addFirst(fold(norm(entry)));
		while (entries.size() > 2) {
			entry = entries.removeFirst();
			entry = join(entry, norm(entries.removeFirst()));
			entries.addFirst(fold(entry));
		}

		entry = entries.removeFirst();
		assert entry.vars.equals(vars);
		assert entries.getFirst().tensor == null;

		entries.clear();
		varOrder.clear();
		return entry.tensor;
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

	public static void main(String[] args) {
		Tensor<Integer> a = Tensor.matrix(new int[] { 2, 2 },
				Arrays.asList(1, 2, 3, 4));
		Tensor.print(a);

		Contract<Integer> contract = new Contract<Integer>(Func1.INT_SUM,
				Func2.INT_MUL);
		contract.add(a, "ij");
		contract.add(a, "jk");
		Tensor.print(contract.get("ijk"));

		contract.add(a, "ij");
		contract.add(a, "jk");
		Tensor.print(contract.get("ik"));

		contract.add(a, "ij");
		contract.add(a, "jk");
		Tensor.print(contract.get("j"));
	}
}
