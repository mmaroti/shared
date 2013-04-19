package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class LexLessThanNode extends Node
{
	private Node first[];
	private Node second[];
	private int orEquals;

	public int evaluate()
	{
		for(int i = 0; i < first.length; ++i)
		{
			int a = first[i].evaluate();
			int b = second[i].evaluate();
			
			if( a < 0 || b < 0 )
				return a < b ? a : b;
			
			if( a != b )
				return a < b ? 1 : 0;
		}
		
		return orEquals;
	}
	
	public LexLessThanNode(Node first[], Node second[], boolean orEquals)
	{
		if( first.length != second.length )
			throw new IllegalArgumentException();
		
		this.first = first;
		this.second = second;
		this.orEquals = orEquals ? 1 : 0;
	}
}
