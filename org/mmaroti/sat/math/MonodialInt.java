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

package org.mmaroti.sat.math;

import java.util.*;

import org.mmaroti.sat.core.*;
import org.mmaroti.sat.solvers.*;

public abstract class MonodialInt extends Problem {
	final int size;
	final Tensor<Boolean> monoid;

	public <BOOL> BOOL isFunction(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		func = Tensor.fold(func, 1, alg.ONE);
		func = Tensor.fold(func, func.getOrder(), alg.ALL);
		return func.get();
	}

	public <BOOL> BOOL inStabilizer2(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 3);
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xytp", alg.AND,
				func.named("xyz"), monoid.named("ztp"));
		t = Tensor.reduce(alg.ANY, "xtpq", alg.AND, t.named("xytp"),
				monoid.named("ytq"));
		t = Tensor.reduce(alg.ALL, "rpq", alg.EQ, t.named("xtpq"),
				monoid.named("xtr"));
		t = Tensor.fold(Tensor.fold(t, 1, alg.ONE), 2, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL inStabilizer3(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 4);
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "atpcd", alg.AND,
				func.named("abcd"), monoid.named("btp"));
		t = Tensor.reduce(alg.ANY, "atpqd", alg.AND, t.named("atpcd"),
				monoid.named("ctq"));
		t = Tensor.reduce(alg.ANY, "atpqr", alg.AND, t.named("atpqd"),
				monoid.named("dtr"));
		t = Tensor.reduce(alg.ALL, "spqr", alg.EQ, t.named("atpqr"),
				monoid.named("ats"));
		t = Tensor.fold(Tensor.fold(t, 1, alg.ONE), 3, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL isEssential2(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 3);

		Tensor<BOOL> t;
		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 2, alg.ALL);
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 2, alg.ALL);
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	public <BOOL> BOOL isEssential3(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 4);

		Tensor<BOOL> t;
		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2, 3 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 3, alg.ALL);
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0, 3 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 3, alg.ALL);
		proj = alg.or(proj, t.get());

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 3, 0 });
		t = Tensor.fold(Tensor.fold(t, 1, alg.EQS), 3, alg.ALL);
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	public <BOOL> BOOL isSubalg1(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 1;
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "y", alg.AND, rel.named("x"),
				monoid.named("yxp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 1, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL isSubalg2(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 2;
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "uyp", alg.AND, rel.named("xy"),
				monoid.named("uxp"));
		t = Tensor.reduce(alg.ANY, "uv", alg.AND, t.named("uyp"),
				monoid.named("vyp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 2, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL isSubalg3(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 3;
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "uyzp", alg.AND, rel.named("xyz"),
				monoid.named("uxp"));
		t = Tensor.reduce(alg.ANY, "uvzp", alg.AND, t.named("uyzp"),
				monoid.named("vyp"));
		t = Tensor.reduce(alg.ANY, "uvw", alg.AND, t.named("uvzp"),
				monoid.named("wzp"));

		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 3, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL preserves1(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 1;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "x", alg.ALL, rel.named("a"),
				rel.named("b"), func.named("xab"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 1, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL preserves2(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 2;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "xy", alg.ALL, rel.named("ab"),
				rel.named("cd"), func.named("xac"), func.named("ybd"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 2, alg.ALL);

		return t.get();
	}

	public <BOOL> BOOL preserves3(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "xyz", alg.ALL, rel.named("abc"),
				rel.named("def"), func.named("xad"), func.named("ybe"),
				func.named("zcf"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(t, 3, alg.ALL);

		return t.get();
	}

	public MonodialInt(final int size, String monoid, String name, int[] sizes) {
		super(name, sizes);

		final int[] monops = decodeMonoid(monoid);
		assert monops.length % size == 0;
		this.size = size;

		List<Integer> elems = new ArrayList<Integer>();
		for (int a : monops)
			elems.add(a);

		this.monoid = Tensor.generate(new int[] { size, size,
				monops.length / size }, new Func1<Boolean, int[]>() {
			@Override
			public Boolean call(int[] elem) {
				int i = elem[1] + elem[2] * size;
				return monops[i] == elem[0];
			}
		});
	}

	public static Tensor<Boolean> collect(
			List<Map<String, Tensor<Boolean>>> solutions, String name) {
		List<Tensor<Boolean>> list = new ArrayList<Tensor<Boolean>>();
		for (Map<String, Tensor<Boolean>> solution : solutions)
			list.add(solution.get(name));

		return Tensor.stack(list);
	}

	public static Tensor<Boolean> getUnaryRels(SatSolver solver, int size,
			String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel", new int[] { size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isSubalg1(alg, func);

				return res;
			}
		};

		List<Map<String, Tensor<Boolean>>> solutions = prob.solveAll(solver);
		return collect(solutions, "rel");
	}

	public static Tensor<Boolean> getBinaryRels(SatSolver solver, int size,
			String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel", new int[] { size,
				size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isSubalg2(alg, func);

				return res;
			}
		};

		List<Map<String, Tensor<Boolean>>> solutions = prob.solveAll(solver);
		return collect(solutions, "rel");
	}

	public static Tensor<Boolean> getTernaryRels(SatSolver solver, int size,
			String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel", new int[] { size,
				size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isSubalg3(alg, func);

				return res;
			}
		};

		List<Map<String, Tensor<Boolean>>> solutions = prob.solveAll(solver);
		return collect(solutions, "rel");
	}

	public static Tensor<Boolean> getBinaryOps(SatSolver solver, int size,
			String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "func", new int[] { size,
				size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");

				BOOL res = isFunction(alg, func);
				res = alg.and(res, inStabilizer2(alg, func));
				res = alg.and(res, isEssential2(alg, func));

				return res;
			}
		};

		List<Map<String, Tensor<Boolean>>> solutions = prob.solveAll(solver);
		return collect(solutions, "func");
	}

	public static Tensor<Boolean> getTernaryOps(SatSolver solver, int size,
			String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "func", new int[] { size,
				size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");

				BOOL res = isFunction(alg, func);
				res = alg.and(res, inStabilizer3(alg, func));
				res = alg.and(res, isEssential3(alg, func));

				return res;
			}
		};

		List<Map<String, Tensor<Boolean>>> solutions = prob.solveAll(solver);
		return collect(solutions, "func");
	}

	public static int[] decodeMonoid(String monoid) {
		List<Integer> xs = new ArrayList<Integer>();
		for (int i = 0; i < monoid.length(); i++) {
			char c = monoid.charAt(i);
			if (c == ' ')
				continue;

			if (c < '0' || c > '9')
				throw new IllegalArgumentException();

			xs.add(c - '0');
		}

		int[] ys = new int[xs.size()];
		for (int i = 0; i < xs.size(); i++)
			ys[i] = xs.get(i);

		return ys;
	}

	public static void main(String[] args) {
		SatSolver solver = new Sat4J();
		solver.debugging = false;

		int size = 3;
		String monoid = "000 001 002 010 011 012 020 022 100 101 110 111 200 202 220 222";
		System.out.println("monoid: " + monoid);

		System.out.println("unary relations:       "
				+ getUnaryRels(solver, size, monoid).getDim(0));

		System.out.println("binary relations:      "
				+ getBinaryRels(solver, size, monoid).getDim(0));

		System.out.println("essential binary ops:  "
				+ getBinaryOps(solver, size, monoid).getDim(0));

		System.out.println("ternary relations:     "
				+ getTernaryRels(solver, size, monoid).getDim(0));

		System.out.println("essential ternary ops: "
				+ getTernaryOps(solver, size, monoid).getDim(0));
	}
}
