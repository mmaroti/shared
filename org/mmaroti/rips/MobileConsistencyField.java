/*
 * Created on Aug 11, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;

/**
 * @author mmaroti
 */
public class MobileConsistencyField
{
	public static double ENVELOPE_FREQ_CUTOFF = 2000.0; 
	public static double TWO_PI = 2.0 * Math.PI;
	
	public class Hyperbola
	{
		// the coordinates of point A
		double Ax, Ay, Az;

		// the coordinates of point B
		double Bx, By, Bz;
		
		// the wave length of carrier frequency
		double lambda;
		
		// the measured phase offset divided by 2*PI
		double fraction;
		
		/**
		 * Returns the absolute error (between 0 and 0.5) of the measured
		 * and theoretical phases for this position.
		 */
		public double getError(double x, double y, double z)
		{
			double dA = Math.sqrt((Ax-x)*(Ax-x) + (Ay-y)*(Ay-y) + (Az-z)*(Az-z));
			double dB = Math.sqrt((Bx-x)*(Bx-x) + (By-y)*(By-y) + (Bz-z)*(Bz-z));

			double e = (dA - dB) / lambda - fraction;
			return Math.abs(e - Math.round(e));
		}

		/**
		 * Returns the difference (between 0 and 1) of the measured
		 * and theoretical phases for this position.
		 */
		public double getDifference(double x, double y, double z)
		{
			double dA = Math.sqrt((Ax-x)*(Ax-x) + (Ay-y)*(Ay-y) + (Az-z)*(Az-z));
			double dB = Math.sqrt((Bx-x)*(Bx-x) + (By-y)*(By-y) + (Bz-z)*(Bz-z));

			double e = (dA - dB) / lambda - fraction;
			return e - Math.floor(e);
		}
		
		public String toString()
		{
			return "A=(" + Ax + "," + Ay + "," + Az 
				+ ") B=(" + Bx + "," + By + "," + Bz
				+ ") lambda=" + lambda + " fraction=" + fraction; 
		}
	}
	
	public List<Hyperbola> hyperbolas = new ArrayList<Hyperbola>();

	protected void loadNotTransmitter(Data data, String scenario, int node)
	{
		Map<String, List<Data.Rips>> transmissions = new HashMap<String, List<Data.Rips>>();
		
		Iterator<Data.Rips> iter = data.ripsData.iterator();
		while( iter.hasNext() )
		{
			Data.Rips rips = iter.next();
			if( ! rips.scenario.equals(scenario) || rips.master == node || rips.assistant == node )
				continue;

			String transmission = rips.time + " " + rips.master + " " + rips.assistant + " " + rips.channel + " " + rips.power;
			List<Data.Rips> entries = transmissions.get(transmission);
			
			if( entries == null )
			{
				entries = new ArrayList<Data.Rips>();
				transmissions.put(transmission, entries);
			}
			
			entries.add(rips);
		}
		
		Iterator<List<Data.Rips>> iter2 = transmissions.values().iterator();
		while( iter.hasNext() )
		{
			List<Data.Rips> entries = iter2.next();
			
			Data.Rips mobile = null;
			Iterator<Data.Rips> iter3 = entries.iterator();
			while( iter3.hasNext() )
			{
				Data.Rips rips = iter3.next();
				if( rips.receiver == node )
				{
					mobile = rips;
					iter3.remove();
					break;
				}
			}
			
			if( mobile == null )
				continue;

			Data.Pos A = data.getPosition(scenario, mobile.master);
			Data.Pos B = data.getPosition(scenario, mobile.assistant);
			
			iter3 = entries.iterator();
			while( iter3.hasNext() )
			{
				Data.Rips rips = iter3.next();
				if( Math.abs(rips.envelopeFreq - mobile.envelopeFreq) > ENVELOPE_FREQ_CUTOFF )
					continue;
				
				Data.Pos C = data.getPosition(scenario, rips.receiver);
				
				Hyperbola hyperbola = new Hyperbola();
				
				hyperbola.Ax = A.x;
				hyperbola.Ay = A.y;
				hyperbola.Az = A.z;
				hyperbola.Bx = B.x;
				hyperbola.By = B.y;
				hyperbola.Bz = B.z;
				hyperbola.lambda = Data.SPEED_OF_LIGHT / mobile.getCarrierFrequency();
				
				double phase = rips.absolutePhase - mobile.absolutePhase;
				double known = (B.getDistance(C) - A.getDistance(C)) / hyperbola.lambda;
				hyperbola.fraction = phase / TWO_PI - known;
				hyperbola.fraction -= Math.floor(hyperbola.fraction);
				
				hyperbolas.add(hyperbola);
			}
		}
	}
	
	protected void loadTransmitter(Data data, String scenario, int node)
	{
		Map<String, List<Data.Rips>> transmissions = new HashMap<String, List<Data.Rips>>();
		
		Iterator<Data.Rips> iter = data.ripsData.iterator();
		while( iter.hasNext() )
		{
			Data.Rips rips = iter.next();
			if( ! rips.scenario.equals(scenario) || (rips.master != node && rips.assistant != node) )
				continue;

			String transmission = rips.time + " " + rips.master + " " + rips.assistant + " " + rips.channel + " " + rips.power;
			List<Data.Rips> entries = transmissions.get(transmission);

			if( entries == null )
			{
				entries = new ArrayList<Data.Rips>();
				transmissions.put(transmission, entries);
			}
			
			entries.add(rips);
		}
		
		Iterator<List<Data.Rips>> iter2 = transmissions.values().iterator();
		while( iter.hasNext() )
		{
			List<Data.Rips> entries = iter2.next();
			
			for(int i = 0; i < entries.size(); ++i)
			{
				Data.Rips slaveC = entries.get(i); 
				Data.Pos C = data.getPosition(scenario, slaveC.receiver);

				Data.Pos other = data.getPosition(scenario,
					slaveC.master != node ? slaveC.master : slaveC.assistant);

				for(int j = 0; j < entries.size(); ++j)
				{
					if( i == j )
						continue;
						
					Data.Rips slaveD = entries.get(j); 
					if( Math.abs(slaveC.envelopeFreq - slaveD.envelopeFreq) > ENVELOPE_FREQ_CUTOFF )
						continue;
					
					Data.Pos D = data.getPosition(scenario, slaveD.receiver);
					
					Hyperbola hyperbola = new Hyperbola();
				
					hyperbola.Ax = C.x;
					hyperbola.Ay = C.y;
					hyperbola.Az = C.z;
					hyperbola.Bx = D.x;
					hyperbola.By = D.y;
					hyperbola.Bz = D.z;
					hyperbola.lambda = Data.SPEED_OF_LIGHT / slaveC.getCarrierFrequency();

					double phase = slaveC.absolutePhase - slaveD.absolutePhase;
					if( node == slaveC.master )
						phase = -phase;
					
					double known = (other.getDistance(D) - other.getDistance(C)) / hyperbola.lambda;
					hyperbola.fraction = phase / TWO_PI - known;
					hyperbola.fraction -= Math.floor(hyperbola.fraction);

					hyperbolas.add(hyperbola);
				}
			}
		}
	}

	public void load(Data data, String scenario, int node)
	{
		hyperbolas.clear();

		loadNotTransmitter(data, scenario, node);
		loadTransmitter(data, scenario, node);
//		loadNormalizedPhases(data, scenario, node);
		
		Data.Pos B = data.getPosition(scenario, node);
		
		System.out.println("loaded for scenario " + scenario + " node " + node 
			+ ": " + hyperbolas.size() + " hyperbolas with average error " + 
			getAverageError(B.x, B.y, B.z));
	}

	protected Map<String, Map<String, Double>> solutions = new HashMap<String, Map<String, Double>>();

	public Map<String, Double> getSolution(Data data, String scenario, int channel)
	{
		String name = "" + System.identityHashCode(data) + " " + scenario + " " + channel;
		
		Map<String, Double> solution = solutions.get(name);
		if( solution == null )
		{
			AbsolutePhaseSolver solver = new AbsolutePhaseSolver();
			solver.load(data.ripsData, scenario, channel);
			solution = solver.solve().getValueMap();
			
			solutions.put(name, solution);
		}
		
		return solution;
	}

	public double getPhase(Map<String, Double> solution, int A, int B, int C, int D)
	{
		double phase = 0.0;
		
		Double d = solution.get(AbsolutePhaseSolver.getDistanceVar(A, D));
		if( d == null )
			return Double.NaN;
		phase += d.doubleValue();
		
		d = solution.get(AbsolutePhaseSolver.getDistanceVar(B, D));
		if( d == null )
			return Double.NaN;
		phase -= d.doubleValue();
		
		d = solution.get(AbsolutePhaseSolver.getDistanceVar(B, C));
		if( d == null )
			return Double.NaN;
		phase += d.doubleValue();

		d = solution.get(AbsolutePhaseSolver.getDistanceVar(A, C));
		if( d == null )
			return Double.NaN;
		phase -= d.doubleValue();

		return phase;
	}

	public void loadNormalizedPhases(Data data, String scenario, int node)
	{
		int[] nodes = data.getNodes();
		for(int i = 0; i < nodes.length; ++i)
		{
			if( nodes[i] == node )
			{
				nodes[i] = nodes[0];
				nodes[0] = node;
				break;
			}
		}
		
		Iterator<Integer> channels = data.channels.iterator();
		while( channels.hasNext() )
		{
			int channel = channels.next();
			
			Map<String, Double> solution = getSolution(data, scenario, channel);

			Data.Pos B = data.getPosition(scenario, nodes[1]);
			Data.Pos C = data.getPosition(scenario, nodes[2]);

			for(int i = 3; i < nodes.length; ++i)
			{
				Data.Pos D = data.getPosition(scenario, nodes[i]);
					
				Hyperbola hyperbola = new Hyperbola();
				
				hyperbola.Ax = C.x;
				hyperbola.Ay = C.y;
				hyperbola.Az = C.z;
				hyperbola.Bx = D.x;
				hyperbola.By = D.y;
				hyperbola.Bz = D.z;
				hyperbola.lambda = Data.SPEED_OF_LIGHT / Data.getCarrierFrequency(channel);

				double phase = getPhase(solution, nodes[0], nodes[1], nodes[2], nodes[i]);	
				if( Double.isNaN(phase) )
					continue;

				double known = (B.getDistance(C) - B.getDistance(D)) / hyperbola.lambda;
				hyperbola.fraction = - (phase / TWO_PI - known);
				hyperbola.fraction -= Math.floor(hyperbola.fraction);

				hyperbolas.add(hyperbola);
			}

			C = data.getPosition(scenario, nodes[1]);
			
			for(int i = 2; i < nodes.length; ++i)
			{
				B = data.getPosition(scenario, nodes[i]);
				
				for(int j = i + 1; j < nodes.length; ++j)
				{
					Data.Pos D = data.getPosition(scenario, nodes[j]);
					
					Hyperbola hyperbola = new Hyperbola();
				
					hyperbola.Ax = C.x;
					hyperbola.Ay = C.y;
					hyperbola.Az = C.z;
					hyperbola.Bx = D.x;
					hyperbola.By = D.y;
					hyperbola.Bz = D.z;
					hyperbola.lambda = Data.SPEED_OF_LIGHT / Data.getCarrierFrequency(channel);

					double phase = getPhase(solution, nodes[0], nodes[i], nodes[1], nodes[j]);	
					if( Double.isNaN(phase) )
						continue;

					double known = (B.getDistance(C) - B.getDistance(D)) / hyperbola.lambda;
					hyperbola.fraction = - (phase / TWO_PI - known);
					hyperbola.fraction -= Math.floor(hyperbola.fraction);

					hyperbolas.add(hyperbola);
				}
			}
		}
	}

	public double getAverageError2(double x, double y, double z)
	{
		double error = 0.0;
		
		Iterator<Hyperbola> iter = hyperbolas.iterator();
		while( iter.hasNext() )
		{
			Hyperbola hyperbola = iter.next();
			error += hyperbola.getError(x, y, z);
		}
		
		return error / hyperbolas.size();
	}
	
	public double getAverageError3(double x, double y, double z)
	{
		double error = 0.0;
		
		Iterator<Hyperbola> iter = hyperbolas.iterator();
		while( iter.hasNext() )
		{
			Hyperbola hyperbola = iter.next();
			if( hyperbola.getError(x, y, z) < 0.10 )
				++error;
		}

		return - error / hyperbolas.size();
	}
	
	public double getAverageError(double x, double y, double z)
	{
		ComplexNumber error = new ComplexNumber(0.0, 0.0);
		
		Iterator<Hyperbola> iter = hyperbolas.iterator();
		while( iter.hasNext() )
		{
			Hyperbola hyperbola = iter.next();
			error.addTrigForm(1.0, TWO_PI * hyperbola.getDifference(x, y, z));
		}

		return error.getAbsoluteValue() / hyperbolas.size();
	}
	
	public void printErrorSurface(double x, double y, double z, int n, double d)
	{
		for(int i = -n; i <= n; ++i)
		{
			for(int j = -n; j <= n; ++j)
			{
				System.out.print(getAverageError(x + j*d, y + i*d, z));
				if( j != n )
					System.out.print("\t");
			}
				
			System.out.println();	
		}
	}
	
	public static double computeAverageError(Data data, String scenario)
	{
		double e = 0.0;
		
		MobileConsistencyField field = new MobileConsistencyField();

		Iterator<Integer> iter = data.nodes.iterator();
		while( iter.hasNext() )
		{
			int node = iter.next();

			field.load(data, scenario, node);

			Data.Pos pos = data.getPosition(scenario, node);
			e += field.getAverageError(pos.x, pos.y, pos.z);
		}
		
		return e / data.nodes.size();
	}
	
	public static double computeAverageError(Data data)
	{
		double e = 0.0;
		
		Iterator<String> iter = data.scenarios.iterator();
		while( iter.hasNext() )
		{
			String scenario = iter.next();
			e += computeAverageError(data, scenario);
		}
		
		return e / data.scenarios.size();
	}
}
