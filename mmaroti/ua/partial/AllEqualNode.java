package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */
 
public class AllEqualNode extends Node
{
	private Node nodes[];

	public int evaluate() 
	{
		int r = 1;
		int a = -1;

		for(int i = 0; i < nodes.length; ++i)
		{
			int b = nodes[i].evaluate();

			if( b < 0 )
			{
				if( b < r )
					r = b;
			}
			else
			{
				if( a < 0 )
					a = b;
				else if (a != b)
					return 0;
			}
		}
		
		return r;
	}
	
	AllEqualNode(Node nodes[])
	{
		if( nodes.length < 2 )
			throw new IllegalArgumentException();
		
		this.nodes = nodes;
	}
}
