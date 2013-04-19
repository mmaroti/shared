package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class Variable implements Symbol, Expression, Cloneable
{
	private String name;

	public String name() { return name; }
	public int arity() { return 0; }

	public Symbol symbol() { return this; }
	public Expression[] subNodes() { return emptyList; }
	public Expression subNode(int i) { return emptyList[i]; }
	
	private static Expression[] emptyList = new Expression[0];

	public Variable(String name)
	{
		this.name = name;
	}
	
	public Object clone()
	{
		return new Variable(name);
	}
}
