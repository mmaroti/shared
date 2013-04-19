package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;

public class Arrays2
{
	public static boolean shallowEquals(Object[] a, Object[] b)
	{
		if( a == null || b == null )
			return a == b;
		
		int i = a.length;
		if( i != b.length )
			return false;
		
		while( --i >= 0 )
			if( a[i] != b[i] )
				return false;
		
		return true;
	}
	
	// from Daniel Phillips <phillips@innominate.de>
	public static int hashCode(int[] a)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = a.length;
		while( --i >= 0 )
		{
			int hash = hash1 + (hash0 ^ (a[i] * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}
	
	public static int shallowHashCode(Object[] a)
	{
		int hash0 = 0x12a3fe2d;
		int hash1 = 0x37abe8f9;

		int i = a.length;
		while( --i >= 0 )
		{
			int hash = hash1 + 
				(hash0 ^ (System.identityHashCode(a[i]) * 71523));
			if (hash < 0) 
				hash -= 0x7fffffff;
				
			hash1 = hash0;
			hash0 = hash;
		}

		return hash0;
	}
	
	public static int[] toIntArray(Collection collection)
	{
		int[] ret = new int[collection.size()];
		int index = 0;
		
		Iterator iter = collection.iterator();
		while( iter.hasNext() )
			ret[index++] = ((Integer)iter.next()).intValue();
		
		return ret;
	}
}
