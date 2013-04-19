package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;

public class IntArrayBuffer implements IntArray
{
	protected int[] array;
	public int[] array() { return array; }
	public int length() { return array.length; }

	public int get(int index) { return array[index]; }
	public void set(int index, int value) { array[index] = value; }

	public void fill(int value)
	{
		Arrays.fill(array, value);
	}

	public void remove(int index)
	{
		if( index < 0 || array.length <= index )
			throw new IndexOutOfBoundsException();
			
		int[] a = new int[array.length - 1];
		
		int i = array.length;
		while( --i > index )
			a[i-1] = array[i];
		
		while( --i >= 0 )
			a[i] = array[i];
			
		array = a;
	}
	
	public void add(int value)
	{
		int[] a = new int[array.length + 1];
		
		int i = array.length;
		a[i] = value;
		
		while( --i >= 0 )
			a[i] = array[i];
			
		array = a;
	}
	
	public int hashCode()
	{
		return Arrays2.hashCode(array);
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof IntArrayBuffer) )
			return false;

		IntArrayBuffer a = (IntArrayBuffer)o;
		
		return Arrays.equals(array, a.array);			
	}

	public Object clone()
	{
		return new IntArrayBuffer(array.clone());
	}
	
	public IntArrayBuffer(int length)
	{
		array = new int[length];
	}
	
	public IntArrayBuffer(int[] array)
	{
		this.array = array;
	}
	
	public IntArrayBuffer(int a, int b)
	{
		array = new int[2];
		array[0] = a;
		array[1] = b;
	}

	public IntArrayBuffer(int a, int b, int c)
	{
		array = new int[3];
		array[0] = a;
		array[1] = b;
		array[2] = c;
	}
	
	public IntArrayBuffer(Collection c)
	{
		array = new int[c.size()];
		Iterator iter = c.iterator();
		for(int i = 0; ; ++i)
			array[i] = ((Integer)iter.next()).intValue();
	}
}
