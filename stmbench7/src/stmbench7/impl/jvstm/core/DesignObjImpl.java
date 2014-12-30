package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.DesignObj;
import stmbench7.core.RuntimeError;

public class DesignObjImpl implements DesignObj{

	protected final int id;
	private final String type;
	private final VBox<Integer> buildDate;

	public DesignObjImpl(int id, String type, int buildDate) {
		this.id = id;
		this.type = type;
		this.buildDate = new VBox<Integer>(buildDate);
	}

	public DesignObjImpl(DesignObjImpl source) {
		throw new Error("DesingObjImpl(DesignObjImpl source) not implemented");
	}

	public int getId() {
		return id;
	}

	public int getBuildDate() {
		return buildDate.get();
	}

	public void updateBuildDate() {
		int bd = buildDate.get();
		if (bd % 2 == 0) buildDate.put(bd-1);
		else buildDate.put(bd+1);
	}

	public void nullOperation() {
		// Intentionally empty
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DesignObj)) return false;
		return ((DesignObj) obj).getId() == id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			throw new RuntimeError(e);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName() +
			": id=" + id +
			", type=" + type +
			", buildDate=" + buildDate.get();
	}

	protected String sequenceToString(Iterable<?> sequence) {
		String seqString = "{ ";
		for(Object element : sequence) seqString += element + " ";
		seqString += "}";
		return seqString;
	}

}
