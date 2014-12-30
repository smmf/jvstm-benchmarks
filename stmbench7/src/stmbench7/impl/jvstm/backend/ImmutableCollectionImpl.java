package stmbench7.impl.jvstm.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stmbench7.backend.ImmutableCollection;

/**
 * Implements a read-only collection of objects.
 */
public class ImmutableCollectionImpl<E> implements ImmutableCollection<E> {

	private final List<E> snapshot;

	public ImmutableCollectionImpl(Iterable<E> elements) {
		snapshot = new ArrayList<E>();
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

	public ImmutableCollection<E> clone() {
		return new ImmutableCollectionImpl<E>(snapshot);
	}
}
