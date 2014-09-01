/*
 * Created on December 2, 2006
 * (C)opyright Miklos Maroti
 */
package org.mmaroti.rips.mp;

import java.util.*;

import org.mmaroti.rips.*;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class Room
{
	/**
	 * The first transmitter
	 */
	public Transmitter master;
	
	/**
	 * The second transmitter
	 */
	public Transmitter assistant;
	
	/**
	 * The master path components
	 */
	public PathComponent[] masterComponents;

	/**
	 * The assistant path components
	 */
	public PathComponent[] assistantComponents;

	private ComplexNumber masterSignal = new ComplexNumber(0.0, 0.0);
	
	/**
	 * Returns the composite signal coming from the master
	 */
	public ComplexNumber getMasterSignal(Position pos)
	{
		masterSignal.set(0.0, 0.0);
		
		for(int i = 0; i < masterComponents.length; ++i)
			masterComponents[i].addSignalComponent(pos, masterSignal);
			
		return masterSignal;
	}

	private ComplexNumber assistantSignal = new ComplexNumber(0.0, 0.0);
	
	/**
	 * Returns the composite signal coming from the master
	 */
	public ComplexNumber getAssistantSignal(Position pos)
	{
		assistantSignal.set(0.0, 0.0);
		
		for(int i = 0; i < masterComponents.length; ++i)
			assistantComponents[i].addSignalComponent(pos, assistantSignal);
			
		return assistantSignal;
	}

	private ComplexNumber interferenceSignal = new ComplexNumber(0.0, 0.0);

	/**
	 * Returns the interference signal, the phase is the measured 
	 * RIPS phase, the amplitude is the quotient of the two amplitudes
	 * from the two transmitters
	 */
	public ComplexNumber getInterferenceSignal(Position pos)
	{
		interferenceSignal.set(getMasterSignal(pos));
		interferenceSignal.divide(getAssistantSignal(pos));
		
		return interferenceSignal;
	}

	private ComplexNumber interferenceVector = new ComplexNumber(0.0, 0.0);
	private double interferenceVectorDelta = 0.05;

	public ComplexNumber getInterferenceVector(Position pos)
	{
		double orig = getInterferenceSignal(pos).getComplexArgument();

		pos.x += interferenceVectorDelta;
		double xoff = getInterferenceSignal(pos).getComplexArgument();
		pos.x -= interferenceVectorDelta;
		
		pos.y += interferenceVectorDelta;
		double yoff = getInterferenceSignal(pos).getComplexArgument();
		pos.y -= interferenceVectorDelta;
		
		xoff -= orig;
		if( xoff > Math.PI )
			xoff -= RadioModel.TWO_PI;
		else if( xoff < -Math.PI )
			xoff += RadioModel.TWO_PI;

		yoff -= orig;
		if( yoff > Math.PI )
			yoff -= RadioModel.TWO_PI;
		else if( yoff < -Math.PI )
			yoff += RadioModel.TWO_PI;

		interferenceVector.x = xoff;
		interferenceVector.y = yoff;		
		return interferenceVector;
	}

	public static final double CHANNEL_FREQ = 526628.5; 

	public ComplexNumber getAveragedInterferenceVector(Position pos)
	{
		ComplexNumber average = new ComplexNumber(0.0, 0.0);
		double origMaster = master.getFrequency();
		double origAssitant = assistant.getFrequency(); 
		
		for(int i = -40; i <= 40; ++i)
		{
			master.setFrequency(origMaster + i * CHANNEL_FREQ);
			assistant.setFrequency(origAssitant + i * CHANNEL_FREQ);
			average.add(getInterferenceVector(pos));
		}

		master.setFrequency(origMaster);
		assistant.setFrequency(origAssitant);
		
		average.x /= 81;
		average.y /= 81;
		
		return average;
	}

	public Wall east	= new Wall(6.0, 7.0);		// greater than west
	public Wall west	= new Wall(0.0, 7.0);
	public Wall north	= new Wall(5.0, 7.0);		// greater than south
	public Wall south	= new Wall(0.0, 7.0);
	public Wall top 	= new Wall(3.0, 7.0);		// greater than bottom
	public Wall bottom	= new Wall(0.0, 4.0);

	List<PathComponent> components = new ArrayList<PathComponent>();

	public void addRoomComponents(Transmitter transmitter, Position position, int reflections)
	{
		for(int i = -reflections; i <= reflections; ++i)
		{
			int e = (i+1) >> 1;
			int w = i >> 1;

			for(int j = -reflections; j <= reflections; ++j)
			{			
				int n = (j+1) >> 1;
				int s = j >> 1;

				for(int k = -reflections; k <= reflections; ++k)
				{		
					int t = (k+1) >> 1;
					int b = k >> 1;

					if( Math.abs(i) + Math.abs(j) + Math.abs(k) > reflections )
						continue;

					double x = position.x + 2.0 * (e * (east.coord - position.x) + w * (position.x - west.coord));
					double y = position.y + 2.0 * (n * (north.coord - position.y) + s * (position.y - south.coord));
					double z = position.z + 2.0 * (t * (top.coord - position.z) + b * (position.z - bottom.coord));

					double attenuation = Math.abs(e * east.attenuation + w * west.attenuation)
						+ Math.abs(n * north.attenuation + s * south.attenuation)
						+ Math.abs(t * top.attenuation + b * bottom.attenuation);
					
					double phaseShift = (i + j) % 2 == 0 ? 0.0 : Math.PI;
	
					PathComponent component = new PathComponent(transmitter, new Position(x,y,z), 
						attenuation, phaseShift);
					component.groundReflections = k >= 0 ? k : -k;
					
					components.add(component);
				}
			}
		}
	}

	/**
	 * Moves the created components from the list to the array of master and
	 * assistant components
	 */
	public void packComponents()
	{
		int m = 0;
		int a = 0;
		
		Iterator<PathComponent> iter = components.iterator();
		while( iter.hasNext() )
		{
			PathComponent component = iter.next();
			
			if( component.transmitter == master )
				++m;
			else if( component.transmitter == assistant )
				++a;
		}
		
		masterComponents = new PathComponent[m];
		assistantComponents = new PathComponent[a];
		
		m = 0;
		a = 0;
		
		iter = components.iterator();
		while( iter.hasNext() )
		{
			PathComponent component = iter.next();
			
			if( component.transmitter == master )
				masterComponents[m++] = component;
			else if( component.transmitter == assistant )
				assistantComponents[a++] = component;
		}
	}

	/**
	 * The environment identifier that is displayed in the title 
	 */
	public String identifier;
	
	public void setup1()
	{
		identifier = "0";
		
		double freq =  430000000.0;
		master = new Transmitter(freq, 0.0);
		assistant = new Transmitter(freq + 1000.0, 0.0);
		interferenceVectorDelta = master.getWaveLength() / 8.0;

		components.clear();
		addRoomComponents(master, new Position(0.1, 0.1, 0.5), 3);
		addRoomComponents(assistant, new Position(5.9, 4.9, 0.5), 3);
		packComponents();

		canvas.setDefaultCenter();		
		canvas.center.z = 0.5;
	}

	public void setup2()
	{
		identifier = "4";
		
		double freq =  430000000.0;
		master = new Transmitter(freq, 0.0);
		assistant = new Transmitter(freq + 1000.0, 0.0);
		interferenceVectorDelta = master.getWaveLength() / 8.0;

		components.clear();
		addRoomComponents(master, new Position(1.0, 1.0, 0.5), 4);
		addRoomComponents(assistant, new Position(3.5, 4, 0.5), 4);
		packComponents();

		canvas.setDefaultCenter();		
		canvas.center.z = 0.5;
	}

	public void setup3()
	{
		identifier = "0 ref";
		
		double freq =  430000000.0;
		master = new Transmitter(freq, 0.0);
		assistant = new Transmitter(freq + 1000.0, 0.0);
		interferenceVectorDelta = master.getWaveLength() / 8.0;

		components.clear();
		addRoomComponents(master, new Position(1.0, 1.0, 0.5), 0);
		addRoomComponents(assistant, new Position(6.0, 3, 0.5), 0);
		packComponents();

		canvas.setDefaultCenter();		
		canvas.center.z = 0.5;
	}

	public void setup4()
	{
	}

	public void loadSetup(int id)
	{
		if( id == 1 )
			setup1();
		else if( id == 2 )
			setup2();
		else if( id == 3 )
			setup3();
		else if( id == 4 )
			setup4();
	}

	public Canvas canvas;

	public static String toString(ComplexNumber number)
	{
		return "" + number.getComplexArgument()
			+ "\t" + number.getAbsoluteValue() 
			+ "\t" + number.x 
			+ "\t" + number.y;
	}

	public void printRipsData(Position slave1, Position slave2)
	{
		double origMasterFreq = master.getFrequency();
		double origAssistantFreq = assistant.getFrequency();

		ComplexNumber[] relativeSignals = new ComplexNumber[120];
		
		for(int channel = 0; channel < relativeSignals.length; ++channel)
		{
			master.setFrequency(origMasterFreq + channel * CHANNEL_FREQ);
			assistant.setFrequency(origAssistantFreq + channel * CHANNEL_FREQ);
			
			ComplexNumber signal = new ComplexNumber(getInterferenceSignal(slave1));
			signal.divide(getInterferenceSignal(slave2));
			relativeSignals[channel] = signal;
			
//			System.out.println("" + channel + "\t" + toString(signal));
		}

		master.setFrequency(origMasterFreq);
		assistant.setFrequency(origAssistantFreq);
		
		for(double distance = -20.0; distance < 20.0; distance += 0.05)
		{
			ComplexNumber signal =  getCorrelation(relativeSignals, origMasterFreq, 
				origMasterFreq + (relativeSignals.length-1) * CHANNEL_FREQ, distance);

			ComplexNumber signal2 =  getCorrelation2(relativeSignals, origMasterFreq, 
				origMasterFreq + (relativeSignals.length-1) * CHANNEL_FREQ, distance);

			double signal3 =  getCorrelation3(relativeSignals, origMasterFreq, 
				origMasterFreq + (relativeSignals.length-1) * CHANNEL_FREQ, distance);

			double signal4 =  getCorrelation4(relativeSignals, origMasterFreq, 
				origMasterFreq + (relativeSignals.length-1) * CHANNEL_FREQ, distance);

			System.out.print("" + distance);
			System.out.print("\t" + toString(signal));
			System.out.print("\t" + toString(signal2));
			System.out.print("\t" + signal3);
			System.out.print("\t" + signal4);
			System.out.println();
		}
	}

	public ComplexNumber getCorrelation(ComplexNumber[] relativeSignals, double startFreq, double endFreq,
		double distance)
	{
		ComplexNumber signal = new ComplexNumber(0.0, 0.0);
		
		double channel_sep = (endFreq - startFreq) / (relativeSignals.length - 1); 
		for(int i = 0; i < relativeSignals.length; ++i)
		{
			double frequency = startFreq + i * channel_sep;
			double phase = frequency * Transmitter.TWO_PI_OVER_LIGHT * distance;
			ComplexNumber rotation = ComplexNumber.createTrigForm(1.0, -phase);
			rotation.multiply(relativeSignals[i]);
			signal.add(rotation);
		}

		signal.multiply(1.0 / relativeSignals.length);
		
		return signal;
	}
	
	public ComplexNumber getCorrelation2(ComplexNumber[] relativeSignals, double startFreq, double endFreq,
		double distance)
	{
		ComplexNumber signal = new ComplexNumber(0.0, 0.0);
		
		double channel_sep = (endFreq - startFreq) / (relativeSignals.length - 1); 
		for(int i = 0; i < relativeSignals.length; ++i)
		{
			double frequency = startFreq + i * channel_sep;
			double phase = frequency * Transmitter.TWO_PI_OVER_LIGHT * distance;
			ComplexNumber rotation = ComplexNumber.createTrigForm(1.0, relativeSignals[i].getComplexArgument()-phase);
			signal.add(rotation);
		}

		signal.multiply(1.0 / relativeSignals.length);
		
		return signal;
	}
	
	public double getCorrelation3(ComplexNumber[] relativeSignals, double startFreq, double endFreq,
		double distance)
	{
		double error = 0.0;
		
		double channel_sep = (endFreq - startFreq) / (relativeSignals.length - 1); 
		for(int i = 0; i < relativeSignals.length; ++i)
		{
			double frequency = startFreq + i * channel_sep;
			double phase = frequency * Transmitter.TWO_PI_OVER_LIGHT * distance;
			
			double e = Math.abs(relativeSignals[i].getComplexArgument()-phase);
			e /= 2 * Math.PI;
			e -= Math.floor(e);
			e *= 2 * Math.PI;
			if( e > Math.PI )
				e = 2 * Math.PI - e; 
			
			error += e;
		}
		
		error /= relativeSignals.length;
		
		return error;
	}
	
	public double getCorrelation4(ComplexNumber[] relativeSignals, double startFreq, double endFreq,
		double distance)
	{
		double error = 0.0;
		double weights = 0.0;
		
		double channel_sep = (endFreq - startFreq) / (relativeSignals.length - 1); 
		for(int i = 0; i < relativeSignals.length; ++i)
		{
			double weight = 0.0;
			if( i > 0 && i < relativeSignals.length -1 )
				weight = 1.0 / (0.2 + Math.abs(relativeSignals[i-1].getComplexArgument() - relativeSignals[i+1].getComplexArgument()));
			
			double frequency = startFreq + i * channel_sep;
			double phase = frequency * Transmitter.TWO_PI_OVER_LIGHT * distance;
			
			double e = Math.abs(relativeSignals[i].getComplexArgument()-phase);
			e /= 2 * Math.PI;
			e -= Math.floor(e);
			e *= 2 * Math.PI;
			if( e > Math.PI )
				e = 2 * Math.PI - e; 
			
			error += weight * e;
			weights += weight;
		}
		
		error /= weights;
		
		return error;
	}
	
	public static void main(String[] _)
	{
		Room room = new Room();
		room.canvas = new Canvas(room);
		room.setup1();

//		Position master = new Position(1.0, 1.0, 0.5);
//		Position assistant = new Position(6.0, 3, 0.5);
//		Position slave1 = new Position(3,2,0.5);
//		Position slave2 = new Position(6,1,1.5);
		
//		room.printRipsData(slave1, slave2);
		
//		System.out.println("qrange = " + (master.getDistance(slave1) - master.getDistance(slave2) 
//			+ assistant.getDistance(slave2) - assistant.getDistance(slave1)));
		
		
		room.canvas.display();
	}
}
