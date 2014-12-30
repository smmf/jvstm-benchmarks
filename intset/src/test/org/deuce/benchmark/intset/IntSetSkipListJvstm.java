package org.deuce.benchmark.intset;

import java.util.*;

import jvstm.Atomic;
import jvstm.VBox;
import jvstm.VBoxInt;

/**
 * @author Pascal Felber
 * @author Sergio Miguel Fernandes (adapted original code to JVSTM)
 */
public class IntSetSkipListJvstm implements IntSet {

	public class Node {
		final private int m_value;
		final private VBox<Node>[] m_forward;

		public Node(int level, int value) {
			m_value = value;
			m_forward = new VBox[level + 1];
			for (int i = 0; i < level + 1; i++) {
			    m_forward[i] = new VBox<Node>();
			}
		}

		public int getValue() {
			return m_value;
		}

		public int getLevel() {
			return m_forward.length - 1;
		}

		public void setForward(int level, Node next) {
			m_forward[level].put(next);
		}

		public Node getForward(int level) {
		    	return m_forward[level].get();
		}

		public String toString() {
			String result = "";
			result += "<l=" + getLevel() + ",v=" + m_value + ">:";
			for (int i = 0; i <= getLevel(); i++) {
				result += " @[" + i + "]=";
				if (m_forward[i].get() != null)
				    	result += m_forward[i].get().getValue();
				else
					result += "null";
			}
			return result;
		}
	}

	// Probability to increase level
	final private double m_probability;
	// Upper bound on the number of levels
	final private int m_maxLevel;
	// Highest level so far
	private VBoxInt m_level;
	// First element of the list
	final private Node m_head;
	// Thread-private PRNG
	final private static ThreadLocal<Random> s_random = new ThreadLocal<Random>() {
		protected synchronized Random initialValue() {
			return new Random();
		}
	};

	public IntSetSkipListJvstm(int maxLevel, double probability) {
		m_maxLevel = maxLevel;
		m_probability = probability;
		m_level = new VBoxInt(0);
		m_head = new Node(m_maxLevel, Integer.MIN_VALUE);
		Node tail = new Node(m_maxLevel, Integer.MAX_VALUE);
		for (int i = 0; i <= m_maxLevel; i++)
			m_head.setForward(i, tail);
	}

	public IntSetSkipListJvstm() {
		this(32, 0.25);
	}

	protected int randomLevel() {
		int l = 0;
		while (l < m_maxLevel && s_random.get().nextDouble() < m_probability)
			l++;
		return l;
	}

	@Atomic(speculativeReadOnly=false)
	public boolean add(int value) {
		boolean result;

		Node[] update = new Node[m_maxLevel + 1];
		Node node = m_head;

		for (int i = m_level.get(); i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
			update[i] = node;
		}
		node = node.getForward(0);

		if (node.getValue() == value) {
			result = false;
		} else {
			int level = randomLevel();
			if (level > m_level.get()) {
				for (int i = m_level.get() + 1; i <= level; i++)
					update[i] = m_head;
				m_level.put(level);
			}
			node = new Node(level, value);
			for (int i = 0; i <= level; i++) {
				node.setForward(i, update[i].getForward(i));
				update[i].setForward(i, node);
			}
			result = true;
		}

		return result;
	}

	@Atomic(speculativeReadOnly=false)
	public boolean remove(int value) {
		boolean result;

		Node[] update = new Node[m_maxLevel + 1];
		Node node = m_head;

		for (int i = m_level.get(); i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
			update[i] = node;
		}
		node = node.getForward(0);

		if (node.getValue() != value) {
			result = false;
		} else {
			int auxLimit = m_level.get();
			for (int i = 0; i <= auxLimit; i++) {
				if (update[i].getForward(i) == node)
					update[i].setForward(i, node.getForward(i));
			}
			while (m_level.get() > 0 && m_head.getForward(m_level.get()).getForward(0) == null)
				m_level.dec();
			result = true;
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result;

		Node node = m_head;

		for (int i = m_level.get(); i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
		}
		node = node.getForward(0);

		result = (node.getValue() == value);

		return result;
	}

	public String toString() {
		String result = "";

		result += "Skip list:\n";
		result += "  Level=" + m_level.get() + "\n";
		result += "  Max_level=" + m_maxLevel + "\n";
		result += "  Probability=" + m_probability + "\n";

		result += "Elements:\n";
		int[] countLevel = new int[m_maxLevel + 1];
		Node element = m_head.getForward(0);
		while (element.getValue() < Integer.MAX_VALUE) {
			countLevel[element.getLevel()]++;
			result += "  " + element.toString() + "\n";
			element = element.getForward(0);
		}

		result += "Level distribution:\n";
		for (int i = 0; i <= m_maxLevel; i++)
			result += "  #[" + i + "]=" + countLevel[i] + "\n";

		return result;
	}
}
