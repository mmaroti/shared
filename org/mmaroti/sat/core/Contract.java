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

	private LinkedList<Integer> variables = new LinkedList<Integer>();
	private LinkedList<Entry<ELEM>> entries = new LinkedList<Entry<ELEM>>();

	public Contract(Func1<ELEM, Iterable<ELEM>> sum,
			Func2<ELEM, ELEM, ELEM> prod) {
		this.sum = sum;
		this.prod = prod;
	}

	private static class Entry<ELEM> {
		public final Tensor<ELEM> tensor;
		public final List<Integer> variables;

		public Entry(Tensor<ELEM> tensor, List<Integer> variables) {
			this.tensor = tensor;
			this.variables = variables;
		}
	}

	public void add(Tensor<ELEM> tensor, int... vars) {
		List<Integer> list = new ArrayList<Integer>();
		for (int v : vars) {
			variables.remove(v);
			variables.add(v);
			list.add(v);
		}

		entries.add(new Entry<ELEM>(tensor, list));
	}

	private Entry<ELEM> normalize(Entry<ELEM> entry) {
		List<Integer> ordered = new ArrayList<Integer>(variables);
		ordered.retainAll(entry.variables);

		int[] shape = new int[ordered.size()];
		int[] map = new int[entry.variables.size()];

		for (int i = 0; i < map.length; i++) {
			int pos = ordered.indexOf(entry.variables.get(i));
			map[i] = pos;
			if (shape[pos] == 0)
				shape[pos] = entry.tensor.getDim(i);
			else
				assert shape[pos] == entry.tensor.getDim(i);
		}

		Tensor<ELEM> tensor = Tensor.reshape(entry.tensor, shape, map);
		return new Entry<ELEM>(tensor, ordered);
	}

	private void fold(Entry<ELEM> entry) {
		List<Integer> fold = new ArrayList<Integer>();
		List<Integer> rest = new ArrayList<Integer>();

		outer: for (Integer v : entry.variables) {
			for (Entry<ELEM> e : entries)
				if (e != entry && e.variables.contains(v)) {
					if (!rest.contains(v))
						rest.add(v);
					continue outer;
				}
			if (!fold.contains(v))
				fold.add(v);
		}

		if (fold.isEmpty())
			return;

		int c = fold.size();
		fold.addAll(rest);

	}

	public Tensor<ELEM> get(int... vars) {
		assert entries.size() >= 1;

		for (int v : vars) {
			variables.remove(v);
			variables.add(v);
		}

		ListIterator<Entry<ELEM>> iter = entries.listIterator();
		while (iter.hasNext())
			iter.set(normalize(iter.next()));

		for (;;) {
			fold(entries.get(0));
			if (entries.size() == 1)
				break;

			fold(entries.get(1));
		}

		return null;
	}
}
