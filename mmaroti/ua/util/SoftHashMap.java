package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.lang.ref.*;
import java.util.HashMap;

public class SoftHashMap<K, V> {
	protected static class Key<K> extends SoftReference<K> {
		protected int hashCode;

		public int hashCode() {
			return hashCode;
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			Key<K> other = (Key<K>) o;
			if (hashCode != other.hashCode)
				return false;

			// for garbage collected keys
			if (this == other)
				return true;

			K ref = this.get();
			return ref != null && ref.equals(other.get());
		}

		public Key(K referent, ReferenceQueue<K> queue) {
			super(referent, queue);
			hashCode = referent.hashCode();
		}

		protected Key() {
			super(null);
		}
	}

	protected static class Needle<K> extends Key<K> {
		K referent;

		public K get() {
			return referent;
		}

		void set(K referent) {
			this.referent = referent;
			hashCode = referent.hashCode();
		}
	}

	protected HashMap<Key<K>, V> map;
	protected ReferenceQueue<K> queue;
	protected Needle<K> needle;

	protected final void removeGarbage() {
		Reference<? extends K> a;
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

	public V put(K key, V value) {
		removeGarbage();
		return map.put(new Key<K>(key, queue), value);
	}

	public V get(K key) {
		removeGarbage();
		needle.set(key);
		return map.get(needle);
	}

	public boolean containsKey(K key) {
		removeGarbage();
		needle.set(key);
		return map.containsKey(needle);
	}

	public boolean remove(K key) {
		removeGarbage();
		needle.set(key);
		return map.remove(needle) != null;
	}

	public SoftHashMap() {
		map = new HashMap<Key<K>, V>();
		queue = new ReferenceQueue<K>();
		needle = new Needle<K>();
	}

	public SoftHashMap(int initialCapacity) {
		map = new HashMap<Key<K>, V>(initialCapacity);
		queue = new ReferenceQueue<K>();
		needle = new Needle<K>();
	}

	public SoftHashMap(int initialCapacity, float loadFactor) {
		map = new HashMap<Key<K>, V>(initialCapacity, loadFactor);
		queue = new ReferenceQueue<K>();
		needle = new Needle<K>();
	}
}
