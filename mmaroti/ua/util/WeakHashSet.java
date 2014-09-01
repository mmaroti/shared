package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.lang.ref.*;
import java.util.HashMap;

public class WeakHashSet<T> {
	protected static class Key<T> extends WeakReference<T> {
		protected int hashCode;

		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			Key<T> other = (Key<T>) o;
			if (hashCode != other.hashCode)
				return false;

			// for garbage collected keys
			if (this == other)
				return true;

			T ref = this.get();
			return ref != null && ref.equals(other.get());
		}

		public Key(T referent, ReferenceQueue<T> queue) {
			super(referent, queue);
			hashCode = referent.hashCode();
		}

		protected Key() {
			super(null);
		}
	}

	protected static class Needle<T> extends Key<T> {
		T referent;

		public T get() {
			return referent;
		}

		void set(T referent) {
			this.referent = referent;
			hashCode = referent.hashCode();
		}
	}

	protected HashMap<Key<T>, Key<T>> map;
	protected ReferenceQueue<T> queue;
	protected Needle<T> needle;

	protected final void removeGarbage() {
		Reference<? extends T> a;
		while ((a = queue.poll()) != null)
			map.remove(a);
	}

	public void clear() {
		map.clear();
		while (queue.poll() != null)
			;
	}

	public boolean isEmpty() {
		removeGarbage();
		return map.isEmpty();
	}

	public int size() {
		removeGarbage();
		return map.size();
	}

	public boolean add(T o) {
		removeGarbage();
		Key<T> key = new Key<T>(o, queue);
		return map.put(key, key) != null;
	}

	public boolean contains(T o) {
		removeGarbage();
		needle.set(o);
		return map.containsKey(needle);
	}

	public boolean remove(T o) {
		removeGarbage();
		needle.set(o);
		return map.remove(needle) != null;
	}

	public T canonicalize(T o) {
		removeGarbage();
		needle.set(o);
		Key<T> key = map.get(needle);
		return key != null ? key.get() : null;
	}

	public WeakHashSet() {
		map = new HashMap<Key<T>, Key<T>>();
		queue = new ReferenceQueue<T>();
		needle = new Needle<T>();
	}

	public WeakHashSet(int initialCapacity) {
		map = new HashMap<Key<T>, Key<T>>(initialCapacity);
		queue = new ReferenceQueue<T>();
		needle = new Needle<T>();
	}

	public WeakHashSet(int initialCapacity, float loadFactor) {
		map = new HashMap<Key<T>, Key<T>>(initialCapacity, loadFactor);
		queue = new ReferenceQueue<T>();
		needle = new Needle<T>();
	}
}
