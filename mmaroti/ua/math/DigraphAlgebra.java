package mmaroti.ua.math;

/**
 *	Copyright (C) 2002 Miklos Maroti
 */

import mmaroti.ua.alg.*;

public class DigraphAlgebra implements Algebra
{
	protected Algebra alg;
	protected Function op;
	protected int size;

	public Function[] operations() { return alg.operations(); }
	public Function[] relations() { return alg.relations(); }
	public int size() { return size; }
	public Algebra alg() { return alg; }

	static int[] intPair = new int[2];
	public final int prod(int a, int b)
	{
		intPair[0] = a;
		intPair[1] = b;
		return op.value(intPair);
	}

	public DigraphAlgebra(Algebra alg)
	{
		this.alg = alg;
		op = alg.operations()[0];
		size = alg.size();
	}
}
