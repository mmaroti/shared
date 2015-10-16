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
		func = Tensor.fold(alg.ONE, 1, func);
		func = Tensor.fold(alg.ALL, func.getOrder(), func);
		return func.get();
	}

	public <BOOL> BOOL inStabilizer2(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 3);
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xytp", alg.AND,
				func.named("xyz"), monoid.named("ztp"));
		t = Tensor.reduce(alg.ANY, "xtpq", alg.AND, t.named("xytp"),
				monoid.named("ytq"));
		t = Tensor.reduce(alg.ALL, "rpq", alg.EQU, t.named("xtpq"),
				monoid.named("xtr"));
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.ONE, 1, t));

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
		t = Tensor.reduce(alg.ALL, "spqr", alg.EQU, t.named("atpqr"),
				monoid.named("ats"));
		t = Tensor.fold(alg.ALL, 3, Tensor.fold(alg.ONE, 1, t));

		return t.get();
	}

	public <BOOL> BOOL isEssential2(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 3);

		Tensor<BOOL> t;
		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2 });
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.EQS, 1, t));
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0 });
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.EQS, 1, t));
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	public <BOOL> BOOL isEssential3(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		assert (func.getOrder() == 4);

		Tensor<BOOL> t;
		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2, 3 });
		t = Tensor.fold(alg.ALL, 3, Tensor.fold(alg.EQS, 1, t));
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0, 3 });
		t = Tensor.fold(alg.ALL, 3, Tensor.fold(alg.EQS, 1, t));
		proj = alg.or(proj, t.get());

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 3, 0 });
		t = Tensor.fold(alg.ALL, 3, Tensor.fold(alg.EQS, 1, t));
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	public <BOOL> BOOL isCompRel1(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 1;
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "y", alg.AND, rel.named("x"),
				monoid.named("yxp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 1, t);

		return t.get();
	}

	public <BOOL> BOOL isCompRel2(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 2;
		Tensor<BOOL> monoid = Tensor.map(alg.LIFT, this.monoid);

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "uyp", alg.AND, rel.named("xy"),
				monoid.named("uxp"));
		t = Tensor.reduce(alg.ANY, "uv", alg.AND, t.named("uyp"),
				monoid.named("vyp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 2, t);

		return t.get();
	}

	public <BOOL> BOOL isCompRel3(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
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
		t = Tensor.fold(alg.ALL, 3, t);

		return t.get();
	}

	public <BOOL> BOOL preserves1(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 1;

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xb", alg.AND,
				func.named("xab"), rel.named("a"));
		t = Tensor.reduce(alg.ANY, "x", alg.AND, t.named("xb"), rel.named("b"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 1, t);

		return t.get();
	}

	public <BOOL> BOOL preserves3(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 3;

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "bcxd", alg.AND,
				rel.named("abc"), func.named("xad"));
		t = Tensor.reduce(alg.ANY, "cxyde", alg.AND, t.named("bcxd"),
				func.named("ybe"));
		t = Tensor.reduce(alg.ANY, "xyzdef", alg.AND, t.named("cxyde"),
				func.named("zcf"));
		t = Tensor.reduce(alg.ANY, "xyz", alg.AND, t.named("xyzdef"),
				rel.named("def"));

		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 3, t);

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

	public static <SBOOL> Tensor<Boolean> getUnaryRels(SatSolver<SBOOL> solver,
			int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel",
				new int[] { size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isCompRel1(alg, func);

				return res;
			}
		};

		return prob.solveAll(solver).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getBinaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel", new int[] {
				size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isCompRel2(alg, func);

				return res;
			}
		};

		return prob.solveAll(solver).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getTernaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "rel", new int[] {
				size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("rel");

				BOOL res = isCompRel3(alg, func);

				return res;
			}
		};

		return prob.solveAll(solver, 20000).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getBinaryOps(SatSolver<SBOOL> solver,
			int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "func", new int[] {
				size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");

				BOOL res = isFunction(alg, func);
				res = alg.and(res, inStabilizer2(alg, func));

				return res;
			}
		};

		return prob.solveAll(solver).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialBinaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "func", new int[] {
				size, size, size }) {
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

		return prob.solveAll(solver).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialTernaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		MonodialInt prob = new MonodialInt(size, monoid, "func", new int[] {
				size, size, size, size }) {
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

		return prob.solveAll(solver, 20000).get("func");
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

	public static void main2(String[] args) {
		SatSolver<Integer> solver = new Sat4J();
		solver.debugging = false;

		long time = System.currentTimeMillis();

		int size = 3;
		String monoid = "012 021";
		System.out.println("monoid: " + monoid);

		System.out.println("unary relations:       "
				+ getUnaryRels(solver, size, monoid).getDim(1));

		System.out.println("binary relations:      "
				+ getBinaryRels(solver, size, monoid).getDim(2));

		System.out.println("essential binary ops:  "
				+ getEssentialBinaryOps(solver, size, monoid).getDim(3));

		System.out.println("ternary relations:     "
				+ getTernaryRels(solver, size, monoid).getDim(3));

		System.out.println("essential ternary ops: "
				+ getEssentialTernaryOps(solver, size, monoid).getDim(4));

		time = System.currentTimeMillis() - time;
		System.err.println("finished in " + (0.001 * time) + " seconds");
	}

	public static void printBinRels(Tensor<Boolean> binrels) {
		assert binrels.getOrder() == 3;

		for (int p = 0; p < binrels.getDim(2); p++) {
			System.out.print("binrel " + p + ":");
			for (int i = 0; i < binrels.getDim(0); i++) {
				for (int j = 0; j < binrels.getDim(1); j++)
					if (binrels.getElem(i, j, p))
						System.out.print(" " + i + "" + j);
			}
			System.out.println();
		}
	}

	public static void printBinOps(Tensor<Boolean> binops) {
		assert binops.getOrder() == 4;

		for (int p = 0; p < binops.getDim(3); p++) {
			System.out.print("binop " + p + ":");
			for (int i = 0; i < binops.getDim(1); i++) {
				System.out.print(" ");
				for (int j = 0; j < binops.getDim(2); j++)
					for (int k = 0; k < binops.getDim(0); k++)
						if (binops.getElem(k, i, j, p))
							System.out.print(k);
			}
			System.out.println();
		}
	}

	public <BOOL> BOOL preserves2(BoolAlg<BOOL> alg, Tensor<BOOL> func,
			Tensor<BOOL> rel) {
		assert func.getOrder() == 3 && rel.getOrder() == 2;

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "bxc", alg.AND,
				rel.named("ab"), func.named("xac"));
		t = Tensor.reduce(alg.ANY, "xycd", alg.AND, t.named("bxc"),
				func.named("ybd"));
		t = Tensor.reduce(alg.ANY, "xy", alg.AND, t.named("xycd"),
				rel.named("cd"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 2, t);

		return t.get();
	}

	public static Tensor<Boolean> getCompatMat22(Tensor<Boolean> binrels,
			Tensor<Boolean> binops) {
		assert binrels.getOrder() == 3 && binops.getOrder() == 4;

		BoolAlg<Boolean> alg = BoolAlg.BOOLEAN;
		Tensor<Boolean> t = Tensor.reduce(alg.ANY, "bcxrf", alg.AND,
				binrels.named("abr"), binops.named("xacf"));
		t = Tensor.reduce(alg.ANY, "cdxyrf", alg.AND, t.named("bcxrf"),
				binops.named("ybdf"));
		t = Tensor.reduce(alg.ANY, "xyrf", alg.AND, t.named("cdxyrf"),
				binrels.named("cdr"));
		t = Tensor.reduce(alg.ALL, "rf", alg.LEQ, t.named("xyrf"),
				binrels.named("xyr"));

		return t;
	}

	public static void printBinMat(String what, Tensor<Boolean> binrel) {
		assert binrel.getOrder() == 2;

		System.out.println(what + ":");
		for (int i = 0; i < binrel.getDim(0); i++) {
			for (int j = 0; j < binrel.getDim(1); j++)
				System.out.print(binrel.getElem(i, j) ? "1" : "0");
			System.out.println();
		}
	}

	public static void main(String[] args) {
		SatSolver<Integer> solver = new Sat4J();
		solver.debugging = false;

		int size = 3;
		String monoid = "000 001 002 010 012 020 111 222";
		System.out.println("monoid: " + monoid);

		Tensor<Boolean> binrels = getBinaryRels(solver, size, monoid);
		printBinRels(binrels);

		Tensor<Boolean> binops = getBinaryOps(solver, size, monoid);
		printBinOps(binops);

		Tensor<Boolean> compat = getCompatMat22(binrels, binops);
		printBinMat("compat", compat);
	}
}
