package org.mmaroti.ua.research;

import java.util.*;

public class StahlGlpk
{
	List<String> sets = new ArrayList<String>();

	String getTuple()
	{
		if( sets.size() <= 1 )
			return sets.get(0);
		
		String tuple = "";
		for(String set : sets)
		{
			if( tuple.length() > 0 )
				tuple += ',';
			tuple += set;
		}
		
		return tuple;
	}

	String getUniverse()
	{
		String all = "";
		for(String name : sets)
		{
			if( all.length() > 0 )
				all += " + ";
			all += name;
		}
		return all;
	}
	
	void addSet(String name)
	{
		if( ! sets.contains(name) )
			sets.add(name);
	}
	
	List<String> constraints = new ArrayList<String>();
	
	void addSubsetConstraint(String smaller, String bigger)
	{
		addSet(smaller);
		addSet(bigger);
		
		String c = smaller + " <= " + bigger;
		if( ! constraints.contains(c) )
			constraints.add(c);
	}

	void removeUniverse()
	{
		constraints.add(getUniverse() + " >= 1");
	}
	
	void printVennDiagram()
	{
		System.out.println("set B := {0,1};");

		String domain = "";
		for(int i = 0; i < sets.size(); ++i)
		{
			if( domain.length() > 0 )
				domain += " cross ";
			
			if( i > 0 && i % 10 == 0 )
				domain += "\n  ";

			domain += "B";
		}

		if( constraints.size() == 0 )
			System.out.println("set C := " + domain);
		else
		{
			System.out.println("set C := { (" + getTuple() + ") in");
			System.out.println("  " + domain + " :");
			
			String line = "";
			for( String c : constraints )
			{
				if( line.length() >= 60 )
				{
					System.out.println(line + " and");
					line = "";
				}

				line += line.length() == 0 ? "  " : " and ";
				line += c;
			}
			System.out.println(line + " };");
		}
		
		System.out.println("var E{C}, integer, >= 0;");
		System.out.println();
	}

	List<String> subjecttos = new ArrayList<String>();

	void addEqualSubjectTo(String name, String value)
	{
		String subject = "sum{ (" + getTuple() + ") in C : " + name + " = 1 }\n";
		subject += "  E[" + getTuple() + "] = " + value;
		
		subjecttos.add(subject);
	}

	void printSubjectTos()
	{
		for(int i = 0; i < subjecttos.size(); ++i)
			System.out.println("subject to S" + i + " : " + subjecttos.get(i) + ";");
		
		System.out.println();
	}
	
	// the size of the circle
	int CIRCLE = 10;
	
	// the number of circles
	int LEVELS = 3;

	String getName(int level, int pos)
	{
		return "" + (char)('a' + level) + "" + (int)(pos % CIRCLE);
	}
	
	void addCircles()
	{
		for(int level = 0; level < LEVELS; ++level)
		{
			for(int pos = 0; pos < CIRCLE; ++pos)
			{
				String a = getName(level, pos);
				String b = getName(level, pos+1);

				if( (level+pos) % 2 == 0 )
					addSubsetConstraint(a, b);
				else
					addSubsetConstraint(b, a);
			}
		}
		
		for(int level = 1; level < LEVELS; ++level)
		{
			for(int pos = 0; pos < CIRCLE; ++pos)
			{
				String a = getName(level-1, pos);
				String b = getName(level, pos);

				if( (level+pos) % 2 == 0 )
					addSubsetConstraint(b, a);
				else
					addSubsetConstraint(a, b);
			}
		}
		
		for(int level = 0; level < LEVELS; ++level)
		{
			for(int pos = 0; pos < CIRCLE; ++pos)
			{
				String a = getName(level, pos);

				if( (level+pos) % 2 == 0 )
					addEqualSubjectTo(a, "MIN");
				else
					addEqualSubjectTo(a, "MAX");
			}
		}
	}

	void addTopFace()
	{
		for(int i = 0; i < CIRCLE; i += 2 )
			for(int j = 1; j < CIRCLE; j += 2 )
			{
				String a = getName(0, i);
				String b = getName(0, j);
				
				addSubsetConstraint(a, b);
			}
	}
	
	static public void main(String[] args)
	{
		StahlGlpk stahl = new StahlGlpk();
		
		stahl.addCircles();
		stahl.addTopFace();
		stahl.removeUniverse();
		stahl.printVennDiagram();

		System.out.println("param MIN := 4;");
		System.out.println("param MAX := 6;");
		System.out.println();

		System.out.println("minimize target : sum{ (" + stahl.getTuple() + ") in C }");
		System.out.println("  E[" + stahl.getTuple() + "];");
		
		
		stahl.printSubjectTos();
	}
}
