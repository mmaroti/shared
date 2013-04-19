package mmaroti.ua.alg;

/**
 *	Copyright (C) 2000, 2001 Miklos Maroti
 */

public class UnaryPolArgument implements Argument
{
	private int size;
	private int[] args;
	private int skip;

	public int[] args() { return args; }

	public boolean first()
	{
		int i = args.length;
		
		while( --i > skip )
			args[i] = 0;
			
		while( --i >= 0 )
			args[i] = 0;
			
		return true;
	}
	
	public boolean next()
	{
		int i = args.length;

		while( --i > skip && ++args[i] >= size )
			args[i] = 0;

		if( i == skip )		
			while( --i >= 0 && ++args[i] >= size )
				args[i] = 0;
		
		return i >= 0;
	}
	
	public UnaryPolArgument(int arity, int size, int skip)
	{
		if( arity < 0 || size < 0 || skip < 0 || skip >= arity )
			throw new IllegalArgumentException();
		
		this.size = size;
		this.args = new int[arity];
		this.skip = skip;
	}
	
	public UnaryPolArgument(int[] args, int size, int skip)
	{
		if( size < 0 || skip < 0 || skip >= args.length )
			throw new IllegalArgumentException();
		
		this.size = size;
		this.args = args;
		this.skip = skip;
	}
}
