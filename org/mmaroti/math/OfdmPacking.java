package org.mmaroti.math;

import edu.emory.mathcs.jtransforms.fft.*;
import java.util.*;

public class OfdmPacking {

	static float getAveragePower(float[] vector) {
		float sum = 0.0f;

		for (int i = 0; i < vector.length; ++i) {
			sum += vector[i] * vector[i];
		}

		sum /= (vector.length / 2);
		return sum;
	}

	static float getPeakPower(float[] vector) {
		float max = 0.0f;

		for (int i = 0; i < vector.length; i += 2) {
			float v = vector[i] * vector[i] + vector[i + 1] * vector[i + 1];
			if (v > max)
				max = v;
		}

		return max;
	}

	static float getMaxAmplitudo(float[] vector) {
		float max = 0.0f;

		for (int i = 0; i < vector.length; ++i) {
			float v = vector[i];
			if (v > max)
				max = v;
			else if (-v > max)
				max = -v;
		}

		return max;
	}

	static float[] getRandomData(int size) {
		size *= 2;

		float[] vector = new float[size];
		for (int i = 0; i < size; ++i) {
			vector[i] = Math.random() < 0.5 ? -1.0f : 1.0f;
		}

		return vector;
	}

	static void scaleVector(float[] vector, float scale) {
		for (int i = 0; i < vector.length; ++i)
			vector[i] *= scale;
	}

	static List<float[]> getAllData(int size) {
		ArrayList<float[]> list = new ArrayList<float[]>();

		size *= 2;
		float[] current = new float[size];

		int i;
		for (i = 0; i < size; ++i)
			current[i] = -1.0f;

		outer: for (;;) {
			list.add(current.clone());

			for (i = 0; i < size; ++i) {
				if (current[i] < 0.0f) {
					current[i] = 1.0f;
					continue outer;
				} else
					current[i] = -1.0f;
			}

			break;
		}

		return list;
	}

	public static float[] concatenate(float[] vector1, float[] vector2) {
		float[] target = new float[vector1.length + vector2.length];

		for (int i = 0; i < vector1.length; ++i)
			target[i] = vector1[i];

		for (int i = 0; i < vector2.length; ++i)
			target[i + vector1.length] = vector2[i];

		return target;
	}

	public static float[] addZeros(float[] vector, int length) {
		float[] target = new float[2 * length];

		int shift = length - vector.length / 2;
		for (int i = 0; i < vector.length; ++i)
			target[i + shift] = vector[i];

		return target;
	}

	public static float[] insertZeros(float[] vector, int length) {
		float[] target = new float[2 * length];

		int oldLength = vector.length / 2;
		int step = (length - oldLength) / oldLength;

		int j = 0;
		for (int i = 0; i < oldLength; ++i) {
			target[2 * j] = vector[2 * i];
			target[2 * j + 1] = vector[2 * i + 1];
			j += 1 + step;
		}

		return target;
	}

	public static void printStat(float[] vector) {
		String s = "length=" + (vector.length / 2);
		s += " avgpower=" + getAveragePower(vector);
		s += " peakpower=" + getPeakPower(vector);
		System.out.println(s);
	}

	public static void printVector(float[] vector) {
		String s = "";
		for (int i = 0; i < vector.length; ++i) {
			s += vector[i] + " ";
		}
		System.out.println(s);
	}

	public static FloatFFT_1D fft8 = new FloatFFT_1D(8);
	public static FloatFFT_1D fft16 = new FloatFFT_1D(16);
	public static FloatFFT_1D fft32 = new FloatFFT_1D(32);
	public static FloatFFT_1D fft64 = new FloatFFT_1D(32);

	public static void fftForward(float[] vector) {
		if (vector.length == 16)
			fft8.complexForward(vector);
		else if (vector.length == 32)
			fft16.complexForward(vector);
		else if (vector.length == 64)
			fft32.complexForward(vector);
		else if (vector.length == 128)
			fft64.complexForward(vector);
		else
			throw new IllegalArgumentException();

		// scaleVector(vector, (float) (1.0 / Math.sqrt(vector.length / 2)));
	}

	public static void fftReverse(float[] vector) {
		if (vector.length == 16)
			fft8.complexInverse(vector, false);
		else if (vector.length == 32)
			fft16.complexInverse(vector, false);
		else if (vector.length == 64)
			fft32.complexInverse(vector, false);
		else if (vector.length == 128)
			fft64.complexInverse(vector, false);
		else
			throw new IllegalArgumentException();

		// scaleVector(vector, (float) (1.0 / Math.sqrt(vector.length / 2)));
	}

	public static float[] transform1(float[] vector) {
		return vector;
	}

	public static float[] transform5(float[] vector) {
		return concatenate(vector, vector);
	}

	public static void main(String[] args) {
		float[] vector = getRandomData(16);
		printStat(vector);
		vector = transform5(vector);
		printStat(vector);
	}

	public static float[] transform2(float[] vector) {
		vector = addZeros(vector, 32);
		fft32.complexInverse(vector, false);
		scaleVector(vector, (float) (1.0 / Math.sqrt(vector.length / 2)));
		return vector;
	}

	public static void main2(String[] args) {
		int length = 8;
		int[] histogram = new int[1000];
		List<float[]> codes = getAllData(length);

		float[] xcode = codes.get(10000);
		printVector(xcode);
		printVector(xcode);

		double averageCrest = 0.0;
		for (float[] code : codes) {
			code = transform1(code);
			double maxAmplitudo = getMaxAmplitudo(code);
			double averageAmpl = Math.sqrt(getAveragePower(code));
			double crestFactor = maxAmplitudo / averageAmpl;
			averageCrest += crestFactor;
			histogram[(int) (maxAmplitudo / averageAmpl * 10.0)] += 1;
		}

		int c = 0;
		for (int i = 0; c < codes.size(); ++i) {
			c += histogram[i];
			if (c != 0)
				System.out.println("" + i + ": " + histogram[i]);
		}
		System.out.println("Average Crest Factor = "
				+ (averageCrest / codes.size()));
	}
}
