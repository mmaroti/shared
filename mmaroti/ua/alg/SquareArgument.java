package mmaroti.ua.alg;

/**
 *	Copyright (C) 2000, 2001 Miklos Maroti
 */

public class SquareArgument implements Argument
{
	private int size;
	private int[] args;

	public int[] args() { return args; }

	public boolean first()
	{
		for(int i = 0; i < args.length; ++i)
			args[i] = 0;
			
		return true;
	}
	
	public boolean next()
	{
		int i = args.length;
		while( --i >= 0 && ++args[i] >= size )
			args[i] = 0;
		
		return i >= 0;
	}
	
	public SquareArgument(int arity, int size)
	{
		if( arity < 0 || size < 0 )
			throw new IllegalArgumentException();
		
		this.size = size;
		this.args = new int[arity];
	}
	
	public SquareArgument(int[] args, int size)
	{
		if( size < 0 )
			throw new IllegalArgumentException();
		
		this.size = size;
		this.args = args;
	}
}
