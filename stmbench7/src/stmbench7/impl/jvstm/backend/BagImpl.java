package stmbench7.impl.jvstm.backend;

import java.util.Iterator;

import jvstm.VBox;
import jvstm.VBoxInt;
import jvstm.util.Cons;
import stmbench7.annotations.ContainedInAtomic;

@ContainedInAtomic
public class BagImpl<E> implements Iterable<E> {

	@SuppressWarnings("unchecked")
	protected final VBox<Cons<E>> elements = new VBox<Cons<E>>((Cons<E>)Cons.empty());
	protected final VBoxInt size = new VBoxInt(0);

	public BagImpl() {
	}

	public BagImpl(BagImpl<E> bag){
		//TODO: really needed???
		throw new Error("BagImpl(BagImpl<E> bag) not implemented");
	}
	
	public boolean add(E element) {
		elements.put(elements.get().cons(element));
		size.inc();
		return true;
	}

	public boolean remove(E element) {
		Cons<E> oldElems = elements.get();
		Cons<E> newElems = oldElems.removeFirst(element);

		if (oldElems == newElems) {
			return false;
		} else {
			elements.put(newElems);
			size.dec();
			return true;
		}
	}

	public int size() {
		return size.getInt();
	}

	// Iterable<E> methods

	public Iterator<E> iterator() {
		return elements.get().iterator();
	}
}
