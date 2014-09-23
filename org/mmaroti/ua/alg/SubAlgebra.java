/**
 *	Copyright (C) Miklos Maroti, 2004-2005
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

package org.mmaroti.ua.alg;

import java.util.Arrays;

import org.mmaroti.ua.util.*;

public class SubAlgebra extends Algebra {
	protected Algebra base;

	/*
	 * Returns the base algebra of this subalgebra
	 */
	public Algebra getBase() {
		return base;
	}

	/**
	 * Constructs a new empty subalgebra of the provided base algebra.
	 */
	public SubAlgebra(Algebra base) {
		this.base = base;

		keys = new Object[1];
		size = 0;
		table = new int[3];
		Arrays.fill(table, EMPTY);
		emptySlots = 3;

		Operation[] operations = base.getOperations();

		this.operations = new Op[operations.length];
		for (int i = 0; i < operations.length; ++i)
			this.operations[i] = new Op(operations[i]);
	}

	protected static final int EMPTY = -1;
	protected static final int REMOVED = -2;

	/**
	 * This contains the hash code to element index mapping. The length of this
	 * array shall be a prime number. Most of the entries are EMPTY.
	 */
	protected int[] table;

	/**
	 * This integer contains the number of EMPTY slots in the table.
	 */
	protected int emptySlots;

	/**
	 * These are the contained objects in index order. It must contain at lease
	 * one empty slot.
	 */
	protected Object[] keys;

	/**
	 * The number of elements contained in this set.
	 */
	protected int size;

	public int getSize() {
		return size;
	}

	public int getIndex(Object element) {
		final int prime = table.length;
		final int hash = hashCode(element) & Integer.MAX_VALUE;
		int slot = hash % prime;

		int index = table[slot];
		if (index >= 0) {
			if (areEquals(element, keys[index]))
				return index;
		} else if (index == EMPTY)
			return -1;

		final int step = 1 + (hash % (prime - 2));

		for (;;) {
			if ((slot -= step) < 0)
				slot += prime;

			index = table[slot];
			if (index >= 0) {
				if (areEquals(element, keys[index]))
					return index;
			} else if (index == EMPTY)
				return -1;
		}
	}

	/**
	 * Returns <code>true</code> if the specified element is contained in this
	 * subset, <code>false</code> otherwise.
	 */
	public boolean contains(Object element) {
		return getIndex(element) >= 0;
	}

	public Object getElement(int index) {
		if (index < 0)
			return null;
		else if (index > size)
			throw new IllegalArgumentException("invalid index");

		return keys[index];
	}

	/**
	 * Returns the representative object from the equivalence class defined by
	 * <code>element</code> and the universe. The returned element is in the
	 * subset and equals (as defined by the base universe) to the
	 * <code>element</code>.
	 * 
	 * @param element
	 *            the sample element
	 * @return an element from the subset that is equal to the specified one, or
	 *         <code>null</code> if no such exists.
	 */
	public Object get(Object element) {
		int index = getIndex(element);

		if (index < 0)
			return null;

		return keys[index];
	}

	/**
	 * Adds a new element to the subset. If this element is already represented
	 * (that is the subset contains another one that is equal to the specified
	 * one), then the subset is not changed and the representative is returned.
	 * Otherwise the new element is inserted and it is returned.
	 * 
	 * @param element
	 *            the element to be added to the subset
	 * @return the representative from the updated subset that is equal to
	 *         <code>element</code>.
	 */
	public Object add(Object element) {
		final int prime = table.length;
		final int hash = hashCode(element) & Integer.MAX_VALUE;
		int slot = hash % prime;
		final int step = 1 + (hash % (prime - 2));
		int free = -1;

		for (;;) {
			int index = table[slot];

			if (index < 0) {
				if (free < 0)
					free = slot;

				if (index == EMPTY)
					break;
			} else if (areEquals(element, keys[index]))
				return keys[index];

			if ((slot -= step) < 0)
				slot += prime;
		}

		if (table[slot] == EMPTY)
			--emptySlots;

		table[slot] = size;
		keys[size++] = element;

		if (size == keys.length || emptySlots < keys.length)
			resize(size);

		return element;
	}

	/**
	 * Called by the {@link #remove} method when moving values from one index to
	 * another. The value at the source must be emptied (even if it is the same
	 * as the destination).
	 */
	protected void moveValue(int source, int destination) {
	}

	/**
	 * Removes an element from the subset.
	 * 
	 * @param element
	 *            the element to be removed
	 * @return the element from the set that was removed. If the specified
	 *         element was not found, then <code>null</code> is returned. If it
	 *         was found, the the equivalent element from the set is returned.
	 */
	public Object remove(Object element) {
		final int prime = table.length;
		int hash = hashCode(element) & Integer.MAX_VALUE;

		int slot = hash % prime;
		int step = 1 + (hash % (prime - 2));
		int index;

		for (;;) {
			index = table[slot];

			if (index >= 0) {
				if (areEquals(element, keys[index]))
					break;
			} else if (index == EMPTY)
				return null;

			if ((slot -= step) < 0)
				slot += prime;
		}

		// return the old object
		element = keys[index];
		table[slot] = REMOVED;

		moveValue(--size, index);

		if (index != size) {
			hash = hashCode(keys[index] = keys[size]) & Integer.MAX_VALUE;
			slot = hash % prime;

			if (table[slot] != size) {
				step = 1 + (hash % (prime - 2));

				do {
					if ((slot -= step) < 0)
						slot += prime;
				} while (table[slot] != size);
			}

			table[slot] = index;
		}

		return element;
	}

	/**
	 * List of primes used for hashing
	 */
	protected static final int[] primes = { 3, 7, 17, 37, 79, 163, 331, 673,
			1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447, 350899,
			701819, 1403641, 2807303, 5614657, 11229331, 22458671, 44917381,
			89834777, 179669557, 359339171, 718678369, 1437356741,
			Integer.MAX_VALUE // yes, it is a prime
	};

	/**
	 * Returns the smallest prime number from the list of primes that is at last
	 * as large as the specified value
	 * 
	 * @param bound
	 *            the required minimum number
	 * @return a prime larger than or equal to <code>bound</code>
	 */
	protected static final int nextPrime(int bound) {
		int i = Arrays.binarySearch(primes, bound);
		if (i < 0)
			i = -i - 1; // choose the next prime larger

		return primes[i];
	}

	/**
	 * Maps should implement this method to resize the values array to the new
	 * capacity. The value array is also cleared by this method when called with
	 * <code>0</code>.
	 */
	protected void resizeValues(int capacity) {
	}

	/**
	 * Clears this set.
	 */
	public void clear() {
		size = 0;
		resize(0);
	}

	/**
	 * Recreates the hash table so that it can hold at least
	 * <code>capacity</code> many objects in total.
	 * 
	 * @param capacity
	 *            the required
	 */
	public void resize(int capacity) {
		if (capacity < size)
			throw new IllegalArgumentException(
					"the capacity is too low to hold all objects");

		final int prime = nextPrime(3 * capacity);
		capacity = 1 + prime / 3; // this is the new capacity

		if (capacity != keys.length) {
			Object[] old = keys;
			keys = new Object[capacity];
			System.arraycopy(old, 0, keys, 0, size);

			resizeValues(capacity);
		}

		if (prime == table.length && emptySlots == prime - size)
			return;

		table = new int[prime];
		Arrays.fill(table, EMPTY);

		outer: for (int i = 0; i < size; ++i) {
			final int hash = hashCode(keys[i]) & Integer.MAX_VALUE;

			int slot = hash % prime;

			int index = table[slot];
			if (index < 0) {
				table[slot] = i;
				continue;
			}

			final int step = 1 + (hash % (prime - 2));

			for (;;) {
				if ((slot -= step) < 0)
					slot += prime;

				index = table[slot];
				if (index < 0) {
					table[slot] = i;
					continue outer;
				}
			}
		}

		emptySlots = prime - size;
	}

	public boolean areEquals(Object elem1, Object elem2) {
		return base.areEquals(elem1, elem2);
	}

	public int hashCode(Object element) {
		return base.hashCode(element);
	}

	public String toString(Object element) {
		return base.toString(element);
	}

	public Object parse(String string) {
		return base.parse(string);
	}

	/**
	 * Generates new elements in this subalgera.
	 * 
	 * @param maxSize
	 *            the maximum number of elements this subset should have. If
	 *            Integer.MAX_VALUE is specified, then the whole subalgebra is
	 *            generated.
	 */
	public void generate(int maxSize) {
		if (getSize() >= maxSize)
			return;

		int radius = -1;
		while (++radius <= getSize()) {
			for (int i = 0; i < operations.length; ++i) {
				Op op = operations[i];
				SphereArgument arg = new SphereArgument(op.getSymbol().arity,
						radius);
				int[] iargs = arg.vector;
				Object[] oargs = op.os;

				if (arg.reset())
					do {
						for (int j = 0; j < iargs.length; ++j)
							oargs[j] = getElement(iargs[j]);

						add(op.base.getValue(oargs));
						if (getSize() >= maxSize)
							return;

					} while (arg.next());
			}
		}
	}

	/**
	 * Calculates the subalgebra generated by the contained elements.
	 */
	public void generate() {
		generate(Integer.MAX_VALUE);
	}

	/**
	 * The operations of the subalgebra
	 */
	protected Op[] operations;

	public Operation[] getOperations() {
		return operations;
	}

	protected class Op extends Operation {
		/**
		 * The operation of the enclosing algebra
		 */
		protected Operation base;

		/**
		 * Static vector of integers holding the transformed indices.
		 */
		protected Object[] os;

		protected Op(Operation op) {
			this.base = op;
			this.os = new Object[op.getSymbol().arity];
		}

		public Symbol getSymbol() {
			return base.getSymbol();
		}

		public Object getValue(Object[] args) {
			return base.getValue(args);
		}

		public int getValue(int[] args) {
			for (int i = 0; i < os.length; ++i)
				os[i] = getElement(args[i]);

			return getIndex(base.getValue(os));
		}

		public int getSize() {
			return size;
		}
	}

	public Relation[] getRelations() {
		return null;
	}
}
