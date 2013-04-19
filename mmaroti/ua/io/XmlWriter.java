package mmaroti.ua.io;

/**
 *	Copyright (C) 2000, 2001 Miklos Maroti
 */

import java.io.*;
import java.util.*;

public class XmlWriter
{
	protected PrintWriter out;
	protected Stack elems;

	protected boolean inElement;
	protected boolean hasIndent;

	public void printIndent()
	{
		int i = elems.size();
		while( --i >= 0 )
			out.print("    ");
	}

	public void startElem(String name)
	{
		if( inElement )
			out.println(">");
		
		printIndent();
		out.print("<");
		out.print(name);
		inElement = true;

		elems.push(name);
	}

	private void startAttr(String name)
	{
		if( !inElement )
			throw new IllegalStateException();
		
		out.print(' ');
		out.print(name);
		out.print("=\"");
	}

	private void endAttr()
	{
		out.print('\"');
	}

	public void attr(String name, String value)
	{
		startAttr(name);
		out.print(value);
		endAttr();
	}

	public void attr(String name, int value)
	{
		startAttr(name);
		out.print(value);
		endAttr();
	}

	public void attr(String name, long value)
	{
		startAttr(name);
		out.print(value);
		endAttr();
	}
	
	public void attr(String name, boolean value)
	{
		startAttr(name);
		out.print(value);
		endAttr();
	}

	public void inBody()
	{
		if( inElement )
		{
			out.println(">");
			inElement = false;
			hasIndent = false;
		}
		
		if( ! hasIndent )
		{
			printIndent();
			hasIndent = true;
		}		
	}
	
	public void print(String data)
	{
		inBody();
		out.print(data);
	}

	public void print(char data)
	{
		inBody();
		out.print(data);
	}

	public void print(int data)
	{
		inBody();
		out.print(data);
	}

	public void println()
	{
		inBody();
		out.println();
		hasIndent = false;
	}

	public void printPadded(int data, int max)
	{
		inBody();
	
		int space = max < 10 ? 1 : (max < 100 ? 2 : 3);
		space -= data < 10 ? 1 : (data < 100 ? 2 : 3);
	
		while( --space >= 0 )
			out.print(' ');
		
		out.print(data);
	}

	public void printComment(String data)
	{
		inBody();
		
		out.print("<!-- ");
		out.print(data);
		out.println("-->");
		
		hasIndent = false;
	}

	public void endElem()
	{
		String name = (String)elems.pop();
		
		if( inElement )
		{
			out.println("/>");
			inElement = false;
		}
		else
		{
			if( hasIndent )
				out.println();
				
			printIndent();
			out.print("</");
			out.print(name);
			out.println(">");
		}
		
		hasIndent = false;
	}
	
	public XmlWriter(PrintWriter out)
	{
		this.out = out;
		elems = new Stack();
		
		inElement = false;
	}
	
	public void finalize()
	{
		out.flush();
	}
}
