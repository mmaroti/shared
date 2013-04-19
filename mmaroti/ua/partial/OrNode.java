package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class OrNode extends Node
{
	private Node subNodes[];

	public int evaluate()
	{
		int r = 0;
	
		for(int i = 0; i < subNodes.length; ++i)
		{
			int a = subNodes[i].evaluate();
			
			if( a == 1 ) 
				return 1;
				
			if( a < r ) 
				r = a;
		}
		
		return r;
	}

	public OrNode(Node subNodes[])
	{
		if( subNodes.length < 2 )
			throw new IllegalArgumentException();
			
		this.subNodes = subNodes;
	}

	public OrNode(Node a0, Node a1)
	{
		subNodes = new Node[2];
		subNodes[0] = a0;
		subNodes[1] = a1;
	}
}
