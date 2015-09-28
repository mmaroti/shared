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

import java.util.*;

public class MonInterval extends Problem {
	final int size;
	final Tensor<Integer> monoid;

	MonInterval(int size, int[] monoid) {
		super("f", new int[] { size, size, size });

		this.size = size;

		List<Integer> elems = new ArrayList<Integer>();
		for (int a : monoid)
			elems.add(a);

		this.monoid = Tensor.matrix(new int[] { size, monoid.length / size },
				elems);
	}

	@Override
	public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
			Map<String, Tensor<BOOL>> tensors) {
		DiscrMath<BOOL> discr = new DiscrMath<BOOL>(alg);

		Tensor<BOOL> f = tensors.get("f");
		BOOL oper = Tensor.fold(Tensor.fold(f, 1, alg.ONE), 2, alg.ALL).get();

		Tensor<BOOL> m = discr.graph(monoid, size);
		Tensor<BOOL> comp = Tensor.reduce(alg.ANY, "yxpq", alg.ALL,
				f.named("yuv"), m.named("uxp"), m.named("vxq"));

		BOOL closed = Tensor.reduce(alg.ANY, "", alg.EQ, comp.named("yxpq"),
				m.named("yxr")).get();

		return alg.and(oper, closed);
	}

	public static void main(String[] args) {
		MonInterval prob = new MonInterval(3, new int[] { 0, 1, 2, 0, 2, 1 });
		Tensor.print(prob.solveOne(new MiniSat()), System.out);
	}
}
