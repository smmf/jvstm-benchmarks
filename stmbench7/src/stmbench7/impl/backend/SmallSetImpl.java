package stmbench7.impl.backend;

import java.util.ArrayList;
import java.util.Iterator;

import stmbench7.annotations.ContainedInAtomic;

/**
 * A simple implementation of a small-size set
 * (used by Assembly and AtomicPart objects).
 */
@ContainedInAtomic
public class SmallSetImpl<E> implements Iterable<E> {

	private ArrayList<E> elements;
	
	public SmallSetImpl() {
		elements = new ArrayList<E>();
	}
	
	public SmallSetImpl(SmallSetImpl<E> source) {
		elements = new ArrayList<E>(source.elements);
	}
	
	public boolean add(E element) {
		if(elements.contains(element)) return false;
		
		elements.add(element);
		return true;
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
