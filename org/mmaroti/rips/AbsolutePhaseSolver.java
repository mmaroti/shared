/*
 * Created on Aug 8, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;

/**
 * @author mmaroti
 */
public class AbsolutePhaseSolver
{
	public Set nodes = new TreeSet();

	public static class Quad
	{
		public int A, B, C, D;
		public double radian;
	}
	
	public List quads = new ArrayList();
	
	public void load(List ripsData, String scenario, int channel)
	{
		nodes.clear();
		quads.clear();
		
		Map references = new HashMap();
		
		Iterator iter = ripsData.iterator();
		while( iter.hasNext() )
		{
			Data.Rips rips = (Data.Rips)iter.next();
			if( ! rips.scenario.equals(scenario) || rips.channel != channel )
				continue;
			
			String refName = rips.time + " " + rips.master + " " + rips.assistant + " " + rips.power;
			Data.Rips ref = (Data.Rips)references.get(refName);
			if( ref == null )
			{
				references.put(refName, rips);
				continue;
			}
			
			Quad quad = new Quad();
			quad.A = rips.master;
			quad.B = rips.assistant;
			quad.C = rips.receiver;
			quad.D = ref.receiver;
			quad.radian = rips.absolutePhase - ref.absolutePhase;
			quads.add(quad);
			
			nodes.add(new Integer(quad.A));
			nodes.add(new Integer(quad.B));
			nodes.add(new Integer(quad.C));
			nodes.add(new Integer(quad.D));
		}
	}

	protected static String getDistanceVar(int A, int B)
	{
		if( A < B )
		{
			int C = A;
			A = B;
			B = C;
		}
		
		return "d(" + A + "," + B + ")";
	}
	
	protected PhaseEquations equations = new PhaseEquations();
	
	protected void createAllEquations()
	{
		equations.clear();
		
		Iterator iter = quads.iterator();
		while( iter.hasNext() )
		{
			Quad quad = (Quad)iter.next();
			
			PhaseEquations.Equation equation = equations.createEquation();
			equation.addCoefficient(getDistanceVar(quad.A,quad.D), 1.0);
			equation.addCoefficient(getDistanceVar(quad.B,quad.D), -1.0);
			equation.addCoefficient(getDistanceVar(quad.B,quad.C), 1.0);
			equation.addCoefficient(getDistanceVar(quad.A,quad.C), -1.0);
			equation.addConstant(quad.radian);
			
			equations.addEquation(equation);
		}
	}

	public PhaseEquations.Solution solve()
	{
		createAllEquations();

		PhaseEquations.Solution solution = equations.solve();
		while( solution.getMaximumError() > 2.0 )
		{
			equations.removeLargeErrorEquations(solution, 2.0);
			solution = equations.solve();
		}
		while( solution.getMaximumError() > 1.0 )
		{
			equations.removeLargeErrorEquations(solution, 1.0);
			solution = equations.solve();
		}
		while( solution.getMaximumError() > 0.5 )
		{
			equations.removeLargeErrorEquations(solution, 0.5);
			solution = equations.solve();
		}
		while( solution.getMaximumError() > 0.25 )
		{
			equations.removeLargeErrorEquations(solution, 0.25);
			solution = equations.solve();
		}
		
		return solution;
	}
}
