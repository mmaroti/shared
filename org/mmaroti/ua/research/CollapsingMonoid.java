/**
 *	Copyright (C) Miklos Maroti, 2010
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

package org.mmaroti.ua.research;

import java.applet.*;
import java.io.*;
import java.text.*;
import java.util.*;

import org.mmaroti.ua.math.*;
import org.mmaroti.ua.csp.*;
import org.mmaroti.ua.util.*;

public class CollapsingMonoid extends Applet
{
	private static final long serialVersionUID = 7270727689298106625L;

	TransSemigroup monoid;
	Problem basicProblem;

	void setupBasicProblem()
	{
		basicProblem = new Problem();
		int size = monoid.getUniverse();

		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
				basicProblem.addVariable("t" + i + j, size);
		
		int[][] tuples = monoid.getElements().toArray(new int[monoid.getSize()][]); 
		RelationTuples relation = new RelationTuples(size, tuples);

		for(int i = 0; i < monoid.getSize(); ++i)
			for(int j = 0; j < monoid.getSize(); ++j)
			{
				String[] names = new String[size];
				for(int k = 0; k < size; ++k)
					names[k] = "t" + monoid.getElement(i)[k] + monoid.getElement(j)[k];
				
				basicProblem.addConstraint(names, relation);
			}
	}
	
	Problem derivedProblem;
	
	boolean findNontrivialSolution()
	{
		int size = monoid.getUniverse();
		Relation notAllEquals = new NotAllEquals(size);
		
		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
			{
				setupBasicProblem();
				derivedProblem = new Problem(basicProblem);
				
				String[] names = new String[size];
				for(int k = 0; k < size; ++k)
					names[k] = "t" + i + k;
				derivedProblem.addConstraint(names, notAllEquals);
				
				for(int k = 0; k < size; ++k)
					names[k] = "t" + k + i;
				derivedProblem.addConstraint(names, notAllEquals);
				
				boolean t = derivedProblem.findOneSolution();
				if( t )
					return true;
			}
		
		return false;
	}
	
	String printSolution()
	{
		String s = "";
		int size = monoid.getUniverse();

		for(int i = 0; i < size; ++i)
		{
			for(int j = 0; j < size; ++j)
				s += derivedProblem.getValue("t" + i + j)[0];
			
			s += "\n";
		}
		
		return s;
	}
	
	static int[] parseTransformation(String s, int universe)
	{
		int[] a = new int[s.length()];
		if( a.length > 10 || a.length > universe )
			throw new IllegalArgumentException("incorrect transformation");

		for(int i = 0; i < a.length; ++i)
		{
			char c = s.charAt(i);
			if( c < '0' || c > '9' )
				throw new IllegalArgumentException("illegal charater");
			
			int d = c - '0';
			if( d >= universe )
				throw new IllegalArgumentException("too large element");
			
			a[i] = d;
		}
		
		return a;
	}
	
	void parseMonoid(String args)
	{
		monoid = null;
		
		StringTokenizer tokenizer = new StringTokenizer(args);
		while( tokenizer.hasMoreTokens() )
		{
			String token = tokenizer.nextToken();

			// skip first index tag
			if( monoid == null && token.indexOf('.') >= 0 )
				continue;

			if( monoid == null )
				monoid = new TransSemigroup(token.length());

			int[] a = parseTransformation(token, monoid.getUniverse());
			monoid.add(a);
		}
	}

	public static boolean nextTransformation(int[] a)
	{
		for(int i = 0; i < a.length; ++i)
		{
			if( ++a[i] < a.length )
				return true;
			
			a[i] = 0;
		}
		
		return false;
	}
	
	HashSet<String> monoids;
	
	private void listAllMonoids(LinkedList<int[]> generators, int generatorCount)
	{
		if( generators.size() > generatorCount )
		{
			monoid = new TransSemigroup(generators.getFirst().length);

			for(int[] gen : generators)
				monoid.add(gen);
			
			monoid.calculateClosure();

			String s = monoid.printOrderedElements();
			if( monoids.add(s) )
				System.out.println("" + monoids.size() + ".\t" + s);
			
			return;
		}
		
		int[] next;
		if( generators.size() == 1 )
			next = new int[generators.getFirst().length];
		else
			next = generators.getLast().clone();
		
		generators.addLast(next);
		do
		{
			listAllMonoids(generators, generatorCount);
		}
		while( nextTransformation(next) );
		generators.removeLast();
	}
	
	public void listAllMonoids(int universe, int generatorCount)
	{
		System.out.println("# printing the at most " + generatorCount 
				+ " generated monoids on a " + universe + " element set");
		
		LinkedList<int[]> generators = new LinkedList<int[]>();
		
		int[] z = new int[universe];
		for(int i = 0; i < universe; ++i)
			z[i] = i;
		
		generators.add(z);
		monoids = new HashSet<String>();
		
		listAllMonoids(generators, generatorCount);
	}
	
	public void listAllTwoGenerated(int universe)
	{
		System.out.println("# printing two-generated collapsing monoids on a " + universe + " element set:");
		
		List<String> list = new ArrayList<String>();
		
		int[] z = new int[universe];
		for(int i = 0; i < universe; ++i)
			z[i] = i;

	
		int[] a = new int[universe];
		do
		{
			int[] b = a.clone();
			do
			{
				monoid = new TransSemigroup(universe);
				monoid.add(z);
				monoid.add(a);
				monoid.add(b);
				monoid.calculateClosure();

				String s = monoid.printOrderedElements();
				if( list.contains(s) )
					continue;
				
				setupBasicProblem();
				if( ! findNontrivialSolution() )
				{
					list.add(s);
					System.out.println("" + list.size() + ".\t" + s);
				}
			} while( nextTransformation(b) );
		} while( nextTransformation(a) );
	}
	
	public void checkAllMonoids(String filename) throws IOException
	{
		BufferedReader in;
		if( filename.equals("-") )
			in = new BufferedReader(new InputStreamReader(System.in));
		else
			in = new BufferedReader(new FileReader(filename));

		int i = 0;
		String str;
		while( (str = in.readLine()) != null )
		{
			if( str.startsWith("#") || str.startsWith("printing") )
				continue;

			parseMonoid(str);
			setupBasicProblem();
			if( ! findNontrivialSolution() )
				System.out.println("" + (++i) + ".\t" + monoid.printOrderedElements());
		}
		in.close();
	}
	
	public void listStats(String filename) throws IOException
	{
		BufferedReader in;
		if( filename.equals("-") )
			in = new BufferedReader(new InputStreamReader(System.in));
		else
			in = new BufferedReader(new FileReader(filename));

		int[] sizes = new int[1];
		
		String str;
		while( (str = in.readLine()) != null )
		{
			if( str.startsWith("#") || str.startsWith("printing") )
				continue;
			
			parseMonoid(str);
	
			if( sizes.length <= monoid.getSize() )
			{
				int[] s = new int[monoid.getSize() + 1];
				System.arraycopy(sizes, 0, s, 0, sizes.length);
				sizes = s;
			}
			
			sizes[monoid.getSize()] += 1;
		}
		in.close();
		
		for(int i = 0; i < sizes.length; ++i)
		{
			if( sizes[i] == 0 )
				continue;
			
			System.out.println("" + sizes[i] + " monoids of size " + i);
		}
	}
	
	public String getConjugates()
	{
		Set<String> conjugates = new HashSet<String>();

		PermArgument arg = new PermArgument(monoid.getUniverse());
		int[] perm = arg.vector;
		int[] invp = arg.getInverse();
		
		if( arg.reset() ) do
		{
			TransSemigroup other = new TransSemigroup(monoid.getUniverse());
			for( int[] element : monoid.getElements() )
			{
				int[] trans = new int[element.length];

				for(int i = 0; i < element.length; ++i)
					trans[i] = invp[element[perm[i]]];
				
				other.add(trans);
			}
			
			conjugates.add(other.printOrderedElements());
		} while( arg.next() );
		
		String[] strings = conjugates.toArray(new String[conjugates.size()]);
		java.util.Arrays.sort(strings);

		String result = "";
		for(int i = 0; i < strings.length; ++i)
			result += "" + (i+1) + ".\t" + strings[i] + "\n";

		return result;
	}
	
	public String solve(String input)
	{
		String output = "";

		try
		{
			long time = System.currentTimeMillis();

			parseMonoid(input);

			output += "checking monoid " + monoid.printElements() + "\n\n";

			setupBasicProblem();
			if( findNontrivialSolution() )
			{
				output += "not collapsing:\n";
				output += printSolution();
			}
			else
				output += "collapsing\n";

			output += "\nconjugacy class:\n" + getConjugates();
			
			time = System.currentTimeMillis() - time;
			DecimalFormat df = new DecimalFormat("#.###");
			output += "\nexecution time: " + df.format(0.001 * time) + " seconds";
		}
		catch(Throwable e)
		{
			return e.toString();
		}
		
		return output;
	}

	public static void main(String[] args) throws IOException
	{
		if( args.length == 0 )
		{
			System.out.println("Usage: java -jar CollapsingMonoid.jar <command>\n"
					 + "\twhere <command> is one of the following:\n\n"
					 + "  -twogen <universe> : lists all at most two generated\n"
					 + "\tcollapsing monoids on an <universe> element set.\n"
					 + "  -input <filename> : reads the given textfile and prints\n"
					 + "\tonly the collapsing ones.\n"
					 + "  -stats <filename> : lists the number of monoids in the file\n"
					 + "\tgrouped by the size of the monoid\n"
					 + "  -monoids <universe> <generators> : lists all monoids on an\n"
					 + "\t<universe> element set with at most <generators> generators.\n"
					 + "  <monoid> : checks if the given monoid is collapsing or not,\n" 
					 + "\twhere monoid is e.g. 012 000 020 111 222."
					 );
			return;
		}

		String input = "";
		for(String s : args)
			input += " " + s;
		
		CollapsingMonoid applet = new CollapsingMonoid();

		if( args[0].equals("-twogen") )
			applet.listAllTwoGenerated(Integer.parseInt(args[1]));
		else if( args[0].equals("-input") )
			applet.checkAllMonoids(args[1]);
		else if( args[0].equals("-monoids") )
			applet.listAllMonoids(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		else if( args[0].equals("-stats") )
			applet.listStats(args[1]);
		else
			System.out.print(applet.solve(input));
	}
}
