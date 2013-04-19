package org.mmaroti.ua.research;

import org.mmaroti.ua.set.*;

public class MinmalMajorityClones
{
	static Set functions = new ProductSet(new FiniteSet(5), 60, "");
	static SubSet clone = new SubSet(functions);
	
	static Object projx;
	static Object projy;
	static Object projz;
	static int generator[];
	
	static void closure()
	{
		
	}
	
	public static void main(String[] _)
	{
		projx = functions.parse("012210013310014410023320024420034430123321124421134431234432");
		projy = functions.parse("120102130103140104230203240204340304231213241214341314342324");
		projz = functions.parse("201021301031401041302032402042403043312132412142413143423243");
	
		System.out.println(functions.toString(projx));
	}
}
