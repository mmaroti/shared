package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.lang.ref.*;
import java.util.HashMap;

public class WeakHashSet
{
	protected static class Key extends WeakReference
	{
		protected int hashCode;
		public int hashCode() { return hashCode; }
		
		public boolean equals(Object o)
		{
			Key other = (Key)o;
			if( hashCode != other.hashCode )
				return false;
			
			// for garbage collected keys
			if( this == other )
				return true;

			o = this.get();				
			return o != null && o.equals(other.get());
		}

		public Key(Object referent, ReferenceQueue queue)
		{
			super(referent, queue);
			hashCode = referent.hashCode();
		}

		protected Key()
		{
			super(null);
		}		
	}

	protected static class KeyBuffer extends Key
	{
		Object referent;
		public Object get() { return referent; }
		
		void set(Object referent)
		{
			this.referent = referent;
			hashCode = referent.hashCode();
		}
	}

	protected HashMap map;
	protected ReferenceQueue queue;
	protected KeyBuffer keyBuffer;
	
	protected final void removeGarbage()
	{
		Reference a;
		while( (a = queue.poll()) != null )
			map.remove(a);
	}
	
	public void clear()
	{
		map.clear();
		while( queue.poll() != null )
			;
	}
	
	public boolean isEmpty()
	{
		removeGarbage();
		return map.isEmpty();
	}
	
	public int size()
	{
		removeGarbage();
		return map.size();
	}

	public boolean add(Object o)
	{
		removeGarbage();
		Key key = new Key(o, queue);
		return map.put(key, key) != null;
	}

	public boolean contains(Object o)
	{
		removeGarbage();
		keyBuffer.set(o);
		return map.containsKey(keyBuffer);
	}
	
	public boolean remove(Object o)
	{
		removeGarbage();
		keyBuffer.set(o);
		return map.remove(keyBuffer) != null;
	}

	public Object canonicalize(Object o)
	{
		removeGarbage();
		keyBuffer.set(o);
		Key key = (Key)map.get(keyBuffer);
		return key != null ? key.get() : null;
	}
	
	public WeakHashSet()
	{
		map = new HashMap();
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}

	public WeakHashSet(int initialCapacity)
	{
		map = new HashMap(initialCapacity);
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}

	public WeakHashSet(int initialCapacity, float loadFactor)
	{
		map = new HashMap(initialCapacity, loadFactor);
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}
}
