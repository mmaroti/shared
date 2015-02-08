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

public class BoolSet extends Set<Integer> {
	private static final ArrayList<Integer> elems = new ArrayList<Integer>();

	static {
		elems.add(Instance.FALSE);
		elems.add(Instance.TRUE);
	}

	public static BoolSet INSTANCE = new BoolSet();

	@Override
	public ArrayList<Integer> elements() {
		return elems;
	}

	@Override
	public Integer generate(Instance instance) {
		return instance.newvar();
	}

	@Override
	public int eq(Instance instance, Integer arg1, Integer arg2) {
		return instance.eq(arg1, arg2);
	}

	@Override
	public String show(Integer elem) {
		if (elem == Instance.TRUE)
			return "1";
		else if (elem == Instance.FALSE)
			return "0";
		else
			throw new IllegalArgumentException();
	}

	@Override
	public int exclude(Instance instance, Integer elem, boolean[] solution) {
		return Instance.exclude(elem, solution);
	}

	@Override
	public Integer decode(Integer elem, boolean[] solution) {
		return Instance.decode(elem, solution);
	}

	@Override
	public int member(Instance instance, Integer arg) {
		return Instance.TRUE;
	}
}
