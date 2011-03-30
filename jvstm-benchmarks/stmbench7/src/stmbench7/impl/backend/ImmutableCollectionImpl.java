package stmbench7.impl.backend;

import java.util.Iterator;
import java.util.LinkedList;

import stmbench7.backend.ImmutableCollection;

/**
 * Implements a read-only collection of objects.
 */
public class ImmutableCollectionImpl<E> implements ImmutableCollection<E> {

	private final LinkedList<E> snapshot;
	
	public ImmutableCollectionImpl(Iterable<E> elements) {
		snapshot = new LinkedList<E>();
		for(E element : elements) snapshot.add(element);
	}

	public Iterator<E> iterator() {
		return snapshot.iterator();
	}

	public int size() {
		return snapshot.size();
	}
	
	public boolean contains(E element) {
		return snapshot.contains(element);
	}
}
