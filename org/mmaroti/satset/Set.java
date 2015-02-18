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

package org.mmaroti.satset;

import java.io.*;
import java.util.*;

public abstract class Set {
	public final int[] shape;

	public Set(int[] shape) {
		this.shape = shape;
	}

	public abstract List<Matrix<Boolean>> elements();

	public abstract BoolTerm member(Matrix<BoolTerm> elem);

	public List<Matrix<Boolean>> solve(Solver solver) throws IOException {
		List<Matrix<Boolean>> solutions = new ArrayList<Matrix<Boolean>>();

		BoolTerm[] variables = variables();
		BoolTerm.Instance instance = instance(variables);

		for (;;) {
			boolean[] solution = solver.solve(instance.variables,
					instance.clauses);
			if (solution == null)
				break;

			boolean[] decoded = decode(instance, variables, solution);
			solutions.add(Matrix.matrix(shape, decoded));

			if (variables.length == 0)
				break;

			int[] clause = new int[variables.length];
			for (int i = 0; i < clause.length; i++) {
				int lit = instance.getLiteral(variables[i]);
				assert lit != 0;

				clause[i] = decoded[i] ? -lit : lit;
			}
			instance.addClause(clause);
		}

		return solutions;
	}

	public Matrix<Boolean> solveOne(Solver solver) throws IOException {
		BoolTerm[] variables = variables();
		BoolTerm.Instance instance = instance(variables);

		boolean[] solution = solver.solve(instance.variables, instance.clauses);
		if (solution == null)
			return null;

		boolean[] decoded = decode(instance, variables, solution);
		return Matrix.matrix(shape, decoded);
	}

	private BoolTerm[] variables() {
		BoolTerm[] variables = new BoolTerm[Matrix.getSize(shape)];

		for (int i = 0; i < variables.length; i++)
			variables[i] = BoolTerm.newVariable();

		return variables;
	}

	private BoolTerm.Instance instance(BoolTerm[] variables) {
		Matrix<BoolTerm> matrix = Matrix.matrix(shape, variables);
		BoolTerm.Instance instance = member(matrix).instance();

		for (BoolTerm term : variables) {
			int lit = instance.getLiteral(term);
			if (lit == 0) {
				lit = instance.addVariable(term);
				instance.addClause(new int[] { lit, -lit });
			}
		}

		return instance;
	}

	private boolean[] decode(BoolTerm.Instance instance, BoolTerm[] variables,
			boolean[] solution) {
		boolean[] decoded = new boolean[variables.length];

		for (int i = 0; i < decoded.length; i++)
			decoded[i] = variables[i].decode(instance, solution);

		return decoded;
	}

	public final static Set BOOL = new Set(new int[0]) {
		private final List<Matrix<Boolean>> elems;;

		{
			elems = new ArrayList<Matrix<Boolean>>();
			elems.add(Matrix.scalar(Boolean.FALSE));
			elems.add(Matrix.scalar(Boolean.TRUE));
		}

		@Override
		public List<Matrix<Boolean>> elements() {
			return elems;
		}

		@Override
		public BoolTerm member(Matrix<BoolTerm> elem) {
			assert Arrays.equals(shape, elem.shape);
			return BoolTerm.TRUE;
		}
	};

	public static Set power(final Set set, final int power) {
		if (power < 0)
			throw new IllegalArgumentException();

		final List<Matrix<Boolean>> elems = set.elements();

		int[] shape = new int[1 + set.shape.length];
		shape[0] = power;
		System.arraycopy(set.shape, 0, shape, 1, set.shape.length);

		return new Set(shape) {
			@Override
			public List<Matrix<Boolean>> elements() {
				List<Matrix<Boolean>> list = new ArrayList<Matrix<Boolean>>();

				if (elems.size() > 0 || power == 0) {
					int[] indices = new int[power];
					for (;;) {
						List<Matrix<Boolean>> rows = new ArrayList<Matrix<Boolean>>();
						for (int i = 0; i < indices.length; i++)
							rows.add(elems.get(indices[i]));

						Matrix<Boolean> matrix = Matrix
								.collect(rows, set.shape);
						assert Arrays.equals(matrix.shape, shape);
						list.add(matrix);

						int i = indices.length;
						while (--i >= 0 && ++indices[i] >= elems.size())
							indices[i] = 0;

						if (i < 0)
							break;
					}
				}

				return list;
			}

			@Override
			public BoolTerm member(Matrix<BoolTerm> elem) {
				assert Arrays.equals(shape, elem.shape);

				BoolTerm t = BoolTerm.TRUE;
				for (int i = 0; i < shape[0]; i++)
					t = t.and(set.member(elem.row(i)));

				return t;
			}
		};
	}

	public abstract static class SubSet extends Set {
		public final Set base;

		public SubSet(Set base) {
			super(base.shape);
			this.base = base;
		}

		public abstract BoolTerm filter(Matrix<BoolTerm> elem);

		@Override
		public List<Matrix<Boolean>> elements() {
			List<Matrix<Boolean>> elems = base.elements();
			List<Matrix<Boolean>> list = new ArrayList<Matrix<Boolean>>();

			for (Matrix<Boolean> elem : elems) {
				if (filter(Matrix.apply(Func1.BOOLTERM_LIFT, elem)).lower())
					list.add(elem);
			}

			return list;
		}

		@Override
		public BoolTerm member(Matrix<BoolTerm> elem) {
			return base.member(elem).and(filter(elem));
		}
	}

	public static void print(Matrix<Boolean> matrix) {
		System.out.println(Matrix.apply(Func1.BOOLEAN_INT, matrix));
	}

	public static void print(List<Matrix<Boolean>> list) {
		for (Matrix<Boolean> matrix : list)
			print(matrix);
	}
}
