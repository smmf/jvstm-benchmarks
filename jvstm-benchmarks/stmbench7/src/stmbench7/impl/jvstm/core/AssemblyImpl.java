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
		this.superAssembly = new VBox<ComplexAssembly>(superAssembly);
		this.module = new VBox<Module>(module);

	}

	public AssemblyImpl(AssemblyImpl source) {
		//TODO: really needed???
		super(source);
//		this.superAssembly = source.superAssembly;
//		this.module = source.module;
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
}
