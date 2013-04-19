package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public abstract class Node
{
	//	return value: >=0 : the corrent answer
	//                <0  : cannot tell the answer till the value
	//                      of this variable is not known
	//                      (with the most negative marker)

	public abstract int evaluate();
}
