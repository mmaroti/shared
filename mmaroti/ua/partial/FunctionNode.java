package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import mmaroti.ua.alg.*;

public class FunctionNode extends Node
{
	private Function function;
	private Node subNodes[];
	private int args[];

	public int evaluate()
	{
		int e = 0;
		
		for(int i = 0; i < subNodes.length; ++i)
		{
			args[i] = subNodes[i].evaluate();

			if( args[i] < e )
				e = args[i];
		}

		if( e < 0 )
			return e;

		return function.value(args);
	}

	public FunctionNode(Function function, Node subNodes[])
	{
		if( function.arity() != subNodes.length )
			throw new IllegalArgumentException();
	
		this.function = function;
		this.subNodes = subNodes;
		this.args = new int[subNodes.length];
	}
	
	public FunctionNode(Function function, Node a0)
	{
		if( function.arity() != 1 )
			throw new IllegalArgumentException();
	
		this.function = function;
		this.subNodes = new Node[1];
		this.subNodes[0] = a0;
		this.args = new int[subNodes.length];
	}

	public FunctionNode(Function function, Node a0, Node a1)
	{
		if( function.arity() != 2 )
			throw new IllegalArgumentException();
	
		this.function = function;
		this.subNodes = new Node[2];
		this.subNodes[0] = a0;
		this.subNodes[1] = a1;
		this.args = new int[subNodes.length];
	}
}
