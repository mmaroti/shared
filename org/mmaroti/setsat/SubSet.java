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

public abstract class SubSet<A> extends Set<A> {
	Set<A> base;

	public SubSet(Set<A> sup) {
		this.base = sup;
	}

	public abstract int filter(Instance instance, A elem);

	@Override
	public ArrayList<A> elements() {
		Collection<A> as = base.elements();

		ArrayList<A> result = new ArrayList<A>();
		for (A a : as) {
			int t = filter(Instance.BOOL, a);
			if (t == Instance.TRUE)
				result.add(a);
			else
				assert t == Instance.FALSE;
		}

		return result;
	}

	@Override
	public A generate(Instance instance) {
		A a = base.generate(instance);

		int t = filter(instance, a);
		instance.ensure(t);

		return a;
	}

	@Override
	public String show(A elem) {
		return base.show(elem);
	}

	@Override
	public int eq(Instance instance, A arg1, A arg2) {
		return base.eq(instance, arg1, arg2);
	}

	@Override
	public A decode(A elem, boolean[] solution) {
		return base.decode(elem, solution);
	}

	@Override
	public int exclude(Instance instance, A elem, boolean[] solution) {
		return base.exclude(instance, elem, solution);
	}
}
