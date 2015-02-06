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

import java.util.*;

public class SmallSet extends Set<int[]> {
	public final int size;

	public SmallSet(int size) {
		if (size <= 0)
			throw new IllegalArgumentException();

		this.size = size;

		for (int i = 0; i < size; i++) {
			int[] elem = new int[size - 1];

			for (int j = 0; j < i; j++)
				elem[j] = Instance.TRUE;

			for (int j = i; j < elem.length; ++j)
				elem[j] = Instance.FALSE;

			elems.add(elem);
		}
	}

	private ArrayList<int[]> elems = new ArrayList<int[]>();

	@Override
	public Collection<int[]> elements() {
		return elems;
	}

	@Override
	public int[] generate(Instance instance) {
		int[] vars = new int[size - 1];

		for (int i = 0; i < size - 1; i++)
			vars[i] = instance.newvar();

		for (int i = 0; i < size - 2; i++)
			instance.ensure(instance.leq(vars[i + 1], vars[i]));

		return vars;
	}

	@Override
	public int eq(Instance instance, int[] arg1, int[] arg2) {
		assert arg1.length == arg2.length;

		int t = Instance.TRUE;
		for (int i = 0; i < arg1.length; i++)
			t = instance.and(t, instance.eq(arg1[i], arg2[i]));

		return t;
	}

	public int[] constant(int a) {
		return elems.get(a);
	}

	public int[] bitCount(Instance instance, ArrayList<Integer> bits) {
		int[] count = new int[size - 1];

		for (int i = 0; i < count.length; i++)
			count[i] = Instance.FALSE;

		for (int b : bits) {
			for (int i = 0; i < count.length; i++) {
				int c = count[i];
				count[i] = instance.or(c, b);
				b = instance.and(c, b);
			}
		}

		return count;
	}

	@Override
	public String show(int[] elem) {
		assert elem.length == size - 1;

		for (int i = 0; i < elem.length; i++) {
			assert elem[i] == Instance.TRUE || elem[i] == Instance.FALSE;
			assert i == 0 || elem[i - 1] == Instance.TRUE
					|| elem[i] == Instance.FALSE;
		}

		int a = 0;
		while (a < elem.length && elem[a] == Instance.TRUE)
			a += 1;

		return Integer.toString(a);
	}

	@Override
	public int[] decode(int[] elem, boolean[] solution) {
		assert elem.length == size - 1;

		int[] ret = new int[elem.length];
		for (int i = 0; i < elem.length; i++)
			ret[i] = Instance.decode(elem[i], solution);

		return ret;
	}

	@Override
	public int exclude(Instance instance, int[] elem, boolean[] solution) {
		assert elem.length == size - 1;

		int t = Instance.FALSE;
		for (int i = 0; i < elem.length; i++)
			t = instance.or(t, Instance.exclude(elem[i], solution));

		return t;
	}
}
