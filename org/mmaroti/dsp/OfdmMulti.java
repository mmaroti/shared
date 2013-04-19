package org.mmaroti.dsp;

import java.text.DecimalFormat;
import java.util.*;
import edu.emory.mathcs.jtransforms.fft.*;

public class OfdmMulti {

	public static double getAveragePower(double[] code) {
		double sum = 0.0;
		for (int i = 0; i < code.length; ++i)
			sum += code[i] * code[i];

		sum /= (code.length / 2);
		return sum;
	}

	public static List<double[]> filterByMinPower(List<double[]> codes,
			double minPower) {
		ArrayList<double[]> list = new ArrayList<double[]>();
		for (double[] code : codes) {
			if (getAveragePower(code) >= minPower)
				list.add(code);
		}
		return list;
	}

	public static double getMaxAmpl(double[] signal) {
		double max = 0.0;

		for (int i = 0; i < signal.length; ++i) {
			if (signal[i] > max)
				max = signal[i];
			else if (-signal[i] > max)
				max = -signal[i];
		}

		return max;
	}

	public static List<double[]> filterByMaxAmpl(List<double[]> codes,
			double maxAmpl) {
		ArrayList<double[]> list = new ArrayList<double[]>();
		for (double[] code : codes) {
			if (getMaxAmpl(code) <= maxAmpl)
				list.add(code);
		}
		return list;
	}

	public static List<double[]> mapIfft(List<double[]> codes) {
		if (codes.size() == 0)
			return codes;

		int length = codes.get(0).length;
		DoubleFFT_1D fft = new DoubleFFT_1D(length / 2);
		double scale = 1.0 / Math.sqrt(length / 2);

		ArrayList<double[]> list = new ArrayList<double[]>();
		for (double[] code : codes) {
			double[] d = code.clone();
			fft.complexInverse(d, false);

			for (int i = 0; i < length; ++i)
				d[i] *= scale;

			list.add(d);
		}

		return list;
	}

	public static List<double[]> mapFft(List<double[]> codes) {
		if (codes.size() == 0)
			return codes;

		int length = codes.get(0).length;
		DoubleFFT_1D fft = new DoubleFFT_1D(length / 2);
		double scale = 1.0 / Math.sqrt(length / 2);

		ArrayList<double[]> list = new ArrayList<double[]>();
		for (double[] code : codes) {
			double[] d = code.clone();
			fft.complexForward(d);

			for (int i = 0; i < length; ++i)
				d[i] *= scale;

			list.add(d);
		}

		return list;
	}

	public static List<double[]> generate(boolean[] channels, int maxValue) {
		ArrayList<double[]> list = new ArrayList<double[]>();

		int length2 = channels.length * 2;
		boolean[] channels2 = new boolean[length2];
		for (int i = 0; i < channels.length; ++i) {
			channels2[2 * i] = channels[i];
			channels2[2 * i + 1] = channels[i];
		}

		double[] code2 = new double[length2];
		for (int i = 0; i < length2; ++i)
			code2[i] = channels2[i] ? -maxValue : 0;

		outer: for (;;) {
			list.add(code2.clone());

			for (int i = 0; i < length2; ++i) {
				if (channels2[i]) {
					code2[i] += 1.0;
					if (code2[i] > maxValue) {
						code2[i] = -maxValue;
					} else
						continue outer;
				}
			}
			break;
		}

		return list;
	}

	private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

	public static void print(double[] code) {
		String s = "[";
		for (int i = 0; i < code.length; i += 2) {
			if (i != 0)
				s += ",";

			s += FORMAT.format(code[i]);

			if (code[i + 1] >= 0.0)
				s += "+";

			s += FORMAT.format(code[i + 1]) + "j";
		}
		s += "]";
		System.out.println(s);
	}

	public static void print(List<double[]> codes) {
		for (double[] code : codes) {
			System.out.print(getAveragePower(code) + " ");
			print(code);
		}
	}

	public static void main(String[] args) {
		boolean[] channels = new boolean[32];
		channels[0] = true;
		channels[1] = true;
		channels[2] = false;
		channels[channels.length-1] = true;
		channels[channels.length-2] = false;
		List<double[]> codes = generate(channels, 4);
		codes = mapIfft(codes); 
		codes = filterByMaxAmpl(codes, 1.01);
		codes = filterByMinPower(codes, 0.9);
		codes = mapFft(codes);
		print(codes);
		System.out.println(codes.size());
	}
}
