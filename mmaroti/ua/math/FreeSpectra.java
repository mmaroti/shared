package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 *	Copyright (C) 2001 Peter Jipsen
 */

import mmaroti.ua.alg.*;
import mmaroti.ua.io.*;
import mmaroti.ua.util.*;

public class FreeSpectra
{
	public static void main(String[] _)
	{
		AlgebraBuffer alg = new AlgebraBuffer(5);

		int[] table1 = {
			0, 3, 3, 3, 4,
			3, 1, 3, 3, 4,
			3, 3, 2, 3, 4,
			3, 3, 3, 3, 4,
			4, 4, 4, 4, 4 };
/*
		int[] table2 = {
			0, 3, 3, 3, 3,
			3, 1, 3, 3, 3,
			3, 3, 2, 3, 3,
			3, 3, 3, 3, 3,
			3, 3, 3, 3, 4 };
*/
		FunctionBuffer prod = new FunctionBuffer(5, 2, table1);
		alg.addOperation(prod);
		
		FunctionBuffer func = new FunctionBuffer(5, 3);

		for(int x = 0; x < 5; ++x)
			for(int y = 0; y < 5; ++y)
				for(int z = 0; z < 5; ++z)
					func.set(x,y,z, prod.value(prod.value(x,y),z));
		
		func.set(0,1,2, 4);
		func.set(0,2,1, 4);
		func.set(1,0,2, 4);
		func.set(1,2,0, 4);
		func.set(2,0,1, 4);
		func.set(2,1,0, 4);

		alg.addOperation(func);		

		for(int i = 1; i < 5; ++i)
		{
			FreeAlgebra free = new FreeAlgebra(alg, i);
			System.out.println(free.size());
			UaWriter.out.print(new IntArrayBuffer(free.generators()));
		}
	}
}
