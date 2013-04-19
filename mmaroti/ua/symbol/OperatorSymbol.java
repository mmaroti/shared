package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public abstract class OperatorSymbol implements Symbol
{
	protected String name;
	protected int precedence;

	public String name() { return name; }
	public int precedence() { return precedence; }

	public abstract int arity();
	public abstract int maxArity();
	public abstract void parse(LinkedList<Object> tokens);

	/**
	 *	Returns true if a match was found. The object 
	 *	iter.previous() is the matching string.
	 */
	protected boolean findFirstMatch(ListIterator<Object> iter)
	{
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if( obj instanceof String && name.equals(obj) )
				return true;
		}

		return false;
	}

	protected boolean findLastMatch(ListIterator<Object> iter)
	{
		while(iter.hasPrevious())
		{
			Object obj = iter.previous();
			if( obj instanceof String && name.equals(obj) )
				return true;
		}
		
		return false;
	}

	protected boolean removeNextMatch(ListIterator<Object> iter)
	{
		if( !iter.hasNext() )
			return false;
		
		Object obj = iter.next();
		if( obj instanceof String && name.equals(obj) )
		{
			iter.remove();
			return true;
		}
		
		iter.previous();
		return false;
	}

	protected Expression getExpression(Object obj)
	{
		if( !(obj instanceof Expression) )
			throw new IllegalArgumentException("invalid argument for operator " + name);
		
		return (Expression)obj;
	}

	protected OperatorSymbol(String name, int precedence)
	{
		this.name = name;
		this.precedence = precedence;
	}
}
