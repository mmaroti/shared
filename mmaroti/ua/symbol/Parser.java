package mmaroti.ua.symbol;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.*;

public class Parser
{
	private HashMap<String, FunctionSymbol> functionMap;
	private HashMap<String, OperatorSymbol> operatorMap;
	private HashMap<String, Variable> variableMap;

	public Parser(OperatorSymbol[] operatorSymbols)
	{
		functionMap = new HashMap<String, FunctionSymbol>();
		operatorMap = new HashMap<String, OperatorSymbol>();
		variableMap = new HashMap<String, Variable>();
	
		for(int i = 0; i < operatorSymbols.length; ++i)
			operatorMap.put(operatorSymbols[i].name(), operatorSymbols[i]);
	}

	public Expression parse(String input)
	{
		return reduceSingleExpression(tokenize(input));
	}

	public FunctionSymbol[] functionSymbols()
	{
		return functionMap.values().toArray(new FunctionSymbol[functionMap.size()]); 
	}

	public Variable[] variableSymbols()
	{
		return variableMap.values().toArray(new Variable[variableMap.size()]);
	}
	
	private boolean isFunctionName(String name)
	{
		return Character.isLetter(name.charAt(0)) &&
			!operatorMap.containsKey(name) &&
			!variableMap.containsKey(name);
	}

	private boolean isVariableName(String name)
	{
		return Character.isLetter(name.charAt(0)) &&
			!operatorMap.containsKey(name) &&
			!functionMap.containsKey(name);
	}

	private static String operatorChars = "`~!@#$%^&*-=+\\|[]{}<>/?:;'\",.";
	private static boolean isOperator(char ch)
	{
		return operatorChars.indexOf(ch) >= 0;
	}

	/**
	 *	Parses the input string and breaks it into a list of tokens.
	 *	Each token is either a String or a LinkedList, the later
	 *	contains tokens enclosed in parenthesis.
	 */
	public static LinkedList<Object> tokenize(String input)
	{
		Stack<LinkedList<Object>> stack = new Stack<LinkedList<Object>>();
		LinkedList<Object> list = new LinkedList<Object>();
		
		int index = 0;
		int length = input.length();
		
		while( index < length )
		{
			char ch = input.charAt(index);
			int begin = index;
			
			// identifier
			if( Character.isLetter(ch) )
			{
				do { ++index; } 
				while( index < length &&
					Character.isLetterOrDigit(input.charAt(index)) );
			
				list.add(input.substring(begin, index));
			}
			else if( Character.isWhitespace(ch) )
			{
				do{ ++index; }
				while( index < length &&
					Character.isWhitespace(input.charAt(index)) );
			}
			else if( isOperator(ch) )
			{
				do{ ++index; }
				while( index < length &&
					isOperator(input.charAt(index)) );
				
				list.add(input.substring(begin, index));
			}
			else
			{
				if( ch == '(' )
				{
					stack.push(list);
					list = new LinkedList<Object>();
				}
				else if( ch == ')' )
				{
					if( stack.empty() )
						throw new IllegalArgumentException("more closing parenthesis than opening ones");

					LinkedList<Object> subList = list;
					list = stack.pop();
					list.add(subList);
				}
				else
					throw new IllegalArgumentException("illegal character found: " + ch);
				
				++index;
			}
		}

		if( !stack.empty() )
			throw new IllegalArgumentException("more opening parenthesis than closing ones");
			
		return list;
	}

	private void parseFunctions(LinkedList<Object> list)
	{
		Object prev = null;
		Object obj = null;
	
		ListIterator<Object> iter = list.listIterator();
		while(iter.hasNext())
		{
			prev = obj;
			obj = iter.next();

			if( obj instanceof LinkedList &&
				prev instanceof String &&
				isFunctionName((String)prev) )
			{
				@SuppressWarnings("unchecked")
				LinkedList<Object> sublist = (LinkedList<Object>)obj;
				
				Expression[] args = reduceExpressionList(sublist);
				
				FunctionSymbol symbol = (FunctionSymbol)functionMap.get(prev);
				if( symbol == null )
				{
					symbol = new FunctionSymbol((String)prev, args.length);
					functionMap.put((String)prev, symbol);
				}
					
				iter.remove();
				iter.previous();
				iter.set(new ExpressionNode(symbol, args));
			}
		}
	}

	private void parseSublists(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if( obj instanceof LinkedList ) 
			{
				@SuppressWarnings("unchecked")
				LinkedList<Object> sublist = (LinkedList<Object>)obj;

				iter.set(reduceSingleExpression(sublist));
			}
		}
	}

	private void parseVariables(LinkedList<Object> list)
	{
		ListIterator<Object> iter = list.listIterator();
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if( obj instanceof String && isVariableName((String)obj) )
			{
				Variable var = (Variable)variableMap.get(obj);

				if( var == null )
				{
					var = new Variable((String)obj);
					variableMap.put((String)obj, var);
				}
				
				iter.set(var);
			}
		}
	}
	
	private OperatorSymbol findNextOperator(LinkedList<Object> list)
	{
		OperatorSymbol ret = null;
	
		ListIterator<Object> iter = list.listIterator();
		while(iter.hasNext())
		{
			Object obj = iter.next();
			if( obj instanceof String && operatorMap.containsKey(obj) )
			{
				OperatorSymbol s = (OperatorSymbol)operatorMap.get(obj);
				if( ret == null || s.precedence() > ret.precedence() )
					ret = s;
			}
		}
		
		return ret;
	}
	
	static OperatorSymbol Comma = new InfixBinaryAssocSymbol(",",0);	
	private Expression fullyReduce(LinkedList<Object> list, boolean comma)
	{
		parseFunctions(list);
		parseSublists(list);
		parseVariables(list);

		for(;;)
		{
			OperatorSymbol symbol = findNextOperator(list);

			if( symbol == null )
				break;
			
			symbol.parse(list);
		}

		if( comma )
			Comma.parse(list);
		
		if( list.size() > 1 )
			throw new IllegalArgumentException("too many objects or missing operator(s)");

		if( list.size() == 0 )
			return null;
			
		if( !(list.getFirst() instanceof Expression) )
			throw new IllegalArgumentException("invalid expression");
			
		return (Expression)list.getFirst();
	}

	private Expression[] reduceExpressionList(LinkedList<Object> list)
	{
		Expression root = fullyReduce(list, true);

		if( root == null )		
			return new Expression[0];

		if( root.symbol() == Comma )
			return root.subNodes();
		
		Expression[] ret = new Expression[1];
		ret[0] = root;
		return ret;
	}

	private Expression reduceSingleExpression(LinkedList<Object> list)
	{
		Expression root = fullyReduce(list, false);

		if( root == null )		
			throw new IllegalArgumentException("expression or subexpression is empty");
		
		return root;
	}
}
