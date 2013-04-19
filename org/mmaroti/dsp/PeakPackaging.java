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

import java.text.*;
import java.util.*;
import edu.emory.mathcs.jtransforms.fft.*;

public class PeakPackaging {
	public final int dataLength;
	public final int codeLength;
	public final boolean[] channels;
	public final double minPowerLimit, maxPowerLimit;
	public final DoubleFFT_1D fft;

	public PeakPackaging(int dataLength, int codeLength, boolean dataZero,
			double minPowerLimit, double maxPowerLimit) {
		this.dataLength = dataLength;
		this.codeLength = codeLength;
		this.channels = new boolean[codeLength];
		this.minPowerLimit = minPowerLimit;
		this.maxPowerLimit = maxPowerLimit;
		this.fft = new DoubleFFT_1D(codeLength);

		if (dataZero) {
			for (int i = 0; i < (dataLength + 1) / 2; ++i)
				channels[i] = true;
		} else {
			for (int i = 1; i < (dataLength + 3) / 2; ++i)
				channels[i] = true;
		}
		for (int i = codeLength - dataLength / 2; i < codeLength; ++i)
			channels[i] = true;

		System.out.print("OFDM " + dataLength + "/" + codeLength
				+ " at channels");
		for (int i = 0; i < codeLength; ++i) {
			if (channels[i])
				System.out.print(" " + i);
		}
		System.out.println(" power limits " + minPowerLimit + " and "
				+ maxPowerLimit);
	}

	public double[] getFirstQPSKCode() {
		double[] code = new double[codeLength * 2];
		for (int i = 0; i < code.length; ++i)
			code[i] = -1.0;
		return code;
	}

	public boolean getNextQPSKCode(double[] code) {
		assert (code.length == 2 * codeLength);

		for (int i = 0; i < code.length; ++i) {
			if (code[i] < 0.0) {
				code[i] = 1.0;
				return true;
			} else
				code[i] = -1.0;
		}

		return false;
	}

	public double[] getFirst9AMCode() {
		double[] code = new double[codeLength * 2];
		for (int i = 0; i < code.length; ++i)
			code[i] = -1.0;
		return code;
	}

	public boolean getNext9AMCode(double[] code) {
		assert (code.length == 2 * codeLength);

		for (int i = 0; i < code.length; ++i) {
			if (code[i] < -0.5) {
				code[i] = 0.0;
				return true;
			} else if (code[i] < 0.5) {
				code[i] = 1.0;
				return true;
			} else
				code[i] = -1.0;
		}

		return false;
	}

	public void generateQPSKCodewords() {
		double scale = 1.0 / codeLength;
		double[] code = getFirstQPSKCode();

		do {
			double[] vector = code.clone();
			fft.complexForward(vector);
			if (checkPowerLevels(vector, scale)) {
				codeWords.add(code.clone());
			}
		} while (getNextQPSKCode(code));
	}

	public void generate9AMCodewords() {
		double scale = 1.0 / codeLength;
		double[] code = getFirst9AMCode();

		do {
			double[] vector = code.clone();
			fft.complexForward(vector);
			if (checkPowerLevels(vector, scale)) {
				codeWords.add(code.clone());
			}
		} while (getNext9AMCode(code));
	}

	double maxPowerReached = Double.MAX_VALUE;
	double minPowerReached = 0.0;

	public boolean checkPowerLevels(double[] code, double scale) {
		double maxPower = 0.0;
		double minPower = 0.0;

		for (int i = 0; i < codeLength; ++i) {
			double p = code[2 * i] * code[2 * i] + code[2 * i + 1]
					* code[2 * i + 1];
			if (channels[i])
				maxPower += p;
			else
				minPower += p;
		}

		maxPower *= scale / dataLength;
		minPower *= scale / (codeLength - dataLength);

		boolean good = maxPower >= maxPowerLimit && minPower <= minPowerLimit;
		if (good) {
			if (maxPower < maxPowerReached)
				maxPowerReached = maxPower;
			if (minPower > minPowerReached)
				minPowerReached = minPower;
		}
		return good;
	}

	public static void printCode(double[] code) {
		String s = "";
		for (int i = 0; i < code.length; ++i) {
			s += code[i] + " ";
		}
		System.out.println(s);
	}

	List<double[]> codeWords = new ArrayList<double[]>();

	public double getDistance(double[] first, double[] second) {
		double sum = 0.0;
		for (int i = 0; i < first.length; ++i) {
			double d = first[i] - second[i];
			sum += d * d;
		}
		// sum /= codeLength;
		return sum;
		// return Math.sqrt(sum);
	}

	public double getMinimumDistance() {
		double min = Double.MAX_VALUE;
		for (double[] first : codeWords) {
			for (double[] second : codeWords) {
				if (first != second) {
					double d = getDistance(first, second);
					if (min > d)
						min = d;
				}
			}
		}
		return min;
	}

	public void printSmallWords(double distance) {
		double[] origin = new double[codeLength * 2];
		for (int i = 0; i < origin.length; ++i)
			origin[i] = 1.0;

		for (double[] second : codeWords) {
			double d = getDistance(origin, second);
			if (d <= distance)
				System.out.println("[" + codeToString(second) + "]");
		}
	}

	private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
	
	public String codeToString(double[] code) {
		String s = "";
		for (int j = 0; j < codeLength; ++j) {
			if (s.length() != 0)
				s += ",";

			if (code[2 * j + 1] < 0.0)
				s += FORMAT.format(code[2 * j]) + "" + FORMAT.format(code[2 * j + 1]) + "j";
			else
				s += FORMAT.format(code[2 * j]) + "+" + FORMAT.format(code[2 * j + 1]) + "j";
		}
		return s;
	}

	public void printGnuRadioConstellation(int num) {
		if (num <= 0)
			num = codeWords.size();

		System.out.print("[");
		for (int i = 0; i < num; ++i) {
			if (i != 0)
				System.out.println(",");

			System.out.print(codeToString(codeWords.get(i)));
		}
		System.out.println("]");
	}

	public void test() {
		generateQPSKCodewords();

		System.out.print("generated " + codeWords.size() + " codes ");
		System.out.print("with powers " + minPowerReached + " "
				+ maxPowerReached);
		double min = getMinimumDistance();
		System.out.println(" and distance " + min);
		System.out.println("small words:");
		printSmallWords(min);
	}

	public static void main(String[] args) {
		PeakPackaging packaging = new PeakPackaging(10, 16, true, 0.01, 4.0);
		packaging.test();

		//packaging.printGnuRadioConstellation(0);
	}
}
