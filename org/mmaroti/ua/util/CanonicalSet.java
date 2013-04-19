/**
 *	Copyright (C) Miklos Maroti, 2001-2003
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
 * 59 Temple Place, Suite 330, Boston,MA 02111-1307 USA
 */

package org.mmaroti.ua.util;

import java.lang.ref.*;
import java.util.HashMap;

/**
 * Canonical sets can be used to keep and find canonical forms of 
 * immutable objects. The use of canonical objects are especially
 * useful for deeply structured objects where checking the equality
 * of two objects is very time consuming. When these objects are
 * stored in their canonical form, equality can be checked simply
 * by the == operator. The CanonicalSet automatically frees and removes
 * non-referenced objects (by employing weak references).
 */
public final class CanonicalSet
{
	/**
	 * We wrap the canonical objects in week references, 
	 * and store these week references in a HashMap.
	 */
	protected static final class Entry extends WeakReference<Object>
	{
		/**
		 * We need to store the hashCode in case 
		 * the underlying object is already freed.
		 */
		protected int hashCode;
		
		public int hashCode()
		{
			return hashCode;
		}
		
		public boolean equals(Object o)
		{
			// Entries are compared by identity
			if(o instanceof Entry)
				return this == o;

			// Everything else must be a searching Sample
			return ((Seeker)o).equals(this);
		}

		public Entry(Object referent, CanonicalSet map)
		{
			super(referent, map.queue);
			hashCode = map.comparator.hashCode(referent);
		}
	}

	/**
	 * Removes all entries from the map whose 
	 * corresponding week references have been freed.
	 */
	protected void removeGarbage()
	{
		Reference<? extends Object> a;
		while( (a = queue.poll()) != null )
			map.remove(a);
	}

	/**
	 * Removes all remembered canonical forms. 
	 */
	public void clear()
	{
		map.clear();
		while( queue.poll() != null )
			;
	}
	
	/**
	 * The Seeker class is used to compare Entries to a new object
	 * whose canonical form we want to find. 
	 */
	protected static final class Seeker
	{
		public Object sample;
		public Unifier comparator;
		
		public int hashCode()
		{
			return comparator.hashCode(sample);
		}
		
		public boolean equals(Object o)
		{
			o = ((Entry)o).get();
			
			// has been garbage collected
			if( o == null )
				return false;
				
			return comparator.equals(sample, o);
		}
		
		public Object cloneSample()
		{
			return comparator.clone(sample);
		}
	}

	protected HashMap<Entry, Object> map = new HashMap<Entry, Object>();
	protected ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
	protected Unifier comparator;
	protected Seeker seeker = new Seeker();

	/**
	 * Returns true if the object is already canonicalized,
	 * false otherwise.
	 */
	public boolean contains(Object object)
	{
		removeGarbage();
		
		seeker.sample = object;
		Entry entry = (Entry)map.get(seeker);
		
		return entry != null && (object = entry.get()) != null;
	}
	
	/**
	 * Returns the canonical form of an object. If the
	 * 	CanonicalSet does not contain an object equivalent to the sample,
	 *  then this sample will become the canonical form and be returned.
	 * @param sample The sample object whose canonical form we want.
	 * @return The canonical form of the sample object. 
	 */
	public Object canonicalize(Object sample)
	{
		removeGarbage();
		
		seeker.sample = sample;
		Entry entry = (Entry)map.get(seeker);
		
		if( entry != null && (sample = entry.get()) != null )
			return sample;
			
		sample = seeker.cloneSample();
		entry = new Entry(sample, this);
		map.put(entry, entry);
		return sample;
	}
	
	/**
	 * Returns the current number of canonical objects that are still referenced.
	 * @return the number of canonical objects
	 */
	public int size()
	{
		removeGarbage();
		return map.size();
	}

	/**
	 * Creates a new CanonicalSet that hold canonical objects
	 * 	based on the comparator equivalence relation.
	 * @param comparator The comparator object that is consulted
	 * 		when two objects need to be compared.
	 */
	public CanonicalSet(Unifier comparator)
	{
		this.comparator = comparator;
		seeker.comparator = comparator;
	}
}
