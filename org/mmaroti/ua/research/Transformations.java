/*
 * Created on Dec 22, 2006
 * (C)opyright
 */
package org.mmaroti.ua.research;
import java.util.*;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class Transformations
{
	public static int getInteger(char c)
	{
		if( '0' <= c && c <= '9' )
			return c - '0';
		else if( 'a' <= c && c <= 'z' )
			return 10 + c - 'a';
		else
			throw new IllegalArgumentException("illegal element, must be 0-9 and a-z");
	}
	
	public static String product(String a, String b)
	{
		if( a.length() != b.length() )
			throw new IllegalArgumentException("mappings must be of equal length");
		
		String c = new String();
		for(int i = 0; i < a.length(); ++i)
			c += a.charAt(getInteger(b.charAt(i)));
			
		return c;
	}

	public List<String> mappings = new ArrayList<String>();
	
	public void add(String s)
	{
		if( ! mappings.isEmpty() && mappings.get(0).length() != s.length() )
			throw new IllegalArgumentException("mappings must be of equal length");
		
		if( ! mappings.contains(s) )
			mappings.add(s);
	}
	
	public void closure()
	{
		int i = 0;
		while( i < mappings.size() )
		{
			String a = mappings.get(i++);
			
			int j = 0;
			while( j < mappings.size() )
			{
				String b = mappings.get(j++);
				String c = product(a, b);

				if( ! mappings.contains(c) )
					mappings.add(c);
			}
		}
	}

	public void printXml()
	{
		System.out.print(
			"<?xml version=\"1.0\"?>\n" +
			"<algebra>\n" +
			" <basicAlgebra>\n" +
			"  <algName></algName>\n" +
			"  <cardinality>" + mappings.size() + "</cardinality>\n" +
			"  <operations>\n" +
			"   <op>\n" +
			"    <opSymbol>\n" +
			"     <opName></opName>\n" +
			"     <arity>2</arity>\n" +
			"    </opSymbol>\n" +
			"    <opTable>\n" +
			"     <intArray>\n");
	
		for(int i = 0; i < mappings.size(); ++i)
		{
			System.out.print("      <row r=\"[" + i + "]\">");
			String a = mappings.get(i);

			for(int j = 0; j < mappings.size(); ++j)
			{
				String b = mappings.get(j);
				String c = product(a, b);
				
				int k = mappings.indexOf(c);
				
				System.out.print(k);
				if( j + 1 < mappings.size() )
					System.out.print(",");
			}
			
			System.out.print("</row>\n");
		}

		System.out.print(
			"     </intArray>\n" +
			"    </opTable>\n" +
			"   </op>\n" +
			"  </operations>\n" +
			" </basicAlgebra>\n" +
			"</algebra>\n");
	}
	
	public static void main(String[] args)
	{
		if( args.length == 0 )
		{
			System.err.println("Usage: java -jar trans.jar 012 010 011 000 111 222");
			return;
		}

		Transformations table = new Transformations();

		for(int i = 0; i < args.length; ++i)
			table.add(args[i]);

		table.closure();
		table.printXml();
	}
}
