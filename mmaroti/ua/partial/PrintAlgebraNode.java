package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import mmaroti.ua.alg.*;
import mmaroti.ua.io.*;

public class PrintAlgebraNode extends Node
{
	private Node node;
	private Algebra algebra;
	private UaWriter out;
	private int zeroValue;
	
	private int count;

	public int evaluate()
	{
		int r = node.evaluate();
		
		if( r == 1 )
		{
			out.printComment("isomorphism type #" +	Integer.toString(++count));
			out.print(algebra);
			out.println();
		}

		return r == 0 ? zeroValue : r;
	}

	public PrintAlgebraNode(Node node, Algebra algebra, 
		UaWriter out, int zeroValue )
	{
		this.node = node;
		this.algebra = algebra;
		this.out = out;
		this.zeroValue = zeroValue;

		this.count = 0;
	}
}
