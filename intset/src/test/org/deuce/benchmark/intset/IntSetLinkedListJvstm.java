package org.deuce.benchmark.intset;

import jvstm.Atomic;
import jvstm.VBox;

/**
 * @author Pascal Felber
 * @author Sergio Miguel Fernandes (adapted original code to JVSTM)
 */
public class IntSetLinkedListJvstm implements IntSet {

	public class Node {
		final private int m_value;
		private VBox<Node> m_next;

		public Node(int value, Node next) {
			m_value = value;
			m_next = new VBox(next);
		}

		public Node(int value) {
			this(value, null);
		}

		public int getValue() {
			return m_value;
		}

		public void setNext(Node next) {
		    m_next.put(next);
		}

		public Node getNext() {
		    return m_next.get();
		}
	}

	final private Node m_first;

	public IntSetLinkedListJvstm() {
		Node min = new Node(Integer.MIN_VALUE);
		Node max = new Node(Integer.MAX_VALUE);
		min.setNext(max);
		m_first = min;
	}

	@Atomic(speculativeReadOnly=false)
	public boolean add(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v != value;
		if (result) {
			previous.setNext(new Node(value, next));
		}

		return result;
	}

	@Atomic(speculativeReadOnly=false)
	public boolean remove(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v == value;
		if (result) {
			previous.setNext(next.getNext());
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = (v == value);

		return result;
	}
}
