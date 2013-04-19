package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public class ShallowPair implements Cloneable
{
	public Object first;
	public Object second;
	
	public int hashCode() 
	{
		return System.identityHashCode(first) * 71523 + 
			System.identityHashCode(second);
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof ShallowPair) )
			return false;
		
		ShallowPair p = (ShallowPair)o;
		return first == p.first && second == p.second;
	}
	
	public ShallowPair() { }
	public ShallowPair(Object a, Object b)
	{
		first = a;
		second = b;
	}
	
	public ShallowPair(ShallowPair p)
	{
		first = p.first;
		second = p.second;
	}
}
