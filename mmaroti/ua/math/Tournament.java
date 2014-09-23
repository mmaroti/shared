package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.alg.*;
import mmaroti.ua.util.*;

public class Tournament implements Algebra
{
	protected Algebra alg;
	protected Function op;
	protected int size;

	public Function[] operations() { return alg.operations(); }
	public Function[] relations() { return alg.relations(); }
	public int size() { return size; }
	public Algebra alg() { return alg; }

	static int[] intPair = new int[2];
	public final int prod(int a, int b)
	{
		intPair[0] = a;
		intPair[1] = b;
		return op.value(intPair);
	}

	public Tournament(Algebra alg)
	{
		this.alg = alg;
		op = alg.operations()[0];
		size = alg.size();
	}

	public List<IntArray> chains(int length)
	{
		List<IntArray> list = new LinkedList<IntArray>();
		SquareArgument arg = new SquareArgument(length, size);
		int[] args = arg.args();

		if( arg.first() )
		do
		{
			int failure = length;
		
			for(int i = 0; i < length - 1; ++i)
				for(int j = i + 1; j < length; ++j)
					if( args[i] == args[j] || 
						prod(args[i], args[j]) != args[i] )
					{
						failure = j;
						i = length;
						j = length;
					}
			
			if( failure == length )
				list.add(new IntArrayBuffer(args.clone()));

			while( ++failure < length )
				args[failure] = size-1;
			
		} while( arg.next() );
		
		return list;
	}

	public List<IntPair> covers()
	{
		List<IntPair> list = new LinkedList<IntPair>();

		for(int i = 0; i < size; ++i)
			outer: for(int j = 0; j < size; ++j)
				if( i != j && prod(i,j) == i )			
				{
					for(int k = 0; k < size; ++k)
						if( i != k && j != k && 
							prod(i,k) == i && prod(k,j) == k )
						{
							continue outer;
						}

					list.add(new IntPair(i,j));
				}

		return list;
	} 

	public IntPair oneCover()
	{
		for(int i = 0; i < size; ++i)
			outer: for(int j = 0; j < size; ++j)
				if( i != j && prod(i,j) == i )			
				{
					for(int k = 0; k < size; ++k)
						if( i != k && j != k && 
							prod(i,k) == i && prod(k,j) == k )
						{
							continue outer;
						}

					return new IntPair(i, j);
				}
		
		throw new IllegalArgumentException();
	}

	public List<IntArray> triangles()
	{
		List<IntArray> list = new LinkedList<IntArray>();

		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
				for(int k = 0; k < size; ++k)
					if( i != j && prod(i,j) == i &&
						j != k && prod(j,k) == j &&
						k != i && prod(k,i) == k )
					{
						list.add(new IntArrayBuffer(i,j,k));
					}
		
		return list;
	}

	public Set<IntPair> edges()
	{
		Set<IntPair> set = new HashSet<IntPair>();
		
		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
				if( i != j && prod(i,j) == i )			
					set.add(new IntPair(i,j));
					
		return set;
	}

	public Set<IntPair> edgeTranslations(Set<IntPair> input)
	{
		Set<IntPair> ret = new HashSet<IntPair>();
	
		while( !input.isEmpty() )
		{
			IntPair pair = input.iterator().next();
			
			int a = pair.first;
			int b = pair.second;

			if( a == b || prod(a,b) != a )
				throw new IllegalArgumentException();

			input.remove(pair);
			ret.add(pair);

			for(int c = 0; c < size; ++c)
				if( b != c && prod(b,c) == c && prod(a,c) != c )
				{
					pair = new IntPair(prod(a,c), c);
					if( !ret.contains(pair) )
					{
//						System.out.print("edge: ");
//						System.out.print(a);
//						System.out.print(' ');
//						System.out.print(b);
//						System.out.print(" |- ");
//						System.out.print(pair.get(0));
//						System.out.print(' ');
//						System.out.println(pair.get(1));
					
						input.add(pair);
					}
				}
		}

		return ret;	
	}
	
	public Set<IntPair> edgeTranslations(int a, int b)
	{
		Set<IntPair> set = new HashSet<IntPair>();
		set.add(new IntPair(a,b));
		return edgeTranslations(set);
	}

	public Set<IntPair> cycleTranslations(Set<IntPair> input)
	{
		Set<IntPair> ret = new HashSet<IntPair>();
	
		while( !input.isEmpty() )
		{
			IntPair pair = input.iterator().next();
			
			int a = pair.first;
			int b = pair.second;

			if( a == b || prod(a,b) != a )
				throw new IllegalArgumentException();

			input.remove(pair);
			ret.add(pair);

			for(int c = 0; c < size; ++c)
				if( prod(b,c) == b && prod(c,a) == c )
				{
					pair = new IntPair(b, c);
//						System.out.print("cycle: ");
//						System.out.print(a);
//						System.out.print(' ');
//						System.out.print(b);
//						System.out.print(" |- ");
//						System.out.print(pair.get(0));
//						System.out.print(' ');
//						System.out.println(pair.get(1));
					
					if( !ret.contains(pair) )
					{
						input.add(pair);
					}
				}
		}

		return ret;	
	}
	
	public Set<IntPair> cycleTranslations(int a, int b)
	{
		Set<IntPair> set = new HashSet<IntPair>();
		set.add(new IntPair(a,b));
		return cycleTranslations(set);
	}

	public Equivalence closure(Set<IntPair> edges)
	{
		Equivalence equiv = Equivalence.zero(size);
		
		Iterator<IntPair> iter = edges.iterator();
		while( iter.hasNext() )
		{
			IntPair pair = iter.next();
			equiv.join(pair.first, pair.second);
		}
		
		return equiv;
	}

	public List<IntArray> cycleIdeals(Set<IntPair> edges)
	{
		List<IntArray> ideals = new LinkedList<IntArray>();

		while( !edges.isEmpty() )
		{
			IntPair pair = edges.iterator().next();
	
			Set<IntPair> ideal = cycleTranslations(pair.first, pair.second);
			edges.removeAll(ideal);

			HashSet<Integer> elems = new HashSet<Integer>();
			Iterator<IntPair> iter = ideal.iterator();
			while( iter.hasNext() )
			{
				pair = iter.next();
				elems.add(pair.first);
				elems.add(pair.second);
			}

			if( elems.size() >= 3 )
			{
//				ideals.add(ideal);
				ideals.add(new IntArrayBuffer(elems));
			}
		}
			
		return ideals;
	}
}
