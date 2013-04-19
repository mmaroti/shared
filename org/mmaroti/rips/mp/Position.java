/*
 * Created on December 2, 2006
 * (C)opyright Miklos Maroti
 */
package org.mmaroti.rips.mp;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class Position
{
	public double x;
	public double y;
	public double z;

	public Position()
	{
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}

	public void set(Position pos)
	{
		x = pos.x;
		y = pos.y;
		z = pos.z;
	}

	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")";
	}

	public Position(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getDistance(Position p)
	{
		return Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) + (p.z-z)*(p.z-z));
	}
}
