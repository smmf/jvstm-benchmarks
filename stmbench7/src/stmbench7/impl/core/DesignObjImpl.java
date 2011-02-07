package stmbench7.impl.core;

import stmbench7.core.DesignObj;

/**
 * STMBench7 benchmark Design Object (see the specification). Default
 * implementation.
 */
public class DesignObjImpl implements DesignObj {

	protected final int id;
	protected String type;
	protected int buildDate;

	public DesignObjImpl(int id, String type, int buildDate) {
		this.id = id;
		this.type = type;
		this.buildDate = buildDate;
	}

	public DesignObjImpl(DesignObjImpl source) {
		this.id = source.id;
		this.type = source.type;
		this.buildDate = source.buildDate;
	}

	public int getId() {
		return id;
	}

	public int getBuildDate() {
		return buildDate;
	}

	public void updateBuildDate() {
		if (buildDate % 2 == 0)
			buildDate--;
		else
			buildDate++;
	}

	public void nullOperation() {
	}

	public String getType() {
		return type;
	}
}
