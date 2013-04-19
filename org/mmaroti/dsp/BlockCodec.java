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

import java.util.*;
import java.text.*;
import edu.emory.mathcs.jtransforms.fft.*;

public abstract class BlockCodec {
	public final String name;
	public final int dataLength;
	public final int codeLength;

	public BlockCodec(String name, int dataLength, int codeLength) {
		this.name = name;
		this.dataLength = dataLength;
		this.codeLength = codeLength;
	}

	public abstract double[] encode(boolean[] data);

	public abstract boolean[] decode(double[] code);

	protected final static Random RANDOM = new Random();

	public boolean[] getRandomData() {
		boolean[] data = new boolean[dataLength];

		for (int i = 0; i < dataLength; ++i) {
			data[i] = RANDOM.nextBoolean();
		}

		return data;
	}

	protected double SQRT2 = Math.sqrt(2.0);
	protected double LOG2 = Math.log(2.0);

	public double[] getWhiteNoise(double power) {
		power /= SQRT2;
		double[] noise = new double[2 * codeLength];

		for (int i = 0; i < noise.length; ++i)
			noise[i] = power * RANDOM.nextGaussian();

		return noise;
	}

	public static double[] addSignals(double[] code, double[] noise) {
		double signal[] = new double[code.length];
		for (int i = 0; i < code.length; ++i)
			signal[i] = code[i] + noise[i];

		return signal;
	}

	public static void scaleSignal(double[] signal, double scale) {
		for(int i = 0; i < signal.length; ++i)
			signal[i] *= scale;
	}
	
	public static void clipSignal(double[] signal, double clip) {
		double nclip = -clip;
		for(int i = 0; i < signal.length; ++i) {
			if( signal[i] > clip )
				signal[i] = clip;
			else if( signal[i] < nclip )
				signal[i] = nclip;
		}
	}
	
	public double getAveragePower(double[] code) {
		assert (code.length == 2 * codeLength);

		double sum = 0.0;
		for (int i = 0; i < code.length; ++i)
			sum += code[i] * code[i];

		sum /= (code.length / 2);
		return sum;
	}

	public double getPeakPower(double[] code) {
		assert (code.length == 2 * codeLength);

		double max = 0.0;
		for (int i = 0; i < code.length; i += 2) {
			double v = code[i] * code[i] + code[i + 1] * code[i + 1];
			if (v > max)
				max = v;
		}

		return max;
	}

	public int getErrorCount(boolean[] data, boolean[] decoded) {
		assert (data.length == dataLength && decoded.length == dataLength);

		int c = 0;
		for (int i = 0; i < dataLength; ++i) {
			if (data[i] != decoded[i])
				c += 1;
		}

		return c;
	}

	protected final static DecimalFormat FORMAT = new DecimalFormat("0.00");

	protected double getBandwidthPerSample() {
		return 1.0 * dataLength / codeLength;
	}

	protected double getCapacityPerSample(double ber) {
		double h2 = 1.0;
		if (0.0 < ber && ber < 1.0) {
			h2 += ber * Math.log(ber) / LOG2;
			h2 += (1.0 - ber) * Math.log(1.0 - ber) / LOG2;
		}
		return getBandwidthPerSample() * h2;
	}

	protected double getShannonLimit(double snr) {
		return getBandwidthPerSample() * Math.log(1.0 + snr) / LOG2;
	}

	public void simulate(double noisePower) {
		int rounds = 1000000 / dataLength;

		double averageSignalPower = 0.0;
		double peakSignalPower = 0.0;
		int errorCount = 0;

		for (int i = 0; i < rounds; ++i) {
			boolean[] data = getRandomData();
			double[] code = encode(data);
			double[] noise = getWhiteNoise(noisePower);

			averageSignalPower += getAveragePower(code);
			peakSignalPower = Math.max(peakSignalPower, getPeakPower(code));

			double[] signal = addSignals(code, noise);
			errorCount += getErrorCount(data, decode(signal));
		}

		averageSignalPower /= rounds;
		double bitErrorRate = 1.0 * errorCount / (rounds * dataLength);

		String s = name;
		s += " rate=" + dataLength + "/" + codeLength;
		// s += " S_dB=" + FORMAT.format(10.0 * Math.log10(averageSignalPower));
		// s += " P_dB=" + FORMAT.format(10.0 * Math.log10(peakSignalPower));
		s += " N0_dB=" + FORMAT.format(10.0 * Math.log10(noisePower));
		s += " SNR_dB="
				+ FORMAT.format(10.0 * Math.log10(averageSignalPower
						/ noisePower));
		s += " PSNR_dB="
				+ FORMAT.format(10.0 * Math.log10(peakSignalPower / noisePower));
		s += " SbN0_dB="
				+ FORMAT.format(10.0 * Math.log10(averageSignalPower
						* codeLength / dataLength / noisePower));
		s += " BER_log=" + FORMAT.format(Math.log10(bitErrorRate));
		s += " CPS=" + FORMAT.format(getCapacityPerSample(bitErrorRate));
		s += " SL="
				+ FORMAT.format(getShannonLimit(averageSignalPower / noisePower));
		System.out.println(s);
	}

	public void simulate() {
		for(double n0 = 6.0; n0 >= -6.0; n0 -= 3.0)
			simulate(Math.pow(10.0, n0/10.0));
	}

	public static final BlockCodec BPSK = new BlockCodec("BPSK", 1, 1) {
		public double[] encode(boolean[] data) {
			double[] code = new double[2];

			code[0] = data[0] ? 1.0 : -1.0;
			code[1] = data[0] ? 1.0 : -1.0;

			return code;
		}

		public boolean[] decode(double[] code) {
			boolean[] data = new boolean[1];

			data[0] = code[0] + code[1] > 0.0;

			return data;
		}
	};
	
	public static final BlockCodec QPSK = new BlockCodec("QPSK", 2, 1) {
		public double[] encode(boolean[] data) {
			double[] code = new double[2];

			code[0] = data[0] ? 1.0 : -1.0;
			code[1] = data[1] ? 1.0 : -1.0;

			return code;
		}

		public boolean[] decode(double[] code) {
			boolean[] data = new boolean[2];

			data[0] = code[0] > 0.0;
			data[1] = code[1] > 0.0;

			return data;
		}
	};
	
	public static final BlockCodec QPSK2 = new BlockCodec("QPSK2", 2, 2) {
		public double[] encode(boolean[] data) {
			double[] code = new double[4];

			code[0] = data[0] ? 1.0 : -1.0;
			code[1] = data[1] ? 1.0 : -1.0;
			code[2] = code[0];
			code[3] = code[1];

			return code;
		}

		public boolean[] decode(double[] code) {
			boolean[] data = new boolean[2];

			data[0] = code[0] + code[2] > 0.0;
			data[1] = code[1] + code[3] > 0.0;

			return data;
		}
	};
	
	public static DoubleFFT_1D fft8 = new DoubleFFT_1D(8);
	public static DoubleFFT_1D fft16 = new DoubleFFT_1D(16);
	public static DoubleFFT_1D fft32 = new DoubleFFT_1D(32);
	public static DoubleFFT_1D fft64 = new DoubleFFT_1D(32);

	public static void fftForward(double[] vector) {
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

		scaleSignal(vector, 1.0 / Math.sqrt(vector.length / 2));
	}

	public static void fftReverse(double[] vector) {
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

		scaleSignal(vector, 1.0 / Math.sqrt(vector.length / 2));
	}

	public static final BlockCodec OFDM = new BlockCodec("OFDM", 32, 16) {
		public double[] encode(boolean[] data) {
			double[] code = new double[data.length];

			for(int i = 0; i < data.length; ++i)
				code[i] = data[i] ? 1.0 : -1.0;

			fftReverse(code);
			clipSignal(code, 1.0);

			return code;
		}

		public boolean[] decode(double[] code) {
			fftForward(code);

			boolean[] data = new boolean[code.length];
			for(int i = 0; i < data.length; ++i)
				data[i] = code[i] > 0.0;

			return data;
		}
	};
	
	public static final BlockCodec OFDM2 = new BlockCodec("OFDM2", 128, 64) {
		public double[] encode(boolean[] data) {
			double[] code = new double[data.length];

			for(int i = 0; i < data.length; ++i)
				code[i] = data[i] ? 1.0 : -1.0;

			fftReverse(code);
			scaleSignal(code, 10.0);
			clipSignal(code, 1.0);

			return code;
		}

		public boolean[] decode(double[] code) {

			fftForward(code);

			boolean[] data = new boolean[code.length];
			for(int i = 0; i < data.length; ++i)
				data[i] = code[i] > 0.0;

			return data;
		}
	};
	
	public static void main(String[] args) {
		BPSK.simulate();
		QPSK.simulate();
		QPSK2.simulate();
//		OFDM.simulate();
//		OFDM2.simulate();
	}
}
