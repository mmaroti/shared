package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class InfixBinarySingleSymbol extends OperatorSymbol
{
	public int arity() { return 2; }
	public int maxArity() { return 2; }

	public void parse(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		if( !findFirstMatch(iter) )
			return;

		Expression[] args = new Expression[2];

		if( !iter.hasNext() )
			throw new IllegalArgumentException("no right argument for operator " + name);

		args[1] = getExpression(iter.next());

		iter.remove();
		iter.previous();
		iter.remove();
		
		if( !iter.hasPrevious() )
			throw new IllegalArgumentException("no left argument for operator " + name);

		args[0] = getExpression(iter.previous());

		iter.set(new ExpressionNode(this, args));
	}
	
	public InfixBinarySingleSymbol(String name, int precedence)
	{
		super(name, precedence);
	}
}
