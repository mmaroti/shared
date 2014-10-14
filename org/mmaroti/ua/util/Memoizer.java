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

import mmaroti.ua.util.WeakHashSet.Key;

/**
 * Canonical sets can be used to keep and find canonical forms of immutable
 * objects. The use of canonical objects are especially useful for deeply
 * structured objects where checking the equality of two objects is very time
 * consuming. When these objects are stored in their canonical form, equality
 * can be checked simply by the == operator. The CanonicalSet automatically
 * frees and removes non-referenced objects (by employing weak references).
 */
public final class Memoizer<T> {
	/**
	 * We wrap the canonical objects in week references, and store these week
	 * references in a HashMap.
	 */
	protected final class Entry extends WeakReference<T> {
		/**
		 * We need to store the hashCode in case the underlying object is
		 * already freed.
		 */
		protected int hashCode;

		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			Entry other = (Entry) o;
			if (hashCode != other.hashCode)
				return false;

			// for garbage collected keys
			if (this == other)
				return true;

			return comparator.equals(this.get(), other.get());
		}

		public Entry(T referent) {
			super(referent, queue);
			hashCode = comparator.hashCode(referent);
		}
	}

	/**
	 * Removes all entries from the map whose corresponding week references have
	 * been freed.
	 */
	protected void removeGarbage() {
		Reference<? extends T> a;
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
	 * The Needle class is used to compare Entries to a new object whose
	 * canonical form we want to find.
	 */
	protected final class Needle {
		public T sample;

		public int hashCode() {
			return comparator.hashCode(sample);
		}

		public boolean equals(Object o) {
			o = ((Entry) o).get();

			// has been garbage collected
			if (o == null)
				return false;

			return comparator.equals(sample, o);
		}

		public Object cloneSample() {
			return comparator.clone(sample);
		}
	}

	protected HashMap<Entry, Entry> map = new HashMap<Entry, Entry>();
	protected ReferenceQueue<T> queue = new ReferenceQueue<T>();
	protected Unifier<T> comparator;
	protected Needle needle = new Needle();

	/**
	 * Returns true if the object is already canonicalized, false otherwise.
	 */
	public boolean contains(Object object) {
		removeGarbage();

		needle.sample = object;
		Entry entry = (Entry) map.get(needle);

		return entry != null && (object = entry.get()) != null;
	}

	/**
	 * Returns the canonical form of an object. If the CanonicalSet does not
	 * contain an object equivalent to the sample, then this sample will be
	 * cloned and become the canonical form to be returned.
	 * 
	 * @param sample
	 *            The sample object whose canonical form we want.
	 * @return The canonical form of the sample object.
	 */
	public Object memoize(Object sample) {
		removeGarbage();

		needle.sample = sample;
		Entry entry = (Entry) map.get(needle);

		if (entry != null && (sample = entry.get()) != null)
			return sample;

		sample = needle.cloneSample();
		entry = new Entry(sample, this);
		map.put(entry, entry);
		return sample;
	}

	/**
	 * Returns the current number of canonical objects that are still
	 * referenced.
	 * 
	 * @return the number of canonical objects
	 */
	public int size() {
		removeGarbage();
		return map.size();
	}

	/**
	 * Creates a new CanonicalSet that hold canonical objects based on the
	 * comparator equivalence relation.
	 * 
	 * @param comparator
	 *            The comparator object that is consulted when two objects need
	 *            to be compared.
	 */
	public Memoizer(Unifier comparator) {
		this.comparator = comparator;
		needle.comparator = comparator;
	}
}
