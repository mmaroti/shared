package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

public interface IntArray
{
	public int length();
	public int get(int index);
	public void set(int index, int value);
	public void fill(int value);
}
