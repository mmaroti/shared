package mmaroti.ua.alg;

/**
 *	Copyright (C) 2000, 2001 Miklos Maroti
 */

public class SphereArgument implements Argument
{
	private int radius;
	private int[] args;
	private int count;

	public int[] args() { return args; }
	
	public boolean first()
	{
		if( args.length > 0 )
		{
			if( radius < 0 )
				return false;
		
			int i = args.length;
			args[--i] = radius;
			
			while( --i >= 0 )
				args[i] = 0;
		}
		else if( radius >= 0 )
			return false;
				
		count = 1;
		return true;
	}
	
	public boolean next()
	{
		int i = args.length;
		while( --i >= 0 && ++args[i] >= radius )
		{
			if( args[i] > radius )
			{
				--count;
				args[i] = 0;
			}
			else
			{
				++count;
				break;
			}
		}
		
		if( count <= 0 )
		{
			args[args.length-1] = radius;
			count = 1;
		}
		
		return i >= 0;
	}
	
	public SphereArgument(int arity, int radius)
	{
		if( arity < 0 || radius < -1 )
			throw new IllegalArgumentException();
		
		this.radius = radius;
		this.args = new int[arity];
	}
	
	public SphereArgument(int[] args, int radius)
	{
		if( radius < -1 )
			throw new IllegalArgumentException();
		
		this.radius = radius;
		this.args = args;
	}
}
