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

package org.mmaroti.sat.testing;

import org.mmaroti.sat.vector.*;
import java.util.*;

public abstract class BooleanVectorFuzzing {
	public static int size = 6400;

	public static long[] boolVec1 = BooleanAlg.INSTANCE.create(size);
	public static byte[] byteVec1 = ByteAlg.INSTANCE.create(size);

	public static long[] boolVec2 = BooleanAlg.INSTANCE.create(size);
	public static byte[] byteVec2 = ByteAlg.INSTANCE.create(size);

	public static Random random = new Random();

	public static void verify() {
		for (int i = 0; i < size; i++) {
			boolean a = BooleanAlg.INSTANCE.slowGet(boolVec1, i);
			boolean b = ByteAlg.INSTANCE.slowGet(byteVec1, i) != 0;
			if (a != b)
				throw new IllegalStateException("We have found a bug.");
		}

		for (int i = 0; i < size; i++) {
			boolean a = BooleanAlg.INSTANCE.slowGet(boolVec2, i);
			boolean b = ByteAlg.INSTANCE.slowGet(byteVec2, i) != 0;
			if (a != b)
				throw new IllegalStateException("We have found a bug.");
		}
	}

	public static void randomFlip(int count) {
		for (int c = 0; c < count; c++) {
			int i = random.nextInt(size);

			boolean a = BooleanAlg.INSTANCE.slowGet(boolVec1, i);
			boolean b = ByteAlg.INSTANCE.slowGet(byteVec1, i) != 0;
			if (a != b)
				throw new IllegalStateException();

			BooleanAlg.INSTANCE.slowSet(boolVec1, i, !a);
			ByteAlg.INSTANCE.slowSet(byteVec1, i, a ? (byte) 0 : (byte) 1);
		}
	}

	public static void randomCopy(int count) {
		for (int c = 0; c < count; c++) {
			int d = 1 + random.nextInt(100);
			int i = random.nextInt(size - d);
			int j = random.nextInt(size - d);

			BooleanCopy.INSTANCE.apply(boolVec1, i, boolVec2, j, d);
			ByteCopy.INSTANCE.apply(byteVec1, i, byteVec2, j, d);
		}

		BooleanCopy.INSTANCE.apply(boolVec2, 0, boolVec1, 0, size);
		ByteCopy.INSTANCE.apply(byteVec2, 0, byteVec1, 0, size);
	}

	public static void main(String[] args) {
		for (int c = 0; c < 10000; c++) {
			randomFlip(100);
			verify();
			randomCopy(100);
			verify();
		}
		System.out.println("No problems detected.");
	}
}
