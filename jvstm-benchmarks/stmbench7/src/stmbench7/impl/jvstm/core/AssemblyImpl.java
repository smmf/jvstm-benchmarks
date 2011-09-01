package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.Assembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.Module;

public abstract class AssemblyImpl extends DesignObjImpl implements Assembly {

	private final VBox<ComplexAssembly> superAssembly;
	private final VBox<Module> module;

	public AssemblyImpl(int id, String type, int buildDate, Module module, ComplexAssembly superAssembly) {
		super(id, type, buildDate);
		this.module = new VBox<Module>(module);
		this.superAssembly = new VBox<ComplexAssembly>(superAssembly);
	}

	public AssemblyImpl(AssemblyImpl source) {
		super(source);
		throw new Error("AssemblyImpl(AssemblyImpl source) not implemented");
	}

	public ComplexAssembly getSuperAssembly() {
		return superAssembly.get();
	}

	public Module getModule() {
		return module.get();
	}

	public void clearPointers() {
		superAssembly.put(null);
		module.put(null);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Assembly)) return false;
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return super.toString() + ", superAssembly=[" + superAssembly.get() + "]";
	}
}
