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

public class PowerSet<A, B> extends Set<ArrayList<A>> {
	public final Set<A> base;
	public final Collection<B> expElems;

	public PowerSet(Set<A> base, Set<B> exp) {
		this.base = base;
		this.expElems = exp.elements();
	}

	@Override
	public Collection<ArrayList<A>> elements() {
		ArrayList<ArrayList<A>> ret = new ArrayList<ArrayList<A>>();
		int expSize = expElems.size();

		if (expSize <= 0) {
			ret.add(new ArrayList<A>());
			return ret;
		}

		ArrayList<A> baseElems = new ArrayList<A>(base.elements());
		int baseSize = baseElems.size();

		if (baseSize <= 0)
			return ret;

		int[] indices = new int[expSize];
		for (;;) {
			int i;

			ArrayList<A> func = new ArrayList<A>(expSize);
			for (i = 0; i < expSize; i++)
				func.add(baseElems.get(indices[i]));

			ret.add(func);

			for (i = 0; i < expSize && (++indices[i] >= baseSize); i++)
				indices[i] = 0;

			if (i >= expSize)
				break;
		}

		return ret;
	}

	@Override
	public int eq(Instance instance, ArrayList<A> arg1, ArrayList<A> arg2) {
		assert arg1.size() == arg2.size();

		int t = Instance.TRUE;
		for (int i = 0; i < arg1.size(); i++)
			t = instance.and(t, base.eq(instance, arg1.get(i), arg2.get(i)));

		return t;
	}

	@Override
	public String show(ArrayList<A> elem) {
		String s = "[";
		for (int i = 0; i < elem.size(); i++) {
			if (i != 0)
				s += ",";
			s += base.show(elem.get(i));
		}
		s += "]";
		return s;
	}

	@Override
	public ArrayList<A> generate(Instance instance) {
		ArrayList<A> ret = new ArrayList<A>(expElems.size());

		for (int i = 0; i < expElems.size(); i++)
			ret.add(base.generate(instance));

		return ret;
	}

	@Override
	public ArrayList<A> decode(ArrayList<A> elem, boolean[] solution) {
		assert elem.size() == expElems.size();

		ArrayList<A> ret = new ArrayList<A>(elem.size());
		for (A a : elem)
			ret.add(base.decode(a, solution));

		return ret;
	}

	@Override
	public int exclude(Instance instance, ArrayList<A> elem, boolean[] solution) {
		assert elem.size() == expElems.size();

		int t = Instance.FALSE;
		for (int i = 0; i < elem.size(); i++)
			t = instance.or(t, base.exclude(instance, elem.get(i), solution));

		return t;
	}
}
