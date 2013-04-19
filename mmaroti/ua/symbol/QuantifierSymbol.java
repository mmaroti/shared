package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class QuantifierSymbol extends OperatorSymbol
{
	public int arity() { return 2; }
	public int maxArity() { return Integer.MAX_VALUE; }

	public void parse(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		if( !findLastMatch(iter) )
			return;
		
		iter.remove();
		LinkedList<Expression> args = new LinkedList<Expression>();

		Object obj;		
		do
		{
			Expression expr = getExpression(iter.next());

			if( !(expr instanceof Variable) )
				throw new IllegalArgumentException("expecting variable for " + name);

			args.add(expr);
			iter.remove();

			if( !iter.hasNext() )
				throw new IllegalArgumentException("expecting closing : after " + name);

			obj = iter.next();
			iter.remove();
			
			if( !(obj instanceof String) ||	(!obj.equals(",") && !obj.equals(":")) )
			{
				throw new IllegalArgumentException("expecting separator , or : after " + name);
			}
		} while( obj.equals(",") );

		args.add(getExpression(iter.next()));

		iter.set(new ExpressionNode(this, args.toArray(new Expression[args.size()])));
	}
	
	public QuantifierSymbol(String name, int precedence)
	{
		super(name, precedence);
	}
}
