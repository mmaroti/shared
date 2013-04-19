package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class AndNode extends Node
{
	private Node subNodes[];

	public int evaluate()
	{
		int r = 1;
	
		for(int i = 0; i < subNodes.length; ++i)
		{
			int a = subNodes[i].evaluate();

			if( a == 0 )
				return 0;

			if( a < r )
				r = a;
		}
		
		return r;
	}

	public AndNode(Node subNodes[])
	{
		if( subNodes.length < 2 )
			throw new IllegalArgumentException();
			
		this.subNodes = subNodes;
	}
	
	public AndNode(Node a0, Node a1)
	{
		subNodes = new Node[2];
		subNodes[0] = a0;
		subNodes[1] = a1;
	}

	public AndNode(Node a0, Node a1, Node a2)
	{
		subNodes = new Node[3];
		subNodes[0] = a0;
		subNodes[1] = a1;
		subNodes[2] = a2;
	}
}
