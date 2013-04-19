package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

public class ExpressionNode implements Expression
{
	private Symbol symbol;
	private Expression[] subNodes;
	
	public Symbol symbol() { return symbol; }
	public Expression[] subNodes() { return subNodes; }

	public int arity() { return subNodes.length; }
	public Expression subNode(int i) { return subNodes[i]; }

	static private void checkArity(Symbol symbol, int arity)
	{
		if( arity < symbol.arity() || (symbol instanceof OperatorSymbol &&
			arity > ((OperatorSymbol)symbol).maxArity()) )
		{
			throw new IllegalArgumentException(
				"symbol " + symbol.name() + " cannot have " + 
				Integer.toString(arity) + " argument(s)");
		}
	}

	static private Expression[] emptyList = new Expression[0];
	public ExpressionNode(Symbol symbol)
	{
		checkArity(symbol, 0);

		this.symbol = symbol;
		this.subNodes = ExpressionNode.emptyList;
	}

	public ExpressionNode(Symbol symbol, Expression a0)
	{
		checkArity(symbol, 1);
			
		this.symbol = symbol;
		this.subNodes = new Expression[1];
		this.subNodes[0] = a0;
	}

	public ExpressionNode(Symbol symbol, Expression a0, Expression a1)
	{
		checkArity(symbol, 2);
			
		this.symbol = symbol;
		this.subNodes = new Expression[2];
		this.subNodes[0] = a0;
		this.subNodes[1] = a1;
	}

	public ExpressionNode(Symbol symbol, Expression a0, Expression a1,
		Expression a2)
	{
		checkArity(symbol, 3);
			
		this.symbol = symbol;
		this.subNodes = new Expression[3];
		this.subNodes[0] = a0;
		this.subNodes[1] = a1;
		this.subNodes[2] = a2;
	}
	
	public ExpressionNode(Symbol symbol, Expression subNodes[])
	{
		checkArity(symbol, subNodes.length);

		this.symbol = symbol;
		this.subNodes = subNodes;
	}
}
