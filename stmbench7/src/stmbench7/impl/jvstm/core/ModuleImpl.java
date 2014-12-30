package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.Manual;
import stmbench7.core.Module;

public class ModuleImpl extends DesignObjImpl implements Module {

	private final Manual man;
	private final VBox<ComplexAssembly> designRoot;

	public ModuleImpl(int id, String type, int buildDate, Manual man) {
		super(id, type, buildDate);

		this.man = man;
		man.setModule(this);
		designRoot = new VBox<ComplexAssembly>();
	}

	public ModuleImpl(ModuleImpl source) {
		super(source);
		throw new Error("ModuletImpl(ModuleImpl source) not implemented");
	}

	public void setDesignRoot(ComplexAssembly designRoot) {
		this.designRoot.put(designRoot);
	}

	public ComplexAssembly getDesignRoot() {
		return designRoot.get();
	}

	public Manual getManual() {
		return man;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Module)) return false;
		return super.equals(obj);
	}
}
