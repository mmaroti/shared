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
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.mmaroti.ua.util;

import java.lang.ref.*;
import java.util.HashMap;

/**
 * Cache maps can be used to cache results that are time consuming to compute
 * and is beneficial to remember if there is available memory. The CacheMap
 * automatically frees and removes entries when memory is running short by
 * employing soft references on the key object. This to work, no references must
 * be kept on the key object.
 */
public final class Cache<KEY, VALUE> {
	/**
	 * We wrap the cached entries in soft references, and store these soft
	 * references in a HashMap.
	 */
	protected static class Entry<KEY> extends SoftReference<KEY> {
		/**
		 * We need to store the hashCode in case the underlying object is
		 * already freed and we want to remove this object from the hashmap.
		 */
		protected int hashCode;

		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			// Entries are compared by identity
			if (o instanceof Cache.Entry)
				return this == o;

			// Everything else must be a searching Sample
			return ((Seeker<KEY>) o).equals(this);
		}

		public <VALUE> Entry(KEY referent, Cache<KEY, VALUE> map) {
			super(referent, map.queue);
			hashCode = map.comparator.hashCode(referent);
		}
	}

	/**
	 * Removes all entries from the map whose corresponding week references have
	 * been freed.
	 */
	protected void removeGarbage() {
		Reference<? extends KEY> a;
		while ((a = queue.poll()) != null)
			map.remove(a);
	}

	/**
	 * Removes all remembered canonical forms.
	 */
	public void clear() {
		map.clear();
		while (queue.poll() != null)
			;
	}

	/**
	 * The Seeker class is used to compare Entries to a new object whose
	 * canonical form we want to find.
	 */
	protected static final class Seeker<KEY> {
		public KEY sample;
		public Comparator<KEY> comparator;

		public int hashCode() {
			return comparator.hashCode(sample);
		}

		public boolean equals(Object o) {
			@SuppressWarnings("unchecked")
			KEY key = ((Entry<KEY>) o).get();

			// has been garbage collected
			if (key == null)
				return false;

			return comparator.equals(sample, key);
		}
	}

	protected HashMap<Entry<KEY>, VALUE> map = new HashMap<Entry<KEY>, VALUE>();
	protected ReferenceQueue<KEY> queue = new ReferenceQueue<KEY>();
	protected Comparator<KEY> comparator;
	protected Seeker<KEY> seeker = new Seeker<KEY>();

	/**
	 * Returns true if the key is in the map, that is, some information is
	 * associated with it.
	 */
	public boolean contains(KEY key) {
		removeGarbage();

		seeker.sample = key;
		return map.get(seeker) != null;
	}

	/**
	 * Returns the cached value associated with the specified key. If there is
	 * no value associated or it was removed because memory was tight, then
	 * <code>null</code> is returned.
	 */
	public VALUE get(KEY key) {
		removeGarbage();

		seeker.sample = key;
		return map.get(seeker);
	}

	/**
	 * Updates the cache with the new object. It is important that no other
	 * references exist to the specified key for this cache object to work,
	 * otherwise the key and its associated value will not be freed.
	 */
	public void put(KEY key, VALUE value) {
		map.put(new Entry<KEY>(key, this), value);
	}

	/**
	 * Returns the current number of cached entries.
	 */
	public int size() {
		removeGarbage();
		return map.size();
	}

	/**
	 * Creates a new cache map based on the comparator equivalence relation.
	 * 
	 * @param comparator
	 *            The comparator object that is consulted when two objects need
	 *            to be compared.
	 */
	public Cache(Comparator<KEY> comparator) {
		this.comparator = comparator;
		seeker.comparator = comparator;
	}
}
