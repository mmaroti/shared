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
	private final class Entry extends WeakReference<T> {
		/**
		 * We need to store the hashCode in case the underlying object is
		 * already freed.
		 */
		int hashCode;

		private Entry(T value) {
			super(value, queue);
			hashCode = comparator.hashCode(value);
		}

		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object obj) {
			Entry other = (Entry) obj;
			if (hashCode != other.hashCode)
				return false;

			// for garbage collected keys
			if (this == other)
				return true;

			return comparator.equals(this.get(), other.get());
		}
	}

	/**
	 * Removes all entries from the map whose corresponding week references have
	 * been freed.
	 */
	private void removeGarbage() {
		Reference<? extends T> ref;
		while ((ref = queue.poll()) != null)
			map.remove(ref);
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
		public T value;

		public int hashCode() {
			return comparator.hashCode(value);
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			T val = ((Entry) o).get();

			// has been garbage collected
			if (val == null)
				return false;

			return comparator.equals(value, val);
		}
	}

	protected final HashMap<Entry, Entry> map = new HashMap<Entry, Entry>();
	protected final ReferenceQueue<T> queue = new ReferenceQueue<T>();
	protected final Comparator<T> comparator;
	protected final Needle needle = new Needle();

	/**
	 * Returns true if the object is already canonicalized, false otherwise.
	 */
	public boolean contains(T val) {
		removeGarbage();

		needle.value = val;
		Entry entry = (Entry) map.get(needle);
		needle.value = null;

		return entry != null && (val = entry.get()) != null;
	}

	/**
	 * Returns the canonical form of an object. If the CanonicalSet does not
	 * contain an object equivalent to the sample, then this sample will be
	 * cloned and become the canonical form to be returned.
	 * 
	 * @param val
	 *            The sample object whose canonical form we want.
	 * @return The canonical form of the sample object.
	 */
	public T memoize(T val) {
		removeGarbage();

		needle.value = val;
		Entry entry = map.get(needle);
		needle.value = null;

		T val2;
		if (entry != null && (val2 = entry.get()) != null)
			return val2;

		val2 = comparator.clone(val);
		entry = new Entry(val2);
		map.put(entry, entry);

		return val2;
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
	public Memoizer(Comparator<T> comparator) {
		this.comparator = comparator;
	}
}
