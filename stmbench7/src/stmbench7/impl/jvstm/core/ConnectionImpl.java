package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.AtomicPart;
import stmbench7.core.Connection;

/**
 * STMBench7 benchmark Connection (see the specification).
 * Default implementation.
 */
public class ConnectionImpl implements Connection {

	protected final VBox<String> type;
	protected final VBox<Integer> length;
	protected final VBox<AtomicPart> from, to;
	//private volatile int x;

	public ConnectionImpl(AtomicPart from, AtomicPart to, String type, int length) {
		this.type = new VBox<String>(type);
		this.length = new VBox<Integer>(length);
		this.from = new VBox<AtomicPart>(from);
		this.to = new VBox<AtomicPart>(to);
	}

	public Connection getReversed() {
		return new ConnectionImpl(to.get(), from.get(), new String(type.get()), length.get());
	}

	public AtomicPart getSource() {
		return from.get();
	}

	public AtomicPart getDestination() {
		return to.get();
	}

	public int getLength() {
		return length.get();
	}

	public String getType() {
		return type.get();
	}
}
