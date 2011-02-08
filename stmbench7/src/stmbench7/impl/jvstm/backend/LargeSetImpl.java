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
	
	public LargeSetImpl() { /*Empty*/ }

	public LargeSetImpl(LargeSetImpl<E> source) {
		//TODO: ever called?
		throw new Error("LargeSetImpl(LargetSetImpl<E> source) not implemented");
	}

	public boolean add(E element) {
	    RedBlackTree<Node<E>> tree = elements.get();
	    Node<E> node = new Node<E>(element,true);
	    
	    if (contains(node,tree)) {
		return false;
	    }
	    
	    /* If we get here then there was no such node in the tree */
	    count.put(count.get() + 1);
	    elements.put(tree.put(node));
	    return true;
	}

	public boolean remove(E element) {
	    RedBlackTree<Node<E>> tree = elements.get();
	    Node<E> node = new Node<E>(element,false);
	    
	    if (!contains(node,tree)) {
		return false;
	    }
	    
	    /* If we get here then there was no such node in the tree */
	    count.put(count.get() - 1);
	    elements.put(tree.put(node));
	    return true;
	}

	public boolean contains(E element) {
	    return contains(new Node<E>(element),elements.get());
	}
	
	private boolean contains(Node<E> element,RedBlackTree<Node<E>> tree) {
	    Node<E> node = tree.get(element);
	    
	    if (node == null || !node.isActive()) 
		return false;
	    
	    return true;
	}

	public int size() {
	    return count.get();
	}

	public Iterator<E> iterator() {
		return new NodeIterator(elements.get().iterator());
	}
	
	static class Node<E extends Comparable<? super E>> implements Comparable<Node<E>> {
		private final E val;
		private final boolean active;
		
		Node(E value, boolean active) {
			this.val = value;
			this.active = active;
		}
		
		Node(E value) {
		    this(value,true);
		}

		public int compareTo(Node<E> other) {
			return val.compareTo(other.val);
		}

		boolean isActive() {
		        return active;
		}
	}
	
	class NodeIterator implements Iterator<E> {
		private Iterator<Node<E>> iter;
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
				updateNext();
				return result;
			}
		}

		public void remove() {
		    throw new Error("Remove not implemented");
		}
	}
	
	
}
