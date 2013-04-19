package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class FirstOrder
{
	static public OperatorSymbol Not = new PrefixUnarySymbol("!", 4);
	static public OperatorSymbol And = new InfixBinaryAssocSymbol("&", 2);
	static public OperatorSymbol Or = new InfixBinaryAssocSymbol("|", 2);
	static public OperatorSymbol Implies = new InfixBinarySingleSymbol("->", 3);
	static public OperatorSymbol Iff = new InfixBinarySingleSymbol("<->", 3);
	static public OperatorSymbol ForAll = new QuantifierSymbol("forall", 1);
	static public OperatorSymbol Exists = new QuantifierSymbol("exists", 1);
	static public OperatorSymbol Equals = new InfixBinarySingleSymbol("=", 10);

	static public OperatorSymbol[] Connectives = { Not, And, Or, Implies, Iff };
	static public OperatorSymbol[] Quantifiers = { ForAll, Exists };
	static public OperatorSymbol[] Operators = { Not, And, Or, Implies, Iff, ForAll, Exists, Equals };

	static private HashSet<OperatorSymbol> createSet(OperatorSymbol symbols[])
	{
		HashSet<OperatorSymbol> ret = new HashSet<OperatorSymbol>();
		for(int i = 0; i < symbols.length; ++i)
			ret.add(symbols[i]);
		return ret;
	}

	static private HashSet<OperatorSymbol> connectiveSet = createSet(Connectives);
	static private HashSet<OperatorSymbol> quantifierSet = createSet(Quantifiers);
	static private HashSet<OperatorSymbol> operatorSet = createSet(Operators);

	// predicates and variables
	static public HashSet<Variable> freeVariableSet(Expression exp)
	{
		HashSet<Variable> ret;
		Expression subNodes[] = exp.subNodes();
		int i = subNodes.length;

		if( exp.symbol() instanceof Variable )
		{
			ret = new HashSet<Variable>();
			ret.add((Variable)exp.symbol());
		}
		else if( quantifierSet.contains(exp.symbol()) )
		{
			ret = freeVariableSet(subNodes[--i]);
			
			while( --i >= 0 )
				ret.remove(subNodes[i]);
		}
		else
		{
			ret = new HashSet<Variable>();

			while( --i >= 0 )
				ret.addAll(freeVariableSet(subNodes[i]));
		}

		return ret;	
	}

	// predicates and relations
	public static HashSet<Symbol> relationalSymbolSet(Expression exp)
	{
		if( quantifierSet.contains(exp.symbol()) )
			return relationalSymbolSet(exp.subNode(exp.arity()-1));
	
		HashSet<Symbol> ret = new HashSet<Symbol>();
		
		if( connectiveSet.contains(exp.symbol()) )
		{
			for(int i = 0; i < exp.arity(); ++i)
				ret.addAll(relationalSymbolSet(exp.subNode(i)));
		}
		else if( !exp.symbol().equals(Equals) )
			ret.add(exp.symbol());
				
		return ret;
	}

	public static HashSet<Symbol> functionalSymbolSet(Expression exp)
	{
		HashSet<Symbol> ret = new HashSet<Symbol>();
		
		if( !(exp.symbol() instanceof Variable) &&
			!operatorSet.contains(exp.symbol()) )
		{
			ret.add( exp.symbol() );
		}
			
		for(int i = 0; i < exp.arity(); ++i)
			ret.addAll(functionalSymbolSet(exp.subNode(i)));
			
		return ret;
	}

	public static HashSet<Variable> variableSet(Expression exp)
	{
		HashSet<Variable> ret = new HashSet<Variable>();
		
		if( exp.symbol() instanceof Variable )
			ret.add((Variable)exp.symbol());
		
		for(int i = 0; i < exp.arity(); ++i)
			ret.addAll(variableSet(exp.subNode(i)));
			
		return ret;
	}

	public static Variable[] freeVariables(Expression formula)
	{
		HashSet<Variable> ret = freeVariableSet(formula); 
		return ret.toArray(new Variable[ret.size()]);
	}

	public static Variable[] variables(Expression formula)
	{
		HashSet<Variable> ret = variableSet(formula);
		return ret.toArray(new Variable[ret.size()]);
	}

	public static Symbol[] relations(Expression formula)
	{
		HashSet<Symbol> set = relationalSymbolSet(formula);
		set.removeAll(variableSet(formula));
		return set.toArray(new Symbol[set.size()]);
	}

	public static Symbol[] operations(Expression formula)
	{
		HashSet<Symbol> set = functionalSymbolSet(formula);
		set.removeAll(relationalSymbolSet(formula));
		return set.toArray(new Symbol[set.size()]);
	}

	private static Variable createUniqueVariable(Variable old)
	{
		return (Variable)old.clone();
	}

	public static Expression replaceBoundVariables(Expression exp)
	{
		return replaceBoundVariables(exp, new HashMap<Variable, Variable>());
	}

	private static Expression replaceBoundVariables(Expression exp, HashMap<Variable, Variable> map)
	{
		if( exp.symbol() instanceof Variable )
		{
			if( map.containsKey(exp.symbol()) )
				exp = (Variable)map.get(exp.symbol());
			
			return exp;
		}

		Expression[] subNodes = exp.subNodes();
		Expression[] newNodes = new Expression[subNodes.length];

		if( quantifierSet.contains(exp.symbol()) )
		{
			HashMap<Variable, Variable> newMap = new HashMap<Variable, Variable>(map);
		
			for(int i = 0; i < subNodes.length - 1; ++i)
			{
				Variable oldVar = (Variable)subNodes[i].symbol();
				Variable newVar = createUniqueVariable(oldVar);
				
				newNodes[i] = newVar;
				newMap.put(oldVar, newVar);
			}
			
			newNodes[subNodes.length - 1] = 
				replaceBoundVariables(subNodes[subNodes.length - 1], newMap);
		}
		else
		{
			for(int i = 0; i < subNodes.length; ++i)
				newNodes[i] = replaceBoundVariables(subNodes[i], map);
		}
		
		return new ExpressionNode(exp.symbol(), newNodes);
	}
}
