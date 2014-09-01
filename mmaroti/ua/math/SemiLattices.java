package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.util.*;
import mmaroti.ua.alg.*;

public class SemiLattices
{
	public static List<IntPair> covers(Function op)
	{
		Functions.Binary o = new Functions.Binary(op);
	
		List<IntPair> list = new ArrayList<IntPair>();
		int size = o.size();
		
		for(int i = 0; i < size; ++i)
			outer : for(int j = 0; j < size; ++j)
				if( i != j && o.value(i,j) == i )
				{
					for(int k = 0; k < size; ++k)
						if( k != i && k != j && o.value(i,k) == i && 
								o.value(k,j) == k )
							continue outer;
					
					list.add(new IntPair(i,j));
				}
		
		return list;
	}
}
