/*
 * Created on December 2, 2006
 * (C)opyright Miklos Maroti
 */
package org.mmaroti.rips.mp;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class RadioModel
{
	/**
	 * Speed of light in meters per seconds
	 */
	public static final double SPEED_OF_LIGHT  = 299792458.0;

	/**
	 * Twice PI
	 */
	public static final double TWO_PI  = 2.0 * Math.PI;
	
	/**
	 * The maximum signal strength difference between the master
	 * and slave composite signal components for RIPS to be able
	 * to measure the phase difference
	 */
	public static final double RIPS_MAX_DB = 200.0;
	
	private static final double TWENTY_OVER_LOG_10 = 20.0 / Math.log(10.0);

	/**
	 * Converts the signal amplitude of the radio wave 
	 * to signal strength expressed in dB
	 */
	public static double getStrength(double amplitude)
	{
		return Math.log(amplitude) * TWENTY_OVER_LOG_10;
	}

	private static final double LOG_10_OVER_TWENTY = Math.log(10.0) / 20.0;

	/**
	 * Converts the signal strength expressed in dB to signal amplitude
	 */
	public static double getAmplitude(double strength)
	{
		return Math.exp(strength * LOG_10_OVER_TWENTY);
	}

	private static double ATTENUATION_BETA = 2.3 * 10.0 / Math.log(10.0);

	/**
	 * Returns the signal strength attenuation at the given distance.
	 * Note, that at 1 meter distance there is no attenuation.
	 */
	public static double getAttenuation(double distance)
	{
		if( distance <= 0 )
			return Double.MIN_VALUE;
		
		return ATTENUATION_BETA * Math.log(distance);
	}
	
	/**
	 * Returns the reflected amplitude coeficient, a number
	 * between -1 and 1, that is used to multiply the amplitude width.
	 * Negative value means a phase shift of 180 degrees.
	 * The sine parameter is the sine of the angle of incidence, 
	 * 0 beeing parrallel to the surfeace, and 1 beeing perpendicular 
	 */
	public static double getReflectedAmplitudeCoeficient(double sine)
	{
		double sine2 = sine * sine;
		double sine3 = sine2 * sine;
		return 1.0 - 7.0 * sine + 10.0 * sine2 - 5.0 * sine3; 
	}
}
