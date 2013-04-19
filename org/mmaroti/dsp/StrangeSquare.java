package org.mmaroti.dsp;

import java.text.DecimalFormat;
import javax.swing.*;
import org.math.plot.*;

public class StrangeSquare {
	public static int steps = 10;

	public static double[] getHalfSegment(double x0, double x1) {
		double[] s = new double[steps];

		if (x0 == x1) {
			for (int i = 0; i < steps; ++i)
				s[i] = x0;
		} else if (x0 == 0.0 && Math.abs(x1) == 1.0) {
			for (int i = 0; i < steps; ++i)
				s[i] = x1 * Math.sin(Math.PI * 0.5 * i / steps);
		} else if (Math.abs(x0) == 1.0 && x1 == 0.0) {
			for (int i = 0; i < steps; ++i)
				s[i] = x0 * Math.cos(Math.PI * 0.5 * i / steps);
		} else
			throw new IllegalArgumentException();

		return s;
	}

	public static double[] getSegment(double x0, double y0, double x1, double y1) {
		double[] x = getHalfSegment(x0, x1);
		double[] y = getHalfSegment(y0, y1);

		double[] s = new double[2 * steps];
		for (int i = 0; i < steps; ++i) {
			s[2 * i] = x[i];
			s[2 * i + 1] = y[i];
		}

		return s;
	}

	public static double[] concatenate(double[] first, double[] second) {
		double[] s = new double[first.length + second.length];

		System.arraycopy(first, 0, s, 0, first.length);
		System.arraycopy(second, 0, s, first.length, second.length);

		return s;
	}

	double[][] segments = new double[][] { getSegment(0, 1, 1, 1),
			getSegment(1, 1, 1, 0), getSegment(1, 0, 1, -1),
			getSegment(1, -1, 0, -1), getSegment(0, -1, -1, -1),
			getSegment(-1, -1, -1, 0), getSegment(-1, 0, -1, 1),
			getSegment(-1, 1, 0, 1), getSegment(0, 1, 1, 0),
			getSegment(1, 0, 0, -1), getSegment(0, -1, -1, 0),
			getSegment(-1, 0, 0, 1) };

	int[] connectivity = new int[] { 1, 12, 2, 9, 3, 14, 4, 10, 5, 16, 6, 11,
			7, 18, 8, 0, 9, 2, 10, 4, 11, 6, 8, 0 };

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

	public static void plot(double[] path) {
		// define your data
		double[] x = new double[path.length / 2];
		double[] y = new double[path.length / 2];

		for (int i = 0; i < x.length; ++i) {
			x[i] = path[2 * i] + i * 0.001 ;
			y[i] = path[2 * i + 1] + i * 0.001;
		}

		// create your PlotPanel (you can use it as a JPanel)
		Plot2DPanel plot = new Plot2DPanel();

		// define the legend position
		plot.addLegend("SOUTH");

		// add a line plot to the PlotPanel
		plot.addLinePlot("my plot", x, y);

		// put the PlotPanel in a JFrame like a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	public static void main(String[] args) {
		double[] s = concatenate(concatenate(getSegment(0, 1, 1, 0),
				getSegment(1, 0, 1, -1)), getSegment(1,-1,1,0));
		
		plot(s);

	}
}
