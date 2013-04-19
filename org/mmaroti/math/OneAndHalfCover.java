/**
 * Copyright (C) Miklos Maroti, 2013
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

package org.mmaroti.math;

import java.util.*;

public class OneAndHalfCover {
	static class Point {
		public Point(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final double x, y, z;
	}

	public static class Edge {
		public Edge(Point first, Point second) {
			this.first = first;
			this.second = second;
		}

		public final Point first, second;
	};

	public static class Face {
		public Face(Point first, Point second, Point third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public final Point first, second, third;
	};

	public static class Complex {
		public Complex(List<Point> points, List<Face> faces) {
			this.points = points;
			this.faces = faces;
		}

		public final List<Point> points;
		public final List<Face> faces;
	};

	public static Complex sphere() {
		Point top = new Point(0.0, 0.0, 1.0);
		Point bottom = new Point(0.0, 0.0, -1.0);
		Point left = new Point(-1.0, 0.0, 0.0);
		Point right = new Point(1.0, 0.0, 0.0);
		Point front = new Point(0.0, 1.0, 0.0);
		Point back = new Point(0.0, -1.0, 0.0);

		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Face> faces = new ArrayList<Face>();

		points.add(top);
		points.add(bottom);
		points.add(left);
		points.add(right);
		points.add(front);
		points.add(back);

		faces.add(new Face(top, left, front));
		faces.add(new Face(top, right, front));
		faces.add(new Face(top, left, back));
		faces.add(new Face(top, right, back));
		faces.add(new Face(bottom, left, front));
		faces.add(new Face(bottom, right, front));
		faces.add(new Face(bottom, left, back));
		faces.add(new Face(bottom, right, back));

		return new Complex(points, faces);
	}

	public static void print(Complex complex) {
		System.out.println("Complex with " + complex.points.size()
				+ " points and " + complex.faces.size() + " faces");

		for (int i = 0; i < complex.points.size(); ++i) {
			Point point = complex.points.get(i);
			System.out.println("point " + i + ":\t" + point.x + "\t" + point.y
					+ "\t" + point.z);
		}

		for (int i = 0; i < complex.faces.size(); ++i) {
			Face face = complex.faces.get(i);

			int[] indices = new int[3];
			indices[0] = complex.points.indexOf(face.first);
			indices[1] = complex.points.indexOf(face.second);
			indices[2] = complex.points.indexOf(face.third);
			Arrays.sort(indices);

			System.out.println("face " + i + ": \t" + indices[0] + "\t"
					+ indices[1] + "\t" + indices[2]);
		}
	}

	public static void main(String[] args) {
		Complex sphere = sphere();
		print(sphere);
	}
}
