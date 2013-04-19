package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public interface Expression
{
	public Symbol symbol();
	public Expression[] subNodes();

	public int arity();
	public Expression subNode(int i);
}
