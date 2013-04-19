package mmaroti.ua.util;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.lang.ref.*;
import java.util.HashMap;

public class SoftHashMap
{
	protected static class Key extends SoftReference
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

	public Object put(Object key, Object o)
	{
		removeGarbage();
		return map.put(new Key(key, queue), o);
	}

	public Object get(Object key)
	{
		removeGarbage();
		keyBuffer.set(key);
		return map.get(keyBuffer);
	}
	
	public boolean containsKey(Object key)
	{
		removeGarbage();
		keyBuffer.set(key);
		return map.containsKey(keyBuffer);
	}
	
	public boolean remove(Object key)
	{
		removeGarbage();
		keyBuffer.set(key);
		return map.remove(keyBuffer) != null;
	}

	public SoftHashMap()
	{
		map = new HashMap();
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}

	public SoftHashMap(int initialCapacity)
	{
		map = new HashMap(initialCapacity);
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}

	public SoftHashMap(int initialCapacity, float loadFactor)
	{
		map = new HashMap(initialCapacity, loadFactor);
		queue = new ReferenceQueue();
		keyBuffer = new KeyBuffer();
	}
}
