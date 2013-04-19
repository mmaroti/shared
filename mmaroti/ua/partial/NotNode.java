package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class NotNode extends Node
{
	private Node node;

	public int evaluate()
	{
		int a = node.evaluate();
		return a < 0 ? a : (a == 0 ? 1 : 0);
	}

	public NotNode(Node node)
	{
		this.node = node;
	}
}
