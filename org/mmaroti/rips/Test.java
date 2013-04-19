/*
 * Created on Aug 7, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

/**
 * @author mmaroti
 */
public class Test
{
	public static void main3(String[] args) throws Exception
	{
		Data data = new Data();
		
		data.load("C:\\Temp\\rips\\20060505");
		
		AbsolutePhaseSolver solver = new AbsolutePhaseSolver();
		solver.load(data.ripsData, "1", -31);
		solver.solve();
		
		System.out.println("done");
	}

	public static void main(String[] args) throws Exception
	{
		Data data = new Data();
		
		data.load("C:\\Temp\\rips\\20060505");
		data = EnvelopeFreqFilter.filter(data);
		
		String scenario = "3";
		int node = 7294;
		
		// 2192,7551,7788 excellent peak
		// 8092, 8736, 6957 larger ridges with good peak
		// 9799 good peak with broken terrain
		// 7294 shallow ridges with peak
		// 6670 ridge with several peaks
		// 7034 shallow ridge with almost no peak
		
		MobileConsistencyField field = new MobileConsistencyField();
		field.load(data, scenario, node);

		Data.Pos pos = data.getPosition(scenario, node);
		field.printErrorSurface(pos.x, pos.y, pos.z, 40, 0.05);

		System.out.println("done");
	}
	
	public static void main2(String[] args) throws Exception
	{
		Data data = new Data();
		
		data.load("C:\\Temp\\rips\\20060505");
		
		System.out.println("average error: " + MobileConsistencyField.computeAverageError(data));
	}
}
