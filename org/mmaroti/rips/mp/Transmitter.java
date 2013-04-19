/*
 * Created on December 2, 2006
 * (C)opyright
 */
package org.mmaroti.rips.mp;

/**
 * @author mmaroti2
 */
public class Transmitter
{
	/**
	 * Constructs a transmitter with the given frequency and signal strength
	 */
	public Transmitter(double frequency, double strength)
	{
		setFrequency(frequency);
		signalStrength = strength;
	}
	
	private double quasiFrequency;

	public final static double TWO_PI_OVER_LIGHT = 2.0 * Math.PI / RadioModel.SPEED_OF_LIGHT; 

	/**
	 * Sets the transmit frequency given in Hz
	 */
	public void setFrequency(double frequency)
	{
		quasiFrequency = frequency * TWO_PI_OVER_LIGHT; 
	}

	/**
	 * Returns the transmit frequency in Hz
	 */
	public double getFrequency()
	{
		return quasiFrequency / TWO_PI_OVER_LIGHT;
	}

	/**
	 * Returns the wave length in meters
	 */
	public double getWaveLength()
	{
		return RadioModel.TWO_PI / quasiFrequency;
	}

	/**
	 * This is a random phase offset modelling that the transmitters
	 * cannot be turned on at the same time
	 */
	public double extraPhase = 0.0;
//	public double extraPhase = 2.0 * Math.PI * Math.random();

	/**
	 * Returns the phase of the signal at the given distance
	 */
	public double getPhase(double distance)
	{
		return quasiFrequency * distance + extraPhase;
	}

	/**
	 * The signal strength of the transmission (in dB)
	 */
	public double signalStrength;
}
