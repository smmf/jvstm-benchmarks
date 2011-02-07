package stmbench7.impl.backend;

import java.util.TreeSet;

import stmbench7.backend.LargeSet;

/**
 * A simple implementation of a large-size set
 * (used by CompositePart objects).
 * This default implementation is NOT thread-safe.
 */
public class LargeSetImpl<E> extends TreeSet<E> implements LargeSet<E> {

	private static final long serialVersionUID = -6991698966590705390L;

	public LargeSetImpl() {
		super();
	}
	
	public LargeSetImpl(LargeSetImpl<E> source) {
		super(source);
	}
}
