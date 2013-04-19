package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class ForAllNode extends Node
{
	private Variable variable;
	private Node node;

	public int evaluate()
	{
		variable.value = variable.marker;

		int r = node.evaluate();
		if( r != variable.marker )
			return r;
	
		r = 1;
		
		for(variable.value = 0; variable.value < variable.maxValue; 
			++variable.value)
		{
			int a = node.evaluate();
			
			if( a == 0 )
				return 0;
				
			if( a < r )
				r = a;
		}
		
		return r;
	}

	public ForAllNode(Variable variable, Node node)
	{
		this.variable = variable;
		this.variable.takeMarker();
		this.node = node;
	}

	public ForAllNode(Variable variables[], Node node)
	{
		if( variables.length == 0 )
			throw new IllegalArgumentException();
	
		int i = variables.length;
		while( --i >= 1 )
			node = new ForAllNode(variables[i], node);
				
		this.variable = variables[0];
		this.variable.takeMarker();
		this.node = node;
		
	}
}
