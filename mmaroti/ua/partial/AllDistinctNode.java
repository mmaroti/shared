package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*; 

public class AllDistinctNode extends Node
{
	private Node nodes[];
	private Set<Integer> set;

	public int evaluate() 
	{
		int r = 1;

		for(int i = 0; i < nodes.length; ++i)
		{
			int a = nodes[i].evaluate();

			if( a < 0 )
			{
				if( a < r )
					r = a;
			}
			else if( !set.add(a) )
			{
				set.clear();
				return 0;
			}
		}

		set.clear();
		return r;
	}
	
	AllDistinctNode(Node nodes[])
	{
		if( nodes.length < 2 )
			throw new IllegalArgumentException();
		
		this.nodes = nodes;
	}
}
