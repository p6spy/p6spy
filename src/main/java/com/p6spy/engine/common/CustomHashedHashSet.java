
package com.p6spy.engine.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * {@link HashSet} where entries are hashed using custom {@link Hasher}.
 * 
 * @author Peter Butkovic
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class CustomHashedHashSet<T> extends HashSet<T> {

	/**
	 * Maps hash code computed via {@link #hasher} to object stored in the set.
	 */
	private Map<Integer, T> map = new HashMap<Integer, T>();

	final Hasher hasher;

	public CustomHashedHashSet(final Hasher hasher) {
		super();
		this.hasher = hasher;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			if (contains(o)) {
				remove(o);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO implement
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean modified = false;
		for (T o : c) {
			if (add(o)) { 
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			if (!contains(o)) {
				remove(o);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(hasher.getHashCode(o));
	}

	@Override
	public boolean add(T o) {
		final int hash = hasher.getHashCode(o);
		if (!map.containsKey(hash)) {
			map.put(hash, o);	
			super.add(o);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		final int hash = hasher.getHashCode(o);
		if (map.containsKey(hash)) {
			super.remove(map.get(hash));
			map.remove(hash);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new CustomHashedHashSetIterator<T>(super.iterator());
	}

	@Override
	public void clear() {
		map.clear();
		super.clear();
	}

	@Override
	public Object clone() {
		// TODO implement
		throw new UnsupportedOperationException();
	}
	
	class CustomHashedHashSetIterator<E> implements Iterator<E> {

		private final Iterator<E> iterator;

		public CustomHashedHashSetIterator(Iterator<E> iterator) {
			this.iterator = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public E next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			// TODO implement
			throw new UnsupportedOperationException();
		}
	}
}
