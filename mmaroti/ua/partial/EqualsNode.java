package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class EqualsNode extends Node
{
	private Node left;
	private Node right;

	public int evaluate()
	{
		int a = left.evaluate();
		int b = right.evaluate();
		
		if( a < 0 || b < 0 )
			return a < b ? a : b;
		
		return a == b ? 1 : 0;
	}

	public EqualsNode(Node left, Node right)
	{
		this.left = left;
		this.right = right;
	}
}
