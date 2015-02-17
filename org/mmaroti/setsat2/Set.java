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

package org.mmaroti.setsat2;

import java.io.*;
import java.util.*;

public abstract class Set {
	public final int[] shape;

	public Set(int[] shape) {
		this.shape = shape;
	}

	public abstract List<Matrix<Boolean>> elements();

	public abstract int member(Instance instance, Matrix<Integer> arg);

	public List<Matrix<Boolean>> solveAll(Solver solver) throws IOException {
		List<Matrix<Boolean>> solutions = new ArrayList<Matrix<Boolean>>();

		Instance instance = new Instance();
		Matrix<Integer> matrix = generate(instance);

		for (;;) {
			boolean[] solution = solver.solve(instance);
			if (solution == null)
				break;

			Matrix<Boolean> decoded = decode(matrix, solution);
			solutions.add(decoded);

			if (matrix.isEmpty())
				break;

			int t = Instance.FALSE;
			int[] index = new int[matrix.shape.length];
			do {
				int s = matrix.get(index);
				t = instance.and(t, decoded.get(index) ? instance.not(s) : s);
			} while (matrix.nextIndex(index));

			instance.ensure(t);
		}

		return solutions;
	}

	public Matrix<Boolean> solveOne(Solver solver) throws IOException {
		Instance instance = new Instance();
		Matrix<Integer> elem = generate(instance);

		boolean[] solution = solver.solve(instance);
		if (solution == null)
			return null;

		return decode(elem, solution);
	}

	private Matrix<Integer> generate(Instance instance) {
		int[] variables = new int[Matrix.getIntSize(shape)];

		for (int i = 0; i < variables.length; i++)
			variables[i] = instance.newvar();

		Matrix<Integer> matrix = Matrix.matrix(shape, variables);
		instance.ensure(member(instance, matrix));

		return matrix;
	}

	private Matrix<Boolean> decode(final Matrix<Integer> matrix,
			final boolean[] solution) {
		return Matrix.cache(new Matrix<Boolean>(matrix.shape) {
			@Override
			public Boolean get(int[] index) {
				int var = matrix.get(index);
				return Instance.decode(var, solution);
			}
		});
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
		public int member(Instance instance, Matrix<Integer> arg) {
			return Instance.TRUE;
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
			public int member(Instance instance, Matrix<Integer> arg) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}
}
