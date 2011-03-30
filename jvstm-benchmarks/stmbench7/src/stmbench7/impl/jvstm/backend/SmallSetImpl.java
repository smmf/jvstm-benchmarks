package stmbench7.impl.jvstm.backend;

import java.util.Iterator;

import jvstm.util.VLinkedSet;
import stmbench7.annotations.ContainedInAtomic;

@ContainedInAtomic
public class SmallSetImpl<E> implements Iterable<E> {

	private final VLinkedSet<E> elements;

	public SmallSetImpl() {
		elements = new VLinkedSet<E>();
	}

	public SmallSetImpl(SmallSetImpl<E> source) {
		//TODO: ever called?
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
		return elements.iterator();
	}
}
