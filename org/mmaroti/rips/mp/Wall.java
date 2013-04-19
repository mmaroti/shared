/*
 * Created on December 2, 2006
 * (C)opyright Miklos Maroti
 */
package org.mmaroti.rips.mp;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class Wall
{
	public Wall(double coord, double attenuation)
	{
		this.coord = coord;
		this.attenuation = attenuation;
	}
	
	/**
	 * The single important coordinate of the wall
	 */
	public double coord;
	
	/**
	 * The extra attenuation incured when signal is
	 * reflected from this wall.
	 */
	public double attenuation;
}
