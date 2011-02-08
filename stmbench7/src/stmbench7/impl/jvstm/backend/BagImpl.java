package stmbench7.impl.jvstm.backend;

import java.util.Iterator;

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
		//TODO: really needed???
		throw new Error("BagImpl(BagImpl<E> bag) not implemented");
	}
	
	public boolean add(E element) {
		elements.put(elements.get().cons(element));
		return true;
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
		return elements.get().iterator();
	}
}
