package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public class IntPair implements IntArray, Cloneable
{
	public int first;
	public int second;
	
	public int length() { return 2; }

	public int get(int index)
	{
		if( index < 0 || index > 1 )
			throw new IndexOutOfBoundsException();
			
		 return index == 0 ? first : second;
	}
	
	public void set(int index, int value)
	{
		if( index == 0 )
			first = value;
		else if( index == 1 )
			second = value;
		else
			throw new IndexOutOfBoundsException();
	}
	
	public void fill(int value)
	{
		first = value;
		second = value;
	}
	
	public int hashCode() { return first * 71523 + second; }
	
	public boolean equals(Object o)
	{
		if( !(o instanceof IntPair) )
			return false;
		
		IntPair p = (IntPair)o;
		return first == p.first && second == p.second;
	}
	
	public IntPair() { }
	public IntPair(int a, int b)
	{
		first = a;
		second = b;
	}
	
	public IntPair(IntPair p)
	{
		first = p.first;
		second = p.second;
	}
}
