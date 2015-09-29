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

package org.mmaroti.satold2;

import java.util.*;

public class ProductSet<A, B> extends Set<Pair<A, B>> {
	public final Set<A> seta;
	public final Set<B> setb;

	public ProductSet(Set<A> seta, Set<B> setb) {
		this.seta = seta;
		this.setb = setb;
	}

	@Override
	public ArrayList<Pair<A, B>> elements() {
		Collection<A> as = seta.elements();
		Collection<B> bs = setb.elements();

		ArrayList<Pair<A, B>> result = new ArrayList<Pair<A, B>>();
		for (A a : as)
			for (B b : bs)
				result.add(new Pair<A, B>(a, b));

		return result;
	}

	@Override
	public Pair<A, B> generate(Instance instance) {
		A a = seta.generate(instance);
		B b = setb.generate(instance);
		return new Pair<A, B>(a, b);
	}

	@Override
	public int eq(Instance instance, Pair<A, B> arg1, Pair<A, B> arg2) {
		return instance.and(seta.eq(instance, arg1.a, arg2.a),
				setb.eq(instance, arg1.b, arg2.b));
	}

	@Override
	public String show(Pair<A, B> elem) {
		return "(" + seta.show(elem.a) + "," + setb.show(elem.b) + ")";
	}

	@Override
	public int exclude(Instance instance, Pair<A, B> elem, boolean[] solution) {
		return instance.or(seta.exclude(instance, elem.a, solution),
				setb.exclude(instance, elem.b, solution));
	}

	@Override
	public Pair<A, B> decode(Pair<A, B> elem, boolean[] solution) {
		A a = seta.decode(elem.a, solution);
		B b = setb.decode(elem.b, solution);
		return new Pair<A, B>(a, b);
	}

	@Override
	public int member(Instance instance, Pair<A, B> arg) {
		return instance.and(seta.member(instance, arg.a),
				setb.member(instance, arg.b));
	}
}
