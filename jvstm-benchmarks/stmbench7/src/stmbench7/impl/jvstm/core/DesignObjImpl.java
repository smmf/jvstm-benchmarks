package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.DesignObj;

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
		//TODO: really needed???
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
		if (bd % 2 == 0)
			buildDate.put(bd-1);
		else
			buildDate.put(bd+1);
	}

	public void nullOperation() {
	}

	public String getType() {
		return type;
	}


}
