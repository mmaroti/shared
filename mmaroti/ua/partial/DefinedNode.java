package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class DefinedNode extends Node
{
	private Node subNodes[];

	public int evaluate()
	{
		for(int i = 0; i < subNodes.length; ++i)
		{
			if( subNodes[i].evaluate() < 0 )
				return 0;
		}

		return 1;
	}

	public DefinedNode(Node subNodes[])
	{
		this.subNodes = subNodes;
	}
}
