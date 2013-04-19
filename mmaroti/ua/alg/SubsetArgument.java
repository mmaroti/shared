package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.Arrays;

public class SubsetArgument implements Argument
{
	private int[] args;		// strictly increasing function
	private boolean[] selected;

	public int[] args() { return args; }
	public boolean[] selected() { return selected; }

	public void finalize() { clear(); }
	public void clear()
	{
		int i = args.length;
		while( --i >= 0 )
			if( args[i] >= 0 )
			{
				selected[args[i]] = false;
				args[i] = -1;
			}
	}

	public boolean first()
	{
		clear();
				
		int i = 0;
		int a = -1;
		while( i < args.length )
		{
			do
			{
				if( ++a >= selected.length )
					return false;
			} while( selected[a] == true );
				
			args[i++] = a;
		}

		// i = args.length;
		while( --i >= 0 )
			selected[args[i]] = true;

		return true;
	}

	public boolean next()
	{
		int a = selected.length;
		while( --a >= 0 && selected[a] == true )
			;
			
		int i = args.length;
		while( --i >= 0 && args[i] > a )
			;

		if( i < 0 )
			return false;

		a = args[i] + 1;
		
		while( i < args.length )
		{
			selected[args[i]] = false;
			
			while( selected[a] == true )
				++a;
				
			selected[a] = true;
			args[i++] = a++;
		}
		
		return true;
	}

	public SubsetArgument(int arity, int size)
	{
		if( arity < 0 )
			throw new IllegalArgumentException();
		
		args = new int[arity];
		selected = new boolean[size];

		Arrays.fill(args, -1);
		Arrays.fill(selected, false);
	}
	
	public SubsetArgument(int[] args, boolean[] selected)
	{
		this.args = args;
		this.selected = selected;
		
		Arrays.fill(args, -1);
	}
}
