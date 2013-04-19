package mmaroti.ua.partial;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

import mmaroti.ua.alg.*;
import mmaroti.ua.symbol.*;
import mmaroti.ua.io.*;

public class ModelPrinter
{
	private int size;
	private Expression formula;
	private UaWriter writer;

	private Symbol operationSymbols[];
	private Symbol relationSymbols[];
	private Symbol variableSymbols[];

	private FunctionVariable operations[];
	private FunctionVariable relations[];
	private Variable variables[];
	private Variable freeVariables[];

	private Variable variableOrder[];
	private String filter;
	
	public ModelPrinter(Expression formula, int size, UaWriter writer, String filter)
	{
		if( size <= 0 )
			throw new IllegalArgumentException();

		this.filter = filter;
		
		formula = FirstOrder.replaceBoundVariables(formula);
	
		this.formula = formula;
		this.size = size;
		this.writer = writer;

		this.operationSymbols = FirstOrder.operations(formula);
		this.relationSymbols = FirstOrder.relations(formula);
		this.variableSymbols = FirstOrder.variables(formula);
		
		operations = new FunctionVariable[operationSymbols.length];

		for(int i = 0; i < operations.length; ++i)
			operations[i] = new FunctionVariable(size,
				operationSymbols[i].arity(), size);

		relations = new FunctionVariable[relationSymbols.length];
		
		for(int i = 0; i < relations.length; ++i)
			relations[i] = new FunctionVariable(size,
				relationSymbols[i].arity(), 2);
		
		variables = new Variable[variableSymbols.length];
		
		HashSet<mmaroti.ua.symbol.Variable> freeVarSet = FirstOrder.freeVariableSet(formula);
		int c = freeVarSet.size();
		freeVariables = new Variable[c];
			
		for(int i = 0; i < variables.length; ++i)
		{
			variables[i] = new Variable(size);

			if( freeVarSet.contains(variableSymbols[i]) )
				freeVariables[--c] = variables[i];
		}
	}

	private int power(int a, int exp)
	{
		int r = 1;
		while( --exp >= 0 )
			r *= a;
		
		return r;
	}

	private int totalVariableCount()
	{
		int count = 0;
		int i;
		
		for(i = 0; i < operations.length; ++i)
			count += power(size, operations[i].arity());
		
		for(i = 0; i < relations.length; ++i)
			count += power(size, relations[i].arity());

		return count;
	}

	private Node createLexicographicNode()
	{
		FunctionVariable permutation = new FunctionVariable(size, 1, size);
		FunctionVariable inversePerm  = new FunctionVariable(size, 1, size);

		int k = totalVariableCount();

		variableOrder = new Variable[k];
		Node originalVector[] = new Node[k];
		Node permutedVector[] = new Node[k];

		int i, j;
		int radius = size;
		while( --radius >= -1 )
		{
			for(i = 0; i < operations.length; ++i)
			{
				SphereArgument arg = new SphereArgument(operations[i].arity(), radius);
				int args[] = arg.args();

				if( arg.first() )
				do
				{
					--k;

					variableOrder[k] = operations[i].variable(args);					
					originalVector[k] = variableOrder[k];
					
					Node permArgs[] = new Node[args.length];
					for(j = 0; j < args.length; ++j)
						permArgs[j] = permutation.variable(args[j]);

					permutedVector[k] = new FunctionNode(inversePerm,
						new FunctionNode(operations[i], permArgs));
				}
				while( arg.next() );
			}

			for(i = 0; i < relations.length; ++i)
			{
				SphereArgument arg = new SphereArgument(relations[i].arity(), radius);
				int args[] = arg.args();
				
				if( arg.first() )
				do
				{
					--k;

					variableOrder[k] = relations[i].variable(args);
					originalVector[k] = variableOrder[k];
					
					Node permArgs[] = new Node[args.length];
					for(j = 0; j < args.length; ++j)
						permArgs[j] = permutation.variable(args[j]);

					permutedVector[k] = 
						new FunctionNode(relations[i], permArgs);
				}
				while( arg.next() );
			}
		}
	
		return new ForAllPermNode(permutation.variables(),
			inversePerm.variables(), 
			new LexLessThanNode(originalVector, permutedVector, true));
	}

	private Variable[] quantifierVariables(Node[] args)
	{
		Variable[] ret = new Variable[args.length - 1];

		for(int i = 0; i < ret.length; ++i)
			ret[i] = (Variable)args[i];

		return ret;
	}
	
	private Node createExpressionNode(Expression exp)
	{
		Node args[] = new Node[exp.arity()];
		
		for(int i = 0; i < args.length; ++i)
			args[i] = createExpressionNode(exp.subNodes()[i]);
		
		Symbol symbol = exp.symbol();
		
		for(int i = 0; i < operationSymbols.length; ++i)
			if( symbol.equals(operationSymbols[i]) )
				return new FunctionNode(operations[i], args);
		
		for(int i = 0; i < relationSymbols.length; ++i)
			if( symbol.equals(relationSymbols[i]) )
				return new FunctionNode(relations[i], args);

		for(int i = 0; i < variableSymbols.length; ++i)
			if( symbol.equals(variableSymbols[i]) )
				return variables[i];

		if( symbol == FirstOrder.Equals )
			return new EqualsNode(args[0], args[1]);

		if( symbol == FirstOrder.Not )
			return new NotNode(args[0]);

		if( symbol == FirstOrder.And )
			return new AndNode(args);

		if( symbol == FirstOrder.Or )
			return new OrNode(args);

		if( symbol == FirstOrder.Implies )
			return new ImpliesNode(args[0], args[1]);

		if( symbol == FirstOrder.ForAll )
			return new ForAllNode(quantifierVariables(args), 
				args[args.length - 1]);

		if( symbol == FirstOrder.Exists )
			return new ExistsNode(quantifierVariables(args), 
				args[args.length - 1]);

		throw new IllegalArgumentException();
	}

	public void printAllModels()
	{
		Node node = createExpressionNode(formula);
		Algebra algebra = new AlgebraBuffer(size, operations, relations); 

		if( freeVariables.length > 0 )
			node = new ForAllNode(freeVariables, node);
		
		node = new AndNode(node, createLexicographicNode());
		
		if( filter.length() != 0 )
			node = new FilterNode(node, algebra, filter);
		
		node = new PrintAlgebraNode(node, algebra, writer, 1);

		if( variableOrder.length > 0 )
			node = new ForAllNode(variableOrder, node);
		
		node.evaluate();
	}
	
	public void printFirstModel()
	{
		Node node = createExpressionNode(formula);
		Algebra algebra = new AlgebraBuffer(size, operations, relations); 

		if( freeVariables.length > 0 )
			node = new ForAllNode(freeVariables, node);
		
		node = new AndNode(node, createLexicographicNode());
		
		if( filter.length() != 0 )
			node = new FilterNode(node, algebra, filter);
		
		node = new PrintAlgebraNode(node, algebra, writer, 0);

		node = new ExistsNode(variableOrder, node);
		
		node.evaluate();
	}
}
