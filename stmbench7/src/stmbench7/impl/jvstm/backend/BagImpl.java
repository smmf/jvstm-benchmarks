package stmbench7.impl.jvstm.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jvstm.VBox;
import jvstm.util.Cons;
import stmbench7.annotations.ContainedInAtomic;

@ContainedInAtomic
public class BagImpl<E> implements Iterable<E> {

	@SuppressWarnings("unchecked")
	protected final VBox<Cons<E>> elements = new VBox<Cons<E>>((Cons<E>)Cons.empty());

	public BagImpl() {
	}

	public BagImpl(BagImpl<E> bag){
		throw new Error("BagImpl(BagImpl<E> bag) not implemented");
	}

	public void add(E element) {
		elements.put(elements.get().cons(element));
	}

	public boolean remove(E element) {
		Cons<E> oldElems = elements.get();
		Cons<E> newElems = oldElems.removeFirst(element);

		if (oldElems == newElems) {
			return false;
		} else {
			elements.put(newElems);
			return true;
		}
	}

	// Iterable<E> methods

	public Iterator<E> iterator() {
		List<E> snapshot = new ArrayList<E>();
		for(E element : elements.get()) snapshot.add(element);
		Collections.reverse(snapshot);
		return snapshot.iterator();
	}
}
