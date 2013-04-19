/**
 *	Copyright (C) Miklos Maroti, 2000-2001
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.mmaroti.ua.util;

import java.io.*;
import java.util.*;

/**
 * This class aids pretty printing XML files. The generated XML file can contain
 * nested elements. Each element can have attributes and embedded data. The element 
 * and its attributes are written in a single line. The embedded data (or comments)
 * are automatically indented.
 */
public class XmlWriter
{
	/**
	 * Constructs a new XML writer based on the specified stream. Consider
	 * using the pre-constructed XML writer XmlWriter.out based on the
	 * System.out stream.
	 * @param stream The stream that is used to print the XML output.
	 */
	public XmlWriter(PrintStream stream)
	{
		this.stream = stream;
		inElement = false;
	}

	/**
	 * A predefined writer that prints to the standard output.
	 */
	public static XmlWriter out = new XmlWriter(System.out);

	protected PrintStream stream;
	protected Stack<String> elems = new Stack<String>();

	protected boolean inElement;
	protected boolean hasIndent;

	protected void printIndent()
	{
		int i = elems.size();
		while( --i >= 0 )
			stream.print('\t');
	}

	/**
	 * Start each element with this method. You should have a corresponding
	 * endElem method.
	 * @param name The name of the element.
	 */
	public void startElem(String name)
	{
		if( inElement )
			stream.println(">");
		
		printIndent();
		stream.print("<");
		stream.print(name);
		inElement = true;

		elems.push(name);
	}

	protected void startAttr(String name)
	{
		if( !inElement )
			throw new IllegalStateException();
		
		stream.print(' ');
		stream.print(name);
		stream.print("=\"");
	}

	protected void endAttr()
	{
		stream.print('\"');
	}

	/**
	 * Adds a new attribute to the latest element.
	 * @param name The name of the attribute
	 * @param value The value of the attribute.
	 */
	public void attr(String name, String value)
	{
		startAttr(name);
		stream.print(value);
		endAttr();
	}

	/**
	 * Adds a new attribute to the latest element.
	 * @param name The name of the attribute
	 * @param value The value of the attribute.
	 */
	public void attr(String name, int value)
	{
		startAttr(name);
		stream.print(value);
		endAttr();
	}

	/**
	 * Adds a new attribute to the latest element.
	 * @param name The name of the attribute
	 * @param value The value of the attribute.
	 */
	public void attr(String name, long value)
	{
		startAttr(name);
		stream.print(value);
		endAttr();
	}
	
	/**
	 * Adds a new attribute to the latest element.
	 * @param name The name of the attribute
	 * @param value The value of the attribute.
	 */
	public void attr(String name, boolean value)
	{
		startAttr(name);
		stream.print(value);
		endAttr();
	}

	protected void inBody()
	{
		if( inElement )
		{
			stream.println(">");
			inElement = false;
			hasIndent = false;
		}
		
		if( ! hasIndent )
		{
			printIndent();
			hasIndent = true;
		}		
	}
	
	/**
	 * Prints a single line of data. The data should not
	 * have line breaks or special characters.
	 */
	public void printLine(String data)
	{
		inBody();
		stream.println(data);
		hasIndent = false;
	}

	/**
	 * Prints a single line comment. The comment
	 * must not contain line breaks.
	 */
	public void printComment(String comment)
	{
		inBody();
		
		stream.print("<!-- ");
		stream.print(comment);
		stream.println(" -->");
		
		hasIndent = false;
	}

	/**
	 * Closes the last element.
	 */
	public void endElem()
	{
		String name = elems.pop();
		
		if( inElement )
		{
			stream.println("/>");
			inElement = false;
		}
		else
		{
			if( hasIndent )
				stream.println();
				
			printIndent();
			stream.print("</");
			stream.print(name);
			stream.println(">");
		}
		
		hasIndent = false;
	}

	/**
	 * Flushes the stream.
	 */	
	public void finalize()
	{
		stream.flush();
	}
}
