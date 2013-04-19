package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import mmaroti.ua.alg.*;

public class Test
{
	public static void main(String[] _)
	{
		AlgebraBuffer alg = new AlgebraBuffer(2);

		int[] join = {
			0, 1,
			1, 1 };
		alg.addOperation(new FunctionBuffer(2, 2, join));
		
		int[] meet = {
			0, 0,
			0, 1 };
		alg.addOperation(new FunctionBuffer(2, 2, meet));

		for(int i = 1; i <= 6; ++i)
		{
			System.out.println(i);
			FreeAlgebra free = new FreeAlgebra(alg, i);
			System.out.println("done");
			System.out.println(free.size());
//			UaWriter.out.print(new IntArrayBuffer(free.generators()));
		}
	}
}
