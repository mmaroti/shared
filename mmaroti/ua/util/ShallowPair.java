package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public class ShallowPair<FIRST,SECOND> implements Cloneable
{
	public FIRST first;
	public SECOND second;
	
	public int hashCode() 
	{
		return System.identityHashCode(first) * 71523 + 
			System.identityHashCode(second);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o)
	{
		if( !(o instanceof ShallowPair) )
			return false;
		
		ShallowPair<FIRST,SECOND> p = (ShallowPair<FIRST,SECOND>)o;
		return first == p.first && second == p.second;
	}
	
	public ShallowPair() { }
	public ShallowPair(FIRST a, SECOND b)
	{
		first = a;
		second = b;
	}
	
	public ShallowPair(ShallowPair<FIRST,SECOND> p)
	{
		first = p.first;
		second = p.second;
	}
}
