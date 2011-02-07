package stmbench7.impl.jvstm.backend;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jvstm.VBox;
import jvstm.util.RedBlackTree;

import stmbench7.backend.LargeSet;


public class LargeSetImpl<E extends Comparable<? super E>> implements LargeSet<E> {

	@SuppressWarnings("unchecked")
	protected final VBox<RedBlackTree<Node<E>>> elements = new VBox<RedBlackTree<Node<E>>>(RedBlackTree.EMPTY);
	protected final VBox<Integer> count = new VBox<Integer>(0);
	
	public LargeSetImpl() {	}

	public LargeSetImpl(LargeSetImpl<E> source) {
		//TODO: ever called?
		throw new Error("LargeSetImpl(LargetSetImpl<E> source) not implemented");
	}

	public boolean add(E element) {
		Node<E> entry = new Node<E>(element);
		RedBlackTree<Node<E>> tree = elements.get();
		Node<E> existing = tree.get(entry); 
		
		if (existing != null) {
			if (existing.isActive()) {
				return false;
			} else {
			        count.put(count.get() + 1);
				existing.activate();
				return true;
			}
		}
		
		/* If we get here then there was no such node in the tree */
		elements.put(tree.put(entry));
		count.put(count.get() + 1);
		return true;
	}

	public boolean remove(E element) {
		Node<E> existing = elements.get().get(new Node<E>(element));

		if ((existing != null) && existing.isActive()) {
			existing.delete();
			count.put(count.get() - 1);
			return true;
		} else {
			return false;
		}
	}

	public boolean contains(E element) {
		Node<E> node = elements.get().get(new Node<E>(element));
		
		if (node == null) return false;
		
		if (node.isActive())
			return true;
		else
			return false;
	}

	public int size() {
	    return count.get();
	}

	public Iterator<E> iterator() {
		return new NodeIterator(elements.get().iterator());
	}
	
	static class Node<E extends Comparable<? super E>> implements Comparable<Node<E>> {
		private final E val;
		private final VBox<Boolean> active = new VBox<Boolean>(true);
		
		Node(E value) {
			this.val = value;
		}

		public int compareTo(Node<E> other) {
			return val.compareTo(other.val);
		}
		
		public void delete() {
		        active.put(false);
		}

		public void activate() {
		        active.put(true);
		}
		
		boolean isActive() {
		        return active.get();
		}
	}
	
	class NodeIterator implements Iterator<E> {
		private Iterator<Node<E>> iter;
		private Node<E> lastReturned;
		private Node<E> next;

		NodeIterator(Iterator<Node<E>> iter) {
			this.iter = iter;
			updateNext();
		}

		private void updateNext() {
			while (iter.hasNext()) {
				Node<E> nextEntry = iter.next();
				if (nextEntry.isActive()) {
					next = nextEntry;
					return;
				}
			}
			next = null;
		}

		public boolean hasNext() {
			return next != null;
		}

		public E next() {
			if (next == null) {
				throw new NoSuchElementException();
			} else {
				E result = next.val;
				lastReturned = next;
				updateNext();
				return result;
			}
		}

		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}

			LargeSetImpl.this.remove(lastReturned.val);
			lastReturned = null;
		}
	}
	
	
}
