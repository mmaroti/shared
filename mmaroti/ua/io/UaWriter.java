package mmaroti.ua.io;

/**
 *	Copyright (C) 2000, 2001 Miklos Maroti
 */

import java.io.*;
import java.util.*;
import mmaroti.ua.alg.*;
import mmaroti.ua.util.*;

public class UaWriter
{
	private XmlWriter xml;

	public XmlWriter xmlWriter() { return xml; }

	public void printRaw(Function function)
	{
		int arity = function.arity();
		int size = function.size();

		SquareArgument arg = new SquareArgument(arity, size);
		int args[] = arg.args();

		if( arity == 0 )
		{
			xml.print(function.value(args));
			xml.println();
		}
		else
		{
			if( arg.first() ) 
			for(;;)
			{
				if( args[arity-1] != 0 )
					xml.print(' ');

				int value = function.value(args);
				xml.printPadded(value, size);

				if( ! arg.next() )
				{
					xml.println();
					break;
				}

				if( args[arity-1] == 0 )
				{
					xml.println();

					if( arity >= 3 && args[arity-2] == 0 )
						xml.println();
				}
			}
		}
	}

	public void printRaw(IntArray vector)
	{
		int length = vector.length();
		for(int i = 0; i < length; ++i)
		{
			if( i > 0 )
				xml.print(' ');
			
			xml.print(vector.get(i));
		}
	}

	public void printRaw(int[] vector)
	{
		int length = vector.length;
		for(int i = 0; i < length; ++i)
		{
			if( i > 0 )
				xml.print(' ');
			
			xml.print(vector[i]);
		}
	}

	public void print(Algebra algebra) { print(algebra, -1); }
	public void print(Algebra algebra, int index)
	{
		xml.startElem("algebra");
		xml.attr("size", algebra.size());

		if( index >= 0 )
			xml.attr("index", index);

		Function functions[] = algebra.operations();
		for(int i = 0; i < functions.length; ++i)
		{
			xml.startElem("operation");
			xml.attr("arity", functions[i].arity());
			xml.attr("size", functions[i].size());
			printRaw(functions[i]);
			xml.endElem();
		}
		
		functions = algebra.relations();
		for(int i = 0; i < functions.length; ++i)
		{
			xml.startElem("relation");
			xml.attr("arity", functions[i].arity());
			xml.attr("size", functions[i].size());
			printRaw(functions[i]);
			xml.endElem();
		}

		xml.endElem();
	}

	public void print(Equivalence equ) { print(equ, -1); }
	public void print(Equivalence equ, int index)
	{
		xml.startElem("equivalence");
		int size = equ.size();
		xml.attr("size", size);

		if( index >= 0 )
			xml.attr("index", index);

		for(int i = 0; i < size; ++i)
			if( equ.reprezentative(i) == i && equ.blockSize(i) > 1 )
			{
				xml.startElem("block");
				xml.attr("size", equ.blockSize(i));
				xml.print(i);

				for(int j = i + 1; j < size; ++j)
					if( equ.reprezentative(j) == i )
					{
						xml.print(' ');
						xml.print(j);
					}
			
				xml.endElem();
			}

		xml.endElem();
	}

	public void print(IntArray vector) { print(vector, -1); }
	public void print(IntArray vector, int index)
	{
		xml.startElem("vector");
		int length = vector.length();
		xml.attr("length", length);
		
		if( index >= 0 )
			xml.attr("index", index);
			
		printRaw(vector);

		xml.endElem();
	}

	public void print(int[] vector) { print(vector, -1); }
	public void print(int[] vector, int index)
	{
		xml.startElem("vector");
		int length = vector.length;
		xml.attr("length", length);
		
		if( index >= 0 )
			xml.attr("index", index);
			
		printRaw(vector);

		xml.endElem();
	}

	public void print(List list) { print(list, -1); }
	public void print(List list, int index)
	{
		xml.startElem("list");
		int size = list.size();
		xml.attr("size", size);

		if( index >= 0 )
			xml.attr("index", index);

		Iterator iter = list.iterator();
		index = 0;

		while( iter.hasNext() )
			print(iter.next(), index++);
		
		xml.endElem();
	}

	public void print(Set set) { print(set, -1); }
	public void print(Set set, int index)
	{
		xml.startElem("set");
		int size = set.size();
		xml.attr("size", size);

		if( index >= 0 )
			xml.attr("index", index);

		Iterator iter = set.iterator();
		index = 0;

		while( iter.hasNext() )
			print(iter.next(), index++);
		
		xml.endElem();
	}

	public void print(Integer value) { print(value, -1); }
	public void print(Integer value, int index)
	{
		xml.startElem("int");
		xml.attr("value", value.intValue());
		
		if( index >= 0 )
			xml.attr("index", index);
			
		xml.endElem();
	}

	public void print(Object obj) { print(obj, -1); }
	public void print(Object obj, int index)
	{
		if( obj instanceof Algebra )
			print((Algebra)obj, index);
		else if( obj instanceof Equivalence )
			print((Equivalence)obj, index);
		else if( obj instanceof IntArray )
			print((IntArray)obj, index);
		else if( obj instanceof int[] )
			print((int[])obj, index);
		else if( obj instanceof List )
			print((List)obj, index);
		else if( obj instanceof Integer )
			print((Integer)obj, index);
		else if( obj instanceof Set )
			print((Set)obj, index);
		else
			throw new IllegalArgumentException(
			"Unsuported Object type");
	}

	public void printComment(String data)
	{
		xml.printComment(data);
	}
	
	public void println()
	{
		xml.println();
	}

	public UaWriter(XmlWriter w)
	{
		this.xml = w;
	}
	
	public UaWriter(PrintWriter w)
	{
		this.xml = new XmlWriter(w);
	}

	public UaWriter(StringWriter w)
	{
		this.xml = new XmlWriter(new PrintWriter(w, true));
	}
	
	public static UaWriter out = 
		new UaWriter(new PrintWriter(System.out, true));
}
