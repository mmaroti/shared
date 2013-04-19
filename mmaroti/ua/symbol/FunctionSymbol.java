package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class FunctionSymbol implements Symbol
{
	private String name;
	private int arity;

	public String name() { return name; }
	public int arity() { return arity; }
	
	public FunctionSymbol(String name, int arity)
	{
		if( arity < 0 )
			throw new IllegalArgumentException();

		this.name = name;
		this.arity = arity;
	}
}
