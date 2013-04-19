package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.Arrays;

public class Algebras
{
	public static boolean equals(Algebra a, Algebra b)
	{
		return a.size() == b.size() &&
			Arrays.equals(a.operations(), b.operations()) &&
			Arrays.equals(a.relations(), b.relations());
	}

	public static int hashCode(Algebra a)
	{
		int c = a.size();
		
		Function[] f = a.operations();
		for(int i = 0; i < f.length; ++i)
			c += f[i].hashCode();
			
		f = a.relations();
		for(int i = 0; i < f.length; ++i)
			c += f[i].hashCode();
			
		return c;
	}
}
