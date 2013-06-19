/**
 * Copyright (C) Miklos Maroti, 2011
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

package org.mmaroti.dsp;

import Jama.*;

public class CicTest {
	public static int[] derivate(int[] input, int offset) {
		int[] output = new int[input.length];

		for (int i = 0; i < output.length; ++i) {
			int a = i - offset < 0 ? 0 : input[i - offset];
			int b = i >= input.length ? 0 : input[i];

			output[i] = b - a;
		}

		return output;
	}

	public static int[] integrate(int[] input) {
		int[] output = new int[input.length];

		int a = 0;
		for (int i = 0; i < output.length; ++i) {
			a += input[i];
			output[i] = a;
		}

		return output;
	}

	public static int[] pad(int[] input, int left, int right) {
		int[] output = new int[input.length + left + right];

		for (int i = 0; i < input.length; ++i)
			output[i + left] = input[i];

		return output;
	}

	public static int[] trim(int[] input) {
		int i = 0;
		while (i < input.length && input[i] == 0)
			++i;

		int j = input.length - 1;
		while (j > i && input[j] == 0)
			--j;

		int[] output = new int[j + 1 - i];
		for (int k = 0; k < output.length; ++k)
			output[k] = input[i + k];

		return output;
	}

	public static void print(int[] input) {
		String s = "[";
		for (int i = 0; i < input.length; ++i) {
			s += " " + input[i];
		}
		s += " ]";

		System.out.println(s);
	}

	public static int[] transfer(int inter, int decim) {
		int[] a = new int[] { 1 };
		a = pad(a, 0, 4 * (inter + decim - 2));

		for (int i = 0; i < 4; ++i)
			a = derivate(a, inter);

		for (int i = 0; i < 8; ++i)
			a = integrate(a);

		for (int i = 0; i < 4; ++i)
			a = derivate(a, decim);

		return trim(a);
	}

	public static int checkLength(int[] taps, int inter, int decim) {
		return (taps.length + 1 - decim) / (inter - decim);
	}

	public static Matrix getCheckMatrix(int[] taps, int inter, int decim,
			int start, int rows) {
		assert (inter > decim && inter < taps.length);
		assert (0 <= start && start <= inter);

		Matrix A = new Matrix(rows, rows + 1);

		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j <= rows; ++j) {
				int k = start + j * decim - i * inter;
				A.set(i, j, k < 0 || k >= taps.length ? 0 : taps[k]);
			}
		}

		return A.transpose();
	}

	public static Matrix getLeastSquares(Matrix A) {
		A = A.times((A.transpose().times(A)).inverse().times(A.transpose()));
		return A.minus(Matrix.identity(A.getRowDimension(), A.getRowDimension()));
	}

	public static void main(String[] args) {
		int[] taps = transfer(3, 2);
		print(taps);

		Matrix A = getCheckMatrix(taps, 3, 2, 10, 11);
		A.print(4, 1);

		Matrix B = getLeastSquares(A);
		System.out.println(B.rank());
		for (int i = 0; i < B.getRowDimension(); ++i) {
			double c = 1.0 / B.get(i, 0);
			for (int j = 0; j < B.getColumnDimension(); ++j)
				B.set(i, j, c * B.get(i, j));
		}
		B.print(5, 3);

		Matrix C = B.times(getCheckMatrix(taps, 3, 2, 10, 11));
		C.print(5, 3);
	}
}
