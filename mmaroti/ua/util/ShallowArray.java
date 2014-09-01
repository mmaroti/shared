package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public class ShallowArray<TYPE>
{
	public TYPE[] value;
	
	public int hashCode() { return Arrays2.shallowHashCode(value); }
	public Object get(int index) { return value[index]; }
	public void set(int index, TYPE object) { value[index] = object; }

	public ShallowArray(TYPE[] value) { this.value = value; }
	// public ShallowArray(int length) { value = new Object[length]; }

	public ShallowArray(ShallowArray<TYPE> o) 
	{
		value = o.value.clone();
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o)
	{
		return (o instanceof ShallowArray) &&
			Arrays2.shallowEquals(value, ((ShallowArray<TYPE>)o).value);
	}
}
