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

public class SatBuilder extends Calculator<Integer> {
	int variables;
	ArrayList<int[]> clauses = new ArrayList<int[]>();

	public void clear() {
		variables = 1;
		clauses.clear();
		clauses.add(new int[] { 1 });
	}

	public final Integer variable() {
		return ++variables;
	}

	private final Func1<Integer, int[]> GENERATOR = new Func1<Integer, int[]>() {
		@Override
		public Integer call(int[] elem) {
			return variable();
		}
	};

	public final Tensor<Integer> tensor(int... shape) {
		return Tensor.generate(shape, GENERATOR);
	}

	public void ensure(Integer literal) {
		clauses.add(new int[] { literal });
	}

	@Override
	public Integer not(Integer b) {
		return -b;
	}

	@Override
	public Integer or(Integer a, Integer b) {
		if (a == FALSE)
			return b;
		else if (a == TRUE)
			return TRUE;
		else if (b == FALSE)
			return a;
		else if (b == TRUE)
			return TRUE;

		int var = variable();
		clauses.add(new int[] { -a, var });
		clauses.add(new int[] { -b, var });
		clauses.add(new int[] { a, b, -var });
		return var;
	}

	@Override
	public Integer add(Integer a, Integer b) {
		if (a == TRUE)
			return -b;
		else if (a == FALSE)
			return b;
		else if (b == TRUE)
			return -a;
		else if (b == FALSE)
			return a;

		int var = variable();
		clauses.add(new int[] { a, b, -var });
		clauses.add(new int[] { a, -b, var });
		clauses.add(new int[] { -a, b, var });
		clauses.add(new int[] { -a, -b, -var });
		return var;
	}

	public SatBuilder() {
		super(1);
		clear();
	}

	public static void main(String[] args) {
		SatBuilder builder = new SatBuilder();

		Tensor<Integer> m1 = builder.tensor(2, 2);
		Integer t = Tensor.collapse(m1, 2, builder.SUM).get();
		builder.ensure(t);

		SatSolver solver = new MiniSat();
		boolean[] sol = solver.solve(builder.variables, builder.clauses);
		for (boolean b : sol) {
			System.out.println(b);
		}
	}
}
