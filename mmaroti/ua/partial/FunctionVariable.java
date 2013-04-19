package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import mmaroti.ua.alg.*;

public class FunctionVariable implements Function
{
	private int size;
	private int arity;
	private Variable buffer[];

	public int size() { return size; }
	public int arity() { return arity; }
	public Variable[] variables() { return buffer; }

	public FunctionVariable(int size, int arity, int maxValue)
	{
		if( size <= 0 || arity < 0 )
			throw new IllegalArgumentException();
			
		this.size = size;
		this.arity = arity;
		
		int length = 1;
		for(int i = 0; i < arity; ++i)
			length *= size;
		
		buffer = new Variable[length];

		for(int i = 0; i < length; ++i)
			buffer[i] = new Variable(maxValue);
	}

	public Variable variable(int args[])
	{
		if( arity != args.length )
			throw new IllegalArgumentException();
		
		int a = 0;
		for(int i = 0; i < arity; ++i)
		{
			if( args[i] < 0 || args[i] >= size )
				throw new IllegalArgumentException();
			
			a *= size;
			a += args[i];
		}
		
		return buffer[a];
	}

	public Variable variable(int a0)
	{
		if( arity != 1 )
			throw new IllegalStateException();
			
		if( a0 < 0 || a0 >= size )
			throw new IllegalArgumentException();
			
		return buffer[a0];
	}

	public int value(int args[])
	{
		return variable(args).value;
	}
}
