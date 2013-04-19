package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public interface Symbol
{
	public String name();
	public int arity();

	// do not implement the hashCode and equals methods
	// we want to use the address of the Symbol object
}
