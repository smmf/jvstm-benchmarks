package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.backend.ImmutableCollection;
import stmbench7.core.AtomicPart;
import stmbench7.core.CompositePart;
import stmbench7.core.Connection;
import stmbench7.impl.jvstm.backend.ImmutableCollectionImpl;
import stmbench7.impl.jvstm.backend.SmallSetImpl;

public class AtomicPartImpl extends DesignObjImpl implements AtomicPart {

	private final VBox<Integer> x, y;
	private final VBox<SmallSetImpl<Connection>> to, from;
	private final VBox<CompositePart> partOf;

	public AtomicPartImpl(int id, String type, int buildDate, int x, int y) {
		super(id, type, buildDate);

		this.x = new VBox<Integer>(x);
		this.y = new VBox<Integer>(y);
		to = new VBox<SmallSetImpl<Connection>>(new SmallSetImpl<Connection>());
		from = new VBox<SmallSetImpl<Connection>>(new SmallSetImpl<Connection>());
		partOf =  new VBox<CompositePart>();
	}

	public AtomicPartImpl(AtomicPartImpl source) {
		super(source);
		throw new Error("AtomicPartImpl(AtomicPartImpl<E> source) not implemented");
	}

	public void connectTo(AtomicPart destination, String type, int length) {
		Connection connection = new ConnectionImpl(this, destination, type, length);
		to.get().add(connection);
		destination.addConnectionFromOtherPart(connection.getReversed());
	}

	public void addConnectionFromOtherPart(Connection connection) {
		from.get().add(connection);
	}

	public void setCompositePart(CompositePart partOf) {
		this.partOf.put(partOf);
	}

	public int getNumToConnections() {
		return to.get().size();
	}

	public ImmutableCollection<Connection> getToConnections() {
		return new ImmutableCollectionImpl<Connection>(to.get());
	}

	public ImmutableCollection<Connection> getFromConnections() {
		return new ImmutableCollectionImpl<Connection>(from.get());
	}

	public CompositePart getPartOf() {
		return partOf.get();
	}

	public void swapXY() {
		int tmp = y.get();
		y.put(x.get());
		x.put(tmp);
	}

	public int getX() {
		return x.get();
	}

	public int getY() {
		return y.get();
	}

	public void clearPointers() {
		x.put(0);
		y.put(0);
		to.put(null);
		from.put(null);
		partOf.put(null);
	}

	public int compareTo(AtomicPart part) {
		return id - part.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AtomicPart)) return false;
		return ((AtomicPart) obj).getId() == id;
	}

	@Override
	public Object clone() {
		throw new Error(this.getClass().getCanonicalName() + ".clone() not implemented");
	}

	@Override
	public String toString() {
		return super.toString() + ", x=" + x.get() + ", y=" + y.get() + ", partOf=[" + partOf.get() + "]" +
			", to={ " + to.get().size() + " connections }, from={ " + from.get().size() + " connections }";
	}

}
