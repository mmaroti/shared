/**
 * Copyright (C) Miklos Maroti, 2011
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

package org.reflocator.cites4;

import java.io.*;
import java.util.*;

/**
 * A class that can be used to compare records.
 */
public abstract class DataProvider
{
	/**
	 * The two records are identical, they represent the same
	 * rows in their native database, although the two record 
	 * objects are usually different, and might be in different 
	 * tables. We usually silently merge identical records.
	 */
	public static final int IDENTICAL = 3;
	
	/**
	 * The two records are provably referring to the same object,
	 * but in their native database they are different rows.
	 * Usually we cannot merge equivalent records, since then
	 * a subsequent query would not be able to find an identical
	 * match.  
	 */
	public static final int EQUIVALENT = 2;

	/**
	 * The two records look very very similar, but by some small
	 * chance they could be different. Manual inspection is necessary
	 * to decide if the two records are equivalent or different. 
	 */
	public static final int SIMILAR = 1;

	/**
	 * The two records look different, there is no reason why they
	 * could be considered similar.
	 */
	public static final int DIFFERENT = 0;

	/**
	 * Executes the query and returns the list of matching records.
	 * This method may return partial records. If a partial record
	 * does not IDENTICALLY match an already downloaded record, then
	 * the <code>getFullRecord</code> method needs to be called to 
	 * get the full information.
	 */
	public abstract Table getPartialRecords(String query) throws IOException;
	
	/**
	 * This method must be called to turn a partial record into a 
	 * full one.
	 */
	public abstract Record getFullRecord(Record record) throws IOException;
	
	/**
	 * Compares the two records and returns one of the constants
	 * above.
	 */
	public abstract int compare(Record a, Record b);
	
	/**
	 * Returns the Levenshtein distance between the two strings,
	 * which is the number of insertions and deletions needed to
	 * change one string into another. (by Chas Emerick).
	 */
	public static int getLevenshteinDistance(String s, String t)
	{
		if (s == null || t == null)
			throw new IllegalArgumentException("Strings must not be null");
				
		int n = s.length();
		int m = t.length();
				
		if (n == 0)
			return m;
		else if (m == 0)
			return n;

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //place holder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // j-th character of t
		int cost; // cost

		for (i = 0; i<=n; i++)
			p[i] = i;
				
		for (j = 1; j<=m; j++)
		{
			t_j = t.charAt(j-1);
			d[0] = j;
				
			for (i=1; i<=n; i++)
			{
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		} 
				
		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}
	
	protected static Map<String,String> getOptions(String query)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		
		String[] options = query.split(",");
		for(String option : options)
		{
			String[] parts = option.split(":");
			if( parts.length != 2 )
				throw new IllegalArgumentException("Query string is not well formatted");
			
			map.put(parts[0].trim(), parts[1].trim());
		}
		
		return map;
	}
}
