/*
 * Created on December 2, 2006
 * (C)opyright Miklos Maroti
 */
package org.mmaroti.rips.mp;

import org.mmaroti.rips.*;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class PathComponent
{	
	/**
	 * The transmitter for this path component
	 */
	public Transmitter transmitter;
	
	/**
	 * This is the apparent position of the transmitter.
	 * Instead of calculating reflections, we
	 * reflect the real location of the transmitter. 
	 */
	public Position position;
	
	/**
	 * The attenuation of the path component in dB
	 */
	public double attenuation;

	/**
	 * Additional phase shift;
	 */
	public double phaseShift;

	public PathComponent(Transmitter transmitter, Position position, 
		double attenuation, double phaseShift)
	{
		this.transmitter = transmitter;
		this.position = position;
		this.attenuation = attenuation;
		this.phaseShift = phaseShift;
	}

	public int groundReflections;
		
	/**
	 * Adds the received signal component calculated at the specified location
	 * to the given signal
	 */	
	public void addSignalComponent(Position pos, ComplexNumber signal)
	{
		double distance = position.getDistance(pos);

		double amplitude = RadioModel.getAmplitude(
			transmitter.signalStrength - attenuation - RadioModel.getAttenuation(distance));

		// modify it with the radiation intensity of the dipole antenna, supposing vertical polarization
		amplitude *= Math.sqrt((position.x - pos.x)*(position.x - pos.x) + (position.y - pos.y)*(position.y - pos.y))
			/ distance;

		if( groundReflections > 0 )
		{
			double coefficient = RadioModel.getReflectedAmplitudeCoeficient(Math.abs(position.z - pos.z) / distance);
			for(int i = 0; i < groundReflections; ++i)
				amplitude *= coefficient;
		}

		double phase = transmitter.getPhase(distance) + phaseShift;

		signal.addTrigForm(amplitude, phase);
	}
}
