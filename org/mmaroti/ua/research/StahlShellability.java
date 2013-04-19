/**
 *	Copyright (C) Miklos Maroti, 2008
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

import java.util.*;

/**
 * @author mmaroti@math.u-szeged.hu
 */
public class StahlShellability
{
	static int size = 6;
	static int[] levels = new int[] { 2, 3, 4 };
	static int[] values;

	static List<int[]> faces = new ArrayList<int[]>();
	
	static void scanLevel(int level, int start, int completed)
	{
		if( completed > 0 )
		{
			for(int i = start; i < size; ++i)
			{
				int bit = 1 << i;
				if( (values[level] & bit) == 0 )
				{
					values[level] |= bit;
					scanLevel(level, i+1, completed-1);
					values[level] &= ~bit;
				}
			}
		}
		else if( level == levels.length - 1 )
			faces.add(values.clone());
		else
		{
			values[level+1] = values[level];
			scanLevel(level+1, 0, levels[level+1] - levels[level]);
		}
	}
	
	static void createFaces()
	{
		values = new int[levels.length];
		values[0] = 0;
		scanLevel(0, 0, levels[0]);
	}
		
	static String toBinaryString(int a)
	{
		String s = "";

		int i = size;
		while( --i >= 0 )
			s += (a & (1<<i)) != 0 ? "1" : "0";
		
		return s;
	}

	static String cycleToString(int[] cycle)
	{
		String s = "";
		
		for(int i = 0; i < cycle.length; ++i)
		{
			if( ! s.isEmpty() )
				s += " ";

			s += toBinaryString(cycle[i]);
		}
		
		return s;
	}
	
	static void printFaces()
	{
		int count = 0;
		
		Iterator<int[]> iter = faces.iterator();
		while( iter.hasNext() )
			System.out.println("" + (++count) + ". " + cycleToString(iter.next()));
	}

	static void printPolyMake()
	{
		System.out.println("_application topaz");
		System.out.println("_version 2.3");
		System.out.println("_type SimplicialComplex");
		System.out.println();

		List<Integer> vertices = new ArrayList<Integer>();
		for(int i = 0; i < faces.size(); ++i)
		{
			int[] face = faces.get(i);
			for(int j = 0; j < face.length; ++j)
				if( ! vertices.contains(new Integer(face[j])) )
					vertices.add(new Integer(face[j]));
		}

		System.out.println("VERTEX_LABELS");
		String s = "";
		for(int i = 0; i < vertices.size(); ++i)
		{
			if( i > 0 )
				s += " ";
		
			s += toBinaryString(vertices.get(i));
		}
		System.out.println(s);
		System.out.println();
		
		System.out.println("FACETS");
		for(int i = 0; i < faces.size(); ++i)
		{
			s = "";
			int[] face = faces.get(i).clone();
			
			for(int j = 0; j < face.length; ++j)
				face[j] = vertices.indexOf(new Integer(face[j]));
			
			Arrays.sort(face);

			for(int j = 0; j < face.length; ++j)
			{
				if( j > 0 )
					s += " ";
				
				s += face[j];
			}
			
			System.out.println("{" + s + "}");
		}
		System.out.println();
	}

	static int[] applyOrderedFace(int[] cycle, int start, int[] face)
	{
		if( cycle[start] != face[0] )
			return null;
		
		if( cycle[(start+1) % cycle.length] != face[1] )
			return null;

		if( cycle[(start+2) % cycle.length] == face[2] )
		{
			if( cycle.length == 3 )
				return new int[0];
			
			int[] shorter = new int[cycle.length-1];

			for(int i = 0; i < shorter.length; ++i)
				shorter[i] = cycle[(start+2+i) % cycle.length];
			
			return shorter;
		}
		else
		{
			int[] longer = new int[cycle.length+1];
			
			longer[0] = face[0];
			longer[1] = face[2];

			for(int i = 2; i < longer.length; ++i)
				longer[i] = cycle[(start-1+i) % cycle.length];
			
			return longer;
		}
	}
	
	static int[] applyFace(int[] cycle, int start, int[] face)
	{
		int[] ret;
		
		ret = applyOrderedFace(cycle, start, face);
		if( ret != null )
			return ret;
		
		ret = applyOrderedFace(cycle, start, new int[] { face[0], face[2], face[1] });
		if( ret != null )
			return ret;
		
		ret = applyOrderedFace(cycle, start, new int[] { face[1], face[0], face[2] });
		if( ret != null )
			return ret;
		
		ret = applyOrderedFace(cycle, start, new int[] { face[1], face[2], face[0] });
		if( ret != null )
			return ret;
		
		ret = applyOrderedFace(cycle, start, new int[] { face[2], face[0], face[1] });
		if( ret != null )
			return ret;
		
		return applyOrderedFace(cycle, start, new int[] { face[2], face[1], face[0] });
	}

	static int[] applyFace(int[] cycle, int[] face)
	{
		int[] ret;
		
		for(int start = 0; start < cycle.length; ++start)
		{
			ret = applyFace(cycle, start, face);
			if( ret != null )
				return ret;
		}
		
		return null;
	}

	static boolean hasDuplicate(int[] cycle)
	{
		for(int i = 0; i < cycle.length; ++i)
			for(int j = i + 1; j < cycle.length; ++j)
				if( cycle[i] == cycle[j] )
					return true;
		
		return false;
	}
	
	static int[] removeElement(int[] cycle, int index)
	{
		int[] shorter = new int[cycle.length - 1];

		for(int i = 0; i < index; ++i)
			shorter[i] = cycle[i];
		
		for(int i = index + 1; i < cycle.length; ++i)
			shorter[i-1] = cycle[i];
		
		return shorter;
	}
	
	static public void main(String[] args)
	{
		createFaces();
//		printFaces();
		
//		int[] cycle = new int[] { 0x03, 0x0f, 0x0c, 0x3c, 0x30, 0x33 };
	}
}
