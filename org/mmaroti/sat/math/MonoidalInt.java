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

import java.text.*;
import java.util.*;

import org.mmaroti.sat.core.*;
import org.mmaroti.sat.solvers.*;

public class MonoidalInt {
	public static Tensor<Boolean> decodeMonoid(final int size, String monoid) {
		final List<Integer> elems = new ArrayList<Integer>();
		for (int i = 0; i < monoid.length(); i++) {
			char c = monoid.charAt(i);
			if (c == ' ')
				continue;

			if (c < '0' || c > '9')
				throw new IllegalArgumentException();

			elems.add(c - '0');
		}

		assert elems.size() % size == 0;
		return Tensor.generate(new int[] { size, size, elems.size() / size },
				new Func1<Boolean, int[]>() {
					public Boolean call(int[] elem) {
						int i = elem[1] + elem[2] * size;
						return elems.get(i) == elem[0];
					}
				});
	}

	public static <BOOL> BOOL isFunction(BoolAlg<BOOL> alg, Tensor<BOOL> func) {
		func = Tensor.fold(alg.ONE, 1, func);
		func = Tensor.fold(alg.ALL, func.getOrder(), func);
		return func.get();
	}

	public static <BOOL> BOOL isReflexiveRel(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 2 && rel.getDim(0) == rel.getDim(1);

		Tensor<BOOL> t = Tensor.reshape(rel, new int[] { rel.getDim(0) },
				new int[] { 0, 0 });
		t = Tensor.fold(alg.ALL, 1, t);

		return t.get();
	}

	public static <BOOL> BOOL isSymmetricRel(BoolAlg<BOOL> alg, Tensor<BOOL> rel) {
		assert rel.getOrder() == 2 && rel.getDim(0) == rel.getDim(1);

		Tensor<BOOL> t = Tensor.reduce(alg.ALL, "", alg.EQU, rel.named("xy"),
				rel.named("yx"));

		return t.get();
	}

	public static <BOOL> BOOL isTransitiveRel(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel) {
		assert rel.getOrder() == 2 && rel.getDim(0) == rel.getDim(1);

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xz", alg.AND, rel.named("xy"),
				rel.named("yz"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 2, t);

		return t.get();
	}

	public static <BOOL> BOOL isMajorityOp(BoolAlg<BOOL> alg, Tensor<BOOL> op) {
		assert op.getOrder() == 4;

		BOOL t = Tensor.reduce(alg.ALL, "", alg.ID, op.named("xxxy")).get();
		t = alg.and(t, Tensor.reduce(alg.ALL, "", alg.ID, op.named("xxyx"))
				.get());
		t = alg.and(t, Tensor.reduce(alg.ALL, "", alg.ID, op.named("xyxx"))
				.get());

		return t;
	}

	public static <BOOL> BOOL isMaltsevOp(BoolAlg<BOOL> alg, Tensor<BOOL> op) {
		assert op.getOrder() == 4;

		BOOL t = Tensor.reduce(alg.ALL, "", alg.ID, op.named("yxxy")).get();
		t = alg.and(t, Tensor.reduce(alg.ALL, "", alg.ID, op.named("yyxx"))
				.get());

		return t;
	}

	public static <BOOL> BOOL isStabilizer2(BoolAlg<BOOL> alg,
			Tensor<BOOL> func, Tensor<BOOL> monoid) {
		assert func.getOrder() == 3 && monoid.getOrder() == 3;

		Tensor<BOOL> t = Tensor.reduce(alg.ANY, "xytp", alg.AND,
				func.named("xyz"), monoid.named("ztp"));
		t = Tensor.reduce(alg.ANY, "xtpq", alg.AND, t.named("xytp"),
				monoid.named("ytq"));
		t = Tensor.reduce(alg.ALL, "rpq", alg.EQU, t.named("xtpq"),
				monoid.named("xtr"));
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.ONE, 1, t));

		return t.get();
	}

	public static <BOOL> BOOL isStabilizer3(BoolAlg<BOOL> alg,
			Tensor<BOOL> func, Tensor<BOOL> monoid) {
		assert func.getOrder() == 4 && monoid.getOrder() == 3;

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

	public static <BOOL> BOOL isStabilizer4(BoolAlg<BOOL> alg,
			Tensor<BOOL> func, Tensor<BOOL> monoid) {
		assert func.getOrder() == 5 && monoid.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "cdeaxq", alg.AND, monoid.named("bxq"),
				func.named("abcde"));
		t = Tensor.reduce(alg.ANY, "deaxqr", alg.AND, t.named("cdeaxq"),
				monoid.named("cxr"));
		t = Tensor.reduce(alg.ANY, "eaxqrs", alg.AND, t.named("deaxqr"),
				monoid.named("dxs"));
		t = Tensor.reduce(alg.ANY, "axqrst", alg.AND, t.named("eaxqrs"),
				monoid.named("ext"));
		t = Tensor.reduce(alg.ALL, "pqrst", alg.EQU, t.named("axqrst"),
				monoid.named("axp"));
		t = Tensor.fold(alg.ALL, 4, Tensor.fold(alg.ONE, 1, t));

		return t.get();
	}

	public static <BOOL> BOOL isEssentialRel2(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel) {
		assert rel.getOrder() == 2;

		Tensor<BOOL> t1;
		t1 = Tensor.fold(alg.ANY, 1, rel);
		t1 = Tensor.reshape(t1, rel.getShape(), new int[] { 1 });

		Tensor<BOOL> t2;
		t2 = Tensor.reshape(rel, rel.getShape(), new int[] { 1, 0 });
		t2 = Tensor.fold(alg.ANY, 1, t2);
		t2 = Tensor.reshape(t2, rel.getShape(), new int[] { 0 });

		Tensor<BOOL> t;
		t = Tensor.map2(alg.AND, t1, t2);
		t = Tensor.map2(alg.AND, t, Tensor.map(alg.NOT, rel));
		t = Tensor.fold(alg.ANY, 2, t);

		return t.get();
	}

	public static <BOOL> BOOL isEssentialRel3(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel) {
		assert rel.getOrder() == 3;

		Tensor<BOOL> t0;
		t0 = Tensor.fold(alg.ANY, 1, rel);
		t0 = Tensor.reshape(t0, rel.getShape(), new int[] { 1, 2 });

		Tensor<BOOL> t1;
		t1 = Tensor.reshape(rel, rel.getShape(), new int[] { 1, 0, 2 });
		t1 = Tensor.fold(alg.ANY, 1, t1);
		t1 = Tensor.reshape(t1, rel.getShape(), new int[] { 0, 2 });

		Tensor<BOOL> t2;
		t2 = Tensor.reshape(rel, rel.getShape(), new int[] { 2, 0, 1 });
		t2 = Tensor.fold(alg.ANY, 1, t2);
		t2 = Tensor.reshape(t2, rel.getShape(), new int[] { 0, 1 });

		Tensor<BOOL> t;
		t = Tensor.map2(alg.AND, t0, t1);
		t = Tensor.map2(alg.AND, t, t2);
		t = Tensor.map2(alg.AND, t, Tensor.map(alg.NOT, rel));
		t = Tensor.fold(alg.ANY, 3, t);

		return t.get();
	}

	public static <BOOL> BOOL isEssentialOp2(BoolAlg<BOOL> alg,
			Tensor<BOOL> func) {
		assert func.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 0, 2 });
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.EQS, 1, t));
		BOOL proj = t.get();

		t = Tensor.reshape(func, func.getShape(), new int[] { 1, 2, 0 });
		t = Tensor.fold(alg.ALL, 2, Tensor.fold(alg.EQS, 1, t));
		proj = alg.or(proj, t.get());

		return alg.not(proj);
	}

	public static <BOOL> BOOL isEssentialOp3(BoolAlg<BOOL> alg,
			Tensor<BOOL> func) {
		assert func.getOrder() == 4;

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

	public static <BOOL> BOOL isCompatibleRel1(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel, Tensor<BOOL> monoid) {
		assert rel.getOrder() == 1 && monoid.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "y", alg.AND, rel.named("x"),
				monoid.named("yxp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 1, t);

		return t.get();
	}

	public static <BOOL> BOOL isCompatibleRel2(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel, Tensor<BOOL> monoid) {
		assert rel.getOrder() == 2 && monoid.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "uyp", alg.AND, rel.named("xy"),
				monoid.named("uxp"));
		t = Tensor.reduce(alg.ANY, "uv", alg.AND, t.named("uyp"),
				monoid.named("vyp"));
		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 2, t);

		return t.get();
	}

	public static <BOOL> BOOL isCompatibleRel3(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel, Tensor<BOOL> monoid) {
		assert rel.getOrder() == 3 && monoid.getOrder() == 3;

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

	public static <BOOL> BOOL isCompatibleRel4(BoolAlg<BOOL> alg,
			Tensor<BOOL> rel, Tensor<BOOL> monoid) {
		assert rel.getOrder() == 4 && monoid.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "bcdxm", alg.AND, rel.named("abcd"),
				monoid.named("xam"));
		t = Tensor.reduce(alg.ANY, "cdxym", alg.AND, t.named("bcdxm"),
				monoid.named("ybm"));
		t = Tensor.reduce(alg.ANY, "dxyzm", alg.AND, t.named("cdxym"),
				monoid.named("zcm"));
		t = Tensor.reduce(alg.ANY, "xyzu", alg.AND, t.named("dxyzm"),
				monoid.named("udm"));

		t = Tensor.map2(alg.LEQ, t, rel);
		t = Tensor.fold(alg.ALL, 4, t);

		return t.get();
	}

	public static <SBOOL> Tensor<Boolean> getUnaryRels(SatSolver<SBOOL> solver,
			int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				return isCompatibleRel1(alg, rel, monoid);
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getBinaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				return isCompatibleRel2(alg, rel, monoid);
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialBinaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL t = isCompatibleRel2(alg, rel, monoid);
				t = alg.and(t, isEssentialRel2(alg, rel));

				return t;
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getQuasiorderRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL t = isCompatibleRel2(alg, rel, monoid);
				t = alg.and(t, isReflexiveRel(alg, rel));
				t = alg.and(t, isTransitiveRel(alg, rel));

				return t;
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getTernaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				return isCompatibleRel3(alg, rel, monoid);
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialTernaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL b = isCompatibleRel3(alg, rel, monoid);
				b = alg.and(b, isEssentialRel3(alg, rel));

				return b;
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getQuaternaryRels(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("rel", new int[] { size, size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> rel = tensors.get("rel");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				return isCompatibleRel4(alg, rel, monoid);
			}
		};

		return prob.solveAll(solver, LIMIT).get("rel");
	}

	public static <SBOOL> Tensor<Boolean> getBinaryOps(SatSolver<SBOOL> solver,
			int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer2(alg, func, monoid));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialBinaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer2(alg, func, monoid));
				res = alg.and(res, isEssentialOp2(alg, func));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getTernaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer3(alg, func, monoid));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getQuaternaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size, size,
				size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer4(alg, func, monoid));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getEssentialTernaryOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer3(alg, func, monoid));
				res = alg.and(res, isEssentialOp3(alg, func));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getMajorityOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer3(alg, func, monoid));
				res = alg.and(res, isMajorityOp(alg, func));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static <SBOOL> Tensor<Boolean> getMaltsevOps(
			SatSolver<SBOOL> solver, int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		Problem prob = new Problem("func", new int[] { size, size, size, size }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {

				Tensor<BOOL> func = tensors.get("func");
				Tensor<BOOL> monoid = Tensor.map(alg.LIFT, mon);

				BOOL res = isFunction(alg, func);
				res = alg.and(res, isStabilizer3(alg, func, monoid));
				res = alg.and(res, isMaltsevOp(alg, func));

				return res;
			}
		};

		return prob.solveAll(solver, LIMIT).get("func");
	}

	public static void printBinaryRels(Tensor<Boolean> rels) {
		assert rels.getOrder() == 3;

		for (int p = 0; p < rels.getDim(2); p++) {
			System.out.print("binrel " + p + ":");
			for (int i = 0; i < rels.getDim(0); i++)
				for (int j = 0; j < rels.getDim(1); j++)
					if (rels.getElem(i, j, p))
						System.out.print(" " + i + "" + j);
			System.out.println();
		}
	}

	public static void printTernaryRels(Tensor<Boolean> rels) {
		assert rels.getOrder() == 4;

		for (int p = 0; p < rels.getDim(3); p++) {
			System.out.print("trnrel " + p + ":");
			for (int i = 0; i < rels.getDim(0); i++)
				for (int j = 0; j < rels.getDim(1); j++)
					for (int k = 0; k < rels.getDim(2); k++)
						if (rels.getElem(i, j, k, p))
							System.out.print(" " + i + "" + j + "" + k);
			System.out.println();
		}
	}

	public static void printBinaryOps(Tensor<Boolean> ops) {
		assert ops.getOrder() == 4;

		for (int p = 0; p < ops.getDim(3); p++) {
			System.out.print("binop " + p + ":");
			for (int i = 0; i < ops.getDim(1); i++) {
				System.out.print(" ");
				for (int j = 0; j < ops.getDim(2); j++)
					for (int k = 0; k < ops.getDim(0); k++)
						if (ops.getElem(k, i, j, p))
							System.out.print(k);
			}
			System.out.println();
		}
	}

	public static void printTernaryOps(Tensor<Boolean> ops) {
		assert ops.getOrder() == 5;

		for (int p = 0; p < ops.getDim(4); p++) {
			System.out.print("trnop " + p + ":");
			for (int i = 0; i < ops.getDim(1); i++)
				for (int j = 0; j < ops.getDim(2); j++) {
					System.out.print(" ");
					for (int k = 0; k < ops.getDim(3); k++)
						for (int l = 0; l < ops.getDim(0); l++)
							if (ops.getElem(l, i, j, k, p))
								System.out.print(l);
				}
			System.out.println();
		}
	}

	public static <BOOL> Tensor<BOOL> getCompatibility22(BoolAlg<BOOL> alg,
			Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 4 && rels.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "bcxfr", alg.AND, rels.named("abr"),
				ops.named("xacf"));
		t = Tensor.reduce(alg.ANY, "cdxyfr", alg.AND, t.named("bcxfr"),
				ops.named("ybdf"));
		t = Tensor.reduce(alg.ANY, "xyfr", alg.AND, t.named("cdxyfr"),
				rels.named("cdr"));
		t = Tensor.reduce(alg.ALL, "fr", alg.LEQ, t.named("xyfr"),
				rels.named("xyr"));

		return t;
	}

	public static <BOOL> BOOL isCompatible23(BoolAlg<BOOL> alg,
			Tensor<BOOL> op, Tensor<BOOL> rel) {
		assert op.getOrder() == 3 && rel.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "bcdx", alg.AND, op.named("xad"),
				rel.named("abc"));
		t = Tensor.reduce(alg.ANY, "cdexy", alg.AND, t.named("bcdx"),
				op.named("ybe"));
		t = Tensor.reduce(alg.ANY, "defxyz", alg.AND, t.named("cdexy"),
				op.named("zcf"));
		t = Tensor.reduce(alg.ANY, "xyz", alg.AND, t.named("defxyz"),
				rel.named("def"));
		t = Tensor.reduce(alg.ALL, "", alg.LEQ, t.named("xyz"),
				rel.named("xyz"));

		return t.get();
	}

	public static <BOOL> Tensor<BOOL> getCompatibility23(
			final BoolAlg<BOOL> alg, Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 4 && rels.getOrder() == 4;

		final List<Tensor<BOOL>> os = Tensor.unconcat(ops);
		final List<Tensor<BOOL>> rs = Tensor.unconcat(rels);

		return Tensor.generate(os.size(), rs.size(),
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer a, Integer b) {
						return isCompatible23(alg, os.get(a), rs.get(b));
					}
				});
	}

	public static <BOOL> BOOL isCompatible24(BoolAlg<BOOL> alg,
			Tensor<BOOL> op, Tensor<BOOL> rel) {
		assert op.getOrder() == 3 && rel.getOrder() == 4;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "bcdex", alg.AND, rel.named("abcd"),
				op.named("xae"));
		t = Tensor.reduce(alg.ANY, "cdefxy", alg.AND, t.named("bcdex"),
				op.named("ybf"));
		t = Tensor.reduce(alg.ANY, "cdghxy", alg.AND, t.named("cdefxy"),
				rel.named("efgh"));
		t = Tensor.reduce(alg.ANY, "dhxyz", alg.AND, t.named("cdghxy"),
				op.named("zcg"));
		t = Tensor.reduce(alg.ANY, "xyzu", alg.AND, t.named("dhxyz"),
				op.named("udh"));
		t = Tensor.reduce(alg.ALL, "", alg.LEQ, t.named("xyzu"),
				rel.named("xyzu"));

		return t.get();
	}

	public static <BOOL> Tensor<BOOL> getCompatibility24(
			final BoolAlg<BOOL> alg, Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 4 && rels.getOrder() == 5;

		final List<Tensor<BOOL>> os = Tensor.unconcat(ops);
		final List<Tensor<BOOL>> rs = Tensor.unconcat(rels);

		return Tensor.generate(os.size(), rs.size(),
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer a, Integer b) {
						return isCompatible24(alg, os.get(a), rs.get(b));
					}
				});
	}

	public static <BOOL> BOOL isCompatible32(BoolAlg<BOOL> alg,
			Tensor<BOOL> op, Tensor<BOOL> rel) {
		assert op.getOrder() == 4 && rel.getOrder() == 2;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "acbd", alg.AND, rel.named("ab"),
				rel.named("cd"));
		t = Tensor.reduce(alg.ANY, "bdex", alg.AND, t.named("acbd"),
				op.named("xace"));
		t = Tensor.reduce(alg.ANY, "efxy", alg.AND, t.named("bdex"),
				op.named("ybdf"));
		t = Tensor.reduce(alg.ANY, "xy", alg.AND, t.named("efxy"),
				rel.named("ef"));
		t = Tensor.reduce(alg.ALL, "", alg.LEQ, t.named("xy"), rel.named("xy"));

		return t.get();
	}

	public static <BOOL> Tensor<BOOL> getCompatibility32(
			final BoolAlg<BOOL> alg, Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 5 && rels.getOrder() == 3;

		final List<Tensor<BOOL>> os = Tensor.unconcat(ops);
		final List<Tensor<BOOL>> rs = Tensor.unconcat(rels);

		return Tensor.generate(os.size(), rs.size(),
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer a, Integer b) {
						return isCompatible32(alg, os.get(a), rs.get(b));
					}
				});
	}

	public static <BOOL> BOOL isCompatible33(BoolAlg<BOOL> alg,
			Tensor<BOOL> op, Tensor<BOOL> rel) {
		assert op.getOrder() == 4 && rel.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "adbecf", alg.AND, rel.named("abc"),
				rel.named("def"));
		t = Tensor.reduce(alg.ANY, "becfgx", alg.AND, t.named("adbecf"),
				op.named("xadg"));
		t = Tensor.reduce(alg.ANY, "cfghxy", alg.AND, t.named("becfgx"),
				op.named("ybeh"));
		t = Tensor.reduce(alg.ANY, "ghixyz", alg.AND, t.named("cfghxy"),
				op.named("zcfi"));
		t = Tensor.reduce(alg.ANY, "xyz", alg.AND, t.named("ghixyz"),
				rel.named("ghi"));
		t = Tensor.reduce(alg.ALL, "", alg.LEQ, t.named("xyz"),
				rel.named("xyz"));

		return t.get();
	}

	public static <BOOL> Tensor<BOOL> getCompatibility33(
			final BoolAlg<BOOL> alg, Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 5 && rels.getOrder() == 4;

		final List<Tensor<BOOL>> os = Tensor.unconcat(ops);
		final List<Tensor<BOOL>> rs = Tensor.unconcat(rels);

		return Tensor.generate(os.size(), rs.size(),
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer a, Integer b) {
						return isCompatible33(alg, os.get(a), rs.get(b));
					}
				});
	}

	public static <BOOL> BOOL isCompatible34(BoolAlg<BOOL> alg,
			Tensor<BOOL> op, Tensor<BOOL> rel) {
		assert op.getOrder() == 4 && rel.getOrder() == 4;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "aebfcgdh", alg.AND, rel.named("abcd"),
				rel.named("efgh"));
		t = Tensor.reduce(alg.ANY, "bfcgdhix", alg.AND, t.named("aebfcgdh"),
				op.named("xaei"));
		t = Tensor.reduce(alg.ANY, "cgdhijxy", alg.AND, t.named("bfcgdhix"),
				op.named("ybfj"));
		t = Tensor.reduce(alg.ANY, "dhijkxyz", alg.AND, t.named("cgdhijxy"),
				op.named("zcgk"));
		t = Tensor.reduce(alg.ANY, "ijklxyzu", alg.AND, t.named("dhijkxyz"),
				op.named("udhl"));
		t = Tensor.reduce(alg.ANY, "xyzu", alg.AND, t.named("ijklxyzu"),
				rel.named("ijkl"));
		t = Tensor.reduce(alg.ALL, "", alg.LEQ, t.named("xyzu"),
				rel.named("xyzu"));

		return t.get();
	}

	public static <BOOL> Tensor<BOOL> getCompatibility34(
			final BoolAlg<BOOL> alg, Tensor<BOOL> ops, Tensor<BOOL> rels) {
		assert ops.getOrder() == 5 && rels.getOrder() == 5;

		final List<Tensor<BOOL>> os = Tensor.unconcat(ops);
		final List<Tensor<BOOL>> rs = Tensor.unconcat(rels);

		return Tensor.generate(os.size(), rs.size(),
				new Func2<BOOL, Integer, Integer>() {
					@Override
					public BOOL call(Integer a, Integer b) {
						return isCompatible34(alg, os.get(a), rs.get(b));
					}
				});
	}

	public static void printMatrix(String what, Tensor<Boolean> rel) {
		assert rel.getOrder() == 2;

		System.out.println(what + ":");
		for (int j = 0; j < rel.getDim(1); j++) {
			for (int i = 0; i < rel.getDim(0); i++)
				System.out.print(rel.getElem(i, j) ? "1" : "0");
			System.out.println();
		}
	}

	public static <BOOL> BOOL isClosedSubset(BoolAlg<BOOL> alg,
			Tensor<BOOL> subset, Tensor<BOOL> galois) {
		Tensor<BOOL> t;

		t = Tensor.reduce(alg.ALL, "y", alg.LEQ, subset.named("x"),
				galois.named("xy"));
		t = Tensor.reduce(alg.ALL, "x", alg.LEQ, t.named("y"),
				galois.named("xy"));
		t = Tensor.map2(alg.EQU, subset, t);
		t = Tensor.fold(alg.ALL, 1, t);

		return t.get();
	}

	public static <SBOOL> Tensor<Boolean> getClosedSubsets(
			SatSolver<SBOOL> solver, final Tensor<Boolean> galois) {
		Problem prob = new Problem("sub", new int[] { galois.getDim(0) }) {
			@Override
			public <BOOL> BOOL compute(BoolAlg<BOOL> alg,
					Map<String, Tensor<BOOL>> tensors) {
				Tensor<BOOL> sub = tensors.get("sub");
				Tensor<BOOL> rel = Tensor.map(alg.LIFT, galois);
				return isClosedSubset(alg, sub, rel);
			}
		};

		return prob.solveAll(solver, LIMIT).get("sub");
	}

	public static <BOOL> Tensor<BOOL> transpose(Tensor<BOOL> matrix) {
		assert matrix.getOrder() == 2;
		return Tensor.reshape(matrix,
				new int[] { matrix.getDim(1), matrix.getDim(0) }, new int[] {
						1, 0 });
	}

	public static <BOOL> BOOL isMonoid(BoolAlg<BOOL> alg, Tensor<BOOL> monoid) {
		assert monoid.getOrder() == 3;

		Tensor<BOOL> t;
		t = Tensor.reduce(alg.ANY, "xzij", alg.AND, monoid.named("xyi"),
				monoid.named("yzj"));
		t = Tensor.reduce(alg.ALL, "kij", alg.EQU, t.named("xzij"),
				monoid.named("xzk"));

		return isFunction(alg, t);
	}

	public static void checkMonoid(int size, String monoid) {
		final Tensor<Boolean> mon = decodeMonoid(size, monoid);
		if (!isMonoid(BoolAlg.BOOLEAN, mon))
			throw new RuntimeException("This is not a monoid: " + monoid);
	}

	public static Tensor<Boolean> sort(Tensor<Boolean> tensor) {
		List<Tensor<Boolean>> list = Tensor.unconcat(tensor);

		Collections.sort(list, new Comparator<Tensor<Boolean>>() {
			@Override
			public int compare(Tensor<Boolean> arg0, Tensor<Boolean> arg1) {
				Iterator<Boolean> iter0 = arg0.iterator();
				Iterator<Boolean> iter1 = arg1.iterator();

				while (iter0.hasNext()) {
					if (!iter1.hasNext())
						return 1;

					boolean b0 = iter0.next();
					boolean b1 = iter1.next();
					if (b0 && !b1)
						return 1;
					else if (!b0 && b1)
						return -1;
				}

				return iter1.hasNext() ? -1 : 0;
			}
		});

		int[] shape = new int[tensor.getOrder() - 1];
		System.arraycopy(tensor.getShape(), 0, shape, 0, shape.length);
		return Tensor.concat(shape, list);
	}

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	public static String[] TWO_MONOIDS = new String[] { "01", "01 00", "01 10",
			"01 00 11", "01 10 00 11" };

	public static String[] INFINITE_MONOIDS = new String[] { "012", "000 012",
			"002 012", "000 001 012", "000 002 012", "000 011 012",
			"000 012 021", "002 012 022", "000 001 002 012", "000 001 010 012",
			"000 001 011 012", "000 001 012 111", "000 001 002 010 012",
			"000 001 002 011 012", "000 001 002 012 111",
			"000 001 010 011 012", "000 001 010 012 111",
			"000 001 011 012 111", "000 001 012 110 111",
			"000 001 012 111 112", "000 001 012 111 222",
			"000 001 002 010 011 012", "000 001 002 010 012 111",
			"000 001 002 011 012 022", "000 001 002 011 012 111",
			"000 001 002 012 110 111", "000 001 002 012 111 112",
			"000 001 002 012 111 222", "000 001 010 011 012 111",
			"000 001 010 012 110 111", "000 001 010 012 111 222",
			"000 001 011 012 111 112", "000 001 011 012 111 222",
			"000 001 012 102 110 111", "000 001 012 110 111 222",
			"000 001 012 111 112 222", "000 002 010 012 101 111",
			"000 002 010 012 111 222", "000 001 002 010 011 012 111",
			"000 001 002 010 012 110 111", "000 001 002 010 012 111 222",
			"000 001 002 011 012 110 111", "000 001 002 011 012 111 112",
			"000 001 002 011 012 111 222", "000 001 002 012 110 111 112",
			"000 001 002 012 110 111 222", "000 001 002 012 111 112 222",
			"000 001 010 011 012 110 111", "000 001 010 011 012 111 222",
			"000 001 010 012 101 110 111", "000 001 010 012 110 111 222",
			"000 001 011 012 111 112 222", "000 001 012 102 110 111 222",
			"000 001 002 010 011 012 110 111",
			"000 001 002 010 011 012 111 222",
			"000 001 002 010 012 101 110 111",
			"000 001 002 010 012 110 111 112",
			"000 001 002 010 012 110 111 222",
			"000 001 002 011 012 100 110 111",
			"000 001 002 011 012 110 111 222",
			"000 001 002 011 012 111 112 222",
			"000 001 002 012 102 110 111 112",
			"000 001 002 012 110 111 112 222",
			"000 001 002 012 110 111 220 222",
			"000 001 010 011 012 110 111 222",
			"000 001 010 012 101 110 111 222",
			"000 001 002 010 011 012 020 021 022",
			"000 001 002 010 011 012 110 111 112",
			"000 001 002 010 011 012 110 111 222",
			"000 001 002 010 012 101 110 111 112",
			"000 001 002 010 012 101 110 111 222",
			"000 001 002 010 012 110 111 112 222",
			"000 001 002 010 012 110 111 220 222",
			"000 001 002 011 012 100 110 111 222",
			"000 001 002 011 012 110 111 220 222",
			"000 001 002 012 102 110 111 112 222",
			"000 001 010 011 012 100 101 110 111",
			"000 001 002 010 011 012 100 101 110 111",
			"000 001 002 010 011 012 110 111 112 222",
			"000 001 002 010 011 012 110 111 220 222",
			"000 001 002 010 012 101 110 111 112 222",
			"000 001 002 010 012 101 110 111 220 222",
			"000 001 002 011 012 100 110 111 220 222",
			"000 001 002 012 110 111 112 220 221 222",
			"000 001 010 011 012 100 101 102 110 111",
			"000 001 010 011 012 100 101 110 111 222",
			"000 001 002 010 011 012 020 021 022 111 222",
			"000 001 002 010 011 012 100 101 110 111 112",
			"000 001 002 010 011 012 100 101 110 111 222",
			"000 001 002 010 012 110 111 112 220 221 222",
			"000 001 002 012 102 110 111 112 220 221 222",
			"000 001 010 011 012 100 101 102 110 111 222",
			"000 001 002 010 011 012 100 101 102 110 111 112",
			"000 001 002 010 011 012 100 101 110 111 112 222",
			"000 001 002 010 011 012 100 101 110 111 220 222",
			"000 001 002 010 011 012 110 111 112 220 221 222",
			"000 001 002 010 012 101 110 111 112 220 221 222",
			"000 001 002 010 011 012 100 101 102 110 111 112 222",
			"000 001 002 010 011 012 100 101 110 111 112 220 221 222",
			"000 001 002 010 011 012 100 101 102 110 111 112 220 221 222",
			"000 001 002 010 011 012 020 021 022 100 101 110 111 200 202 220 222" };

	public static String[] FINITE_MONOIDS = new String[] {
			"000 012 111",
			"012 120 201",
			"000 002 012 111 222",
			"000 012 021 102 111 120 201 210 222",
			"000 001 002 011 012 022 111 112 122 222",
			"000 001 002 010 011 012 020 022 100 101 110 111 112 121 122 200 202 211 212 220 221 222",
			"000 001 002 010 011 012 020 021 022 100 101 110 111 112 121 122 200 202 211 212 220 221 222",
			"000 001 002 010 011 012 020 022 100 101 110 111 112 120 121 122 200 201 202 211 212 220 221 222",
			"000 001 002 010 011 012 020 021 022 100 101 102 110 111 112 120 121 122 200 201 202 210 211 212 220 221 222" };

	public static String[] UNKNOWN_MONOIDS = new String[] { "012 021",
			"002 012 112", "002 012 220", "000 002 010 012", "000 002 012 022",
			"000 002 012 111", "000 002 012 222", "000 011 012 022",
			"002 012 102 112", "000 002 010 012 111", "000 002 012 022 222",
			"000 002 012 111 112", "000 002 012 220 222",
			"000 011 012 021 022", "002 012 022 200 220",
			"002 012 112 220 221", "000 001 002 010 012 020",
			"000 002 010 012 101 111", "000 002 010 012 111 222",
			"000 002 012 022 111 222", "000 002 012 102 111 112",
			"000 002 012 111 112 222", "000 002 012 111 220 222",
			"002 012 022 200 210 220", "002 012 102 112 220 221",
			"000 001 002 010 012 020 021", "000 002 010 012 101 111 222",
			"000 002 012 022 200 220 222", "000 002 012 102 111 112 222",
			"000 001 002 010 011 012 020 022",
			"000 001 002 010 012 020 111 222",
			"000 001 002 011 012 022 111 222",
			"000 001 011 012 111 112 122 222",
			"000 002 010 012 101 111 220 222",
			"000 002 012 022 111 200 220 222",
			"000 002 012 022 200 210 220 222",
			"000 002 012 111 112 220 221 222",
			"000 001 002 010 012 020 021 111 222",
			"000 002 012 022 111 200 210 220 222",
			"000 002 012 102 111 112 220 221 222",
			"000 001 002 010 011 012 020 022 111 222",
			"000 001 002 010 011 012 020 022 100 101 110 111 200 202 220 222" };

	public static void printStatistics(int size, String monoid) {
		SatSolver<Integer> solver = new Sat4J();
		solver.debugging = false;

		long time = System.currentTimeMillis();
		System.out.println("monoid: " + monoid);
		checkMonoid(size, monoid);

		Tensor<Boolean> unaryRels = getUnaryRels(solver, size, monoid);
		System.out.println("unary relations:        " + unaryRels.getDim(1));

		Tensor<Boolean> binaryRels = getBinaryRels(solver, size, monoid);
		System.out.println("binary relations:       " + binaryRels.getDim(2));
		binaryRels = sort(binaryRels);
		// printBinaryRels(binaryRels);

		Tensor<Boolean> ternaryRels = getTernaryRels(solver, size, monoid);
		System.out.println("ternary relations:      " + ternaryRels.getDim(3));

		Tensor<Boolean> qaryRels = getQuaternaryRels(solver, size, monoid);
		System.out.println("quaternary relations:   " + qaryRels.getDim(4));

		System.out.println("essential binary rels:  "
				+ getEssentialBinaryRels(solver, size, monoid).getDim(2));

		System.out.println("essential ternary rels: "
				+ getEssentialTernaryRels(solver, size, monoid).getDim(3));

		System.out.println("quasiorder relations:   "
				+ getQuasiorderRels(solver, size, monoid).getDim(2));

		Tensor<Boolean> binaryOps = getBinaryOps(solver, size, monoid);
		System.out.println("binary ops:             " + binaryOps.getDim(3));
		binaryOps = sort(binaryOps);
		// printBinaryOps(binaryOps);

		Tensor<Boolean> ternaryOps = getTernaryOps(solver, size, monoid);
		System.out.println("ternary ops:            " + ternaryOps.getDim(4));

		Tensor<Boolean> qaryOps = getQuaternaryOps(solver, size, monoid);
		System.out.println("quaternary ops:         " + qaryOps.getDim(5));

		System.out.println("essential binary ops:   "
				+ getEssentialBinaryOps(solver, size, monoid).getDim(3));

		System.out.println("essential ternary ops:  "
				+ getEssentialTernaryOps(solver, size, monoid).getDim(4));

		System.out.println("majority ops:           "
				+ getMajorityOps(solver, size, monoid).getDim(4));

		System.out.println("maltsev ops:            "
				+ getMaltsevOps(solver, size, monoid).getDim(4));

		Tensor<Boolean> compat, closed;

		compat = getCompatibility22(BoolAlg.BOOLEAN, binaryOps, binaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 2 rel 2):    " + closed.getDim(1));
		printMatrix("close binary op sets", sort(closed));

		compat = getCompatibility23(BoolAlg.BOOLEAN, binaryOps, ternaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 2 rel 3):    " + closed.getDim(1));
		printMatrix("closed binary op sets", sort(closed));

		compat = getCompatibility24(BoolAlg.BOOLEAN, binaryOps, qaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 2 rel 4):    " + closed.getDim(1));

		compat = getCompatibility32(BoolAlg.BOOLEAN, ternaryOps, binaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 3 rel 2):    " + closed.getDim(1));

		compat = getCompatibility33(BoolAlg.BOOLEAN, ternaryOps, ternaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 3 rel 3):    " + closed.getDim(1));

		compat = getCompatibility34(BoolAlg.BOOLEAN, ternaryOps, qaryRels);
		closed = getClosedSubsets(solver, compat);
		System.out.println("clones (op 3 rel 4):    " + closed.getDim(1));

		time = System.currentTimeMillis() - time;
		System.out.println("finished in " + TIME_FORMAT.format(0.001 * time)
				+ " seconds\n");
	}

	public static void main(String[] args) {
		printStatistics(3, "000 002 012 102 111 112 222");
		// for (String monoid : TWO_MONOIDS)
		// printStatistics(2, monoid);
	}

	public static void main2(String[] args) {
		System.out.println("FINITE MONOIDS:");
		for (String monoid : FINITE_MONOIDS)
			printStatistics(3, monoid);

		System.out.println("INFINITE MONOIDS:");
		for (String monoid : INFINITE_MONOIDS)
			printStatistics(3, monoid);

		System.out.println("UNKNOWN MONOIDS:");
		for (String monoid : UNKNOWN_MONOIDS)
			printStatistics(3, monoid);
	}

	public static final int LIMIT = 50000;
}
