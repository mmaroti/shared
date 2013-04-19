package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class InfixBinaryAssocSymbol extends OperatorSymbol
{
	public int arity() { return 2; }
	public int maxArity() { return Integer.MAX_VALUE; }

	public void parse(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		if( !findFirstMatch(iter) )
			return;

		iter.previous();
		if( !iter.hasPrevious() )
			throw new IllegalArgumentException("no left argument for operator " + name);

		LinkedList<Expression> args = new LinkedList<Expression>();

		Object obj = iter.previous();
		for(;;)
		{
			args.add(getExpression(obj));
			iter.remove();
			
			if( !removeNextMatch(iter) )
				break;
				
			if( !iter.hasNext() )
				throw new IllegalArgumentException("no right argument for operator " + name);

			obj = iter.next();			
		}
		
		iter.add(new ExpressionNode(this, args.toArray(new Expression[args.size()])));
	}
	
	public InfixBinaryAssocSymbol(String name, int precedence)
	{
		super(name, precedence);
	}
}
