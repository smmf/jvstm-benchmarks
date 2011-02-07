package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.DesignObj;

public class DesignObjImpl implements DesignObj{

	protected final VBox<Integer> id;
	protected final VBox<String> type;
	protected final VBox<Integer> buildDate;

	public DesignObjImpl(int id, String type, int buildDate) {
		this.id = new VBox<Integer>(id);
		this.type = new VBox<String>(type);
		this.buildDate = new VBox<Integer>(buildDate);
	}

	public DesignObjImpl(DesignObjImpl source) {
		//TODO: really needed???
		throw new Error("DesingObjImpl(DesignObjImpl source) not implemented");
	}

	public int getId() {
		return id.get();
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
		return type.get();
	}


}
