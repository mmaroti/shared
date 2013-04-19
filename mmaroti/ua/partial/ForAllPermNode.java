package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class ForAllPermNode extends Node
{
	private Variable variable;
	private int variableIndex;

	private Variable inverse[];
	private Node node;
	
	public int evaluate()
	{
		int r = node.evaluate();

		if( r == variable.marker )
		{
			// ASSERT( variable.value == variable.marker );
		
			r = 1;
		
			for(int i = 0; i < inverse.length; ++i)
			{
				if( inverse[i].value < 0 )
				{
					// ASSERT( inverse[i].value == inverse[i].marker );
				
					variable.value = i;
					inverse[i].value = variableIndex;

					int a = node.evaluate();

					// reset the marker
					inverse[i].value = inverse[i].marker;
			
					if( a == 0 )
					{
						r = 0;
						break;
					}
				
					if( a < r )
						r = a;
				}
			}

			// reset the marker
			variable.value = variable.marker;
		}
		
		return r;
	}
	
	public ForAllPermNode(Variable variable, int variableIndex, 
		Variable inverse[], Node node)
	{
		this.variable = variable;
		this.variable.takeMarker();
		this.variableIndex = variableIndex;
		
		this.inverse = inverse;
		this.node = node;
		
		// clear the variable value
		this.variable.value = this.variable.marker;
	}
	
	public ForAllPermNode(Variable permutation[], Variable inverse[],
		Node node)
	{
		int maxValue = permutation.length;
	
		if( maxValue == 0 || maxValue != inverse.length )
			throw new IllegalArgumentException();
		
		int i = maxValue;
		while( --i >= 0 )
			node = new ForAllPermNode(inverse[i], i, permutation, node);

		i = maxValue;
		while( --i >= 1 )
			node = new ForAllPermNode(permutation[i], i, inverse, node);
		
		this.variable = permutation[0];
		this.variable.takeMarker();
		this.variableIndex = 0;
		
		this.inverse = inverse;
		this.node = node;
		
		// clear the variable value
		this.variable.value = this.variable.marker;
	}
}
