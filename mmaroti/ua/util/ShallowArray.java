package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public class ShallowArray
{
	public Object[] value;
	
	public int hashCode() { return Arrays2.shallowHashCode(value); }
	public Object get(int index) { return value[index]; }
	public void set(int index, Object object) { value[index] = object; }

	public ShallowArray(Object[] value) { this.value = value; }
	public ShallowArray(int length) { value = new Object[length]; }

	public ShallowArray(ShallowArray o) 
	{
		value = o.value.clone();
	}

	public boolean equals(Object o)
	{
		return (o instanceof ShallowArray) &&
			Arrays2.shallowEquals(value, ((ShallowArray)o).value);
	}
}
