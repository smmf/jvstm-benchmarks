package stmbench7.impl.jvstm.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jvstm.util.VLinkedSet;
import stmbench7.annotations.ContainedInAtomic;

@ContainedInAtomic
public class SmallSetImpl<E> implements Iterable<E> {

	private final VLinkedSet<E> elements;

	public SmallSetImpl() {
		elements = new VLinkedSet<E>();
	}

	public SmallSetImpl(SmallSetImpl<E> source) {
		throw new Error("SmallSetImpl(SmallSetImpl<E> source) not implemented");
	}

	public boolean add(E element) {
		return elements.add(element);
	}

	public boolean remove(E element) {
		return elements.remove(element);
	}

	public boolean contains(E element) {
		return elements.contains(element);
	}

	public int size() {
		return elements.size();
	}

	public Iterator<E> iterator() {
		List<E> snapshot = new ArrayList<E>();
		for(E element : elements) snapshot.add(element);
		Collections.reverse(snapshot);
		return snapshot.iterator();
	}
}
