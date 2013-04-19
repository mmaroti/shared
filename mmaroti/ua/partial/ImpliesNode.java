package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class ImpliesNode extends Node
{
	private Node first;
	private Node second;

	public int evaluate()
	{
		int a = first.evaluate();
		if( a == 0 )
			return 1;

		int b = second.evaluate();
		if( b >= 1 )
			return 1;
			
		return a < b ? a : b;
	}

	public ImpliesNode(Node first, Node second)
	{
		this.first = first;
		this.second = second;
	}
}
