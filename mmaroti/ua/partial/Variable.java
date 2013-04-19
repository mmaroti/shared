package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class Variable extends Node
{
	public int value;
	public int maxValue;
	public int marker;
	
	public int evaluate()
	{
		return value;
	}
	
	private static int markerPool = 0;
	public void takeMarker()
	{
		if( marker != 0 || --markerPool >= 0 )
			throw new IllegalStateException();
			
		marker = markerPool;
	}
	
	public Variable(int maxValue)
	{
		if( maxValue <= 0 )
			throw new IllegalArgumentException();
		
		this.maxValue = maxValue;
		this.marker = 0;
	}
}
