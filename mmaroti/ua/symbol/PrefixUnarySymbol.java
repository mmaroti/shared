package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class PrefixUnarySymbol extends OperatorSymbol
{
	public int arity() { return 1; }
	public int maxArity() { return 1; }

	public void parse(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if( obj instanceof String && name.equals(obj) )
			{
				iter.remove();
				
				if( !iter.hasNext() )
					throw new IllegalArgumentException("no argument for operator " + name);
				
				obj = iter.next();
				
				if( !(obj instanceof Expression) )
					throw new IllegalArgumentException("invalid argument for operator " + name);
				
				iter.set(new ExpressionNode(this, (Expression)obj));
				return;
			}
		}
	}
	
	public PrefixUnarySymbol(String name, int precedence)
	{
		super(name, precedence);
	}
}
