package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.backend.ImmutableCollection;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Module;
import stmbench7.impl.jvstm.backend.BagImpl;
import stmbench7.impl.jvstm.backend.ImmutableCollectionImpl;

/**
 * STMBench7 benchmark Base Assembly (see the specification).
 * Default implementation.
 */
public class BaseAssemblyImpl extends AssemblyImpl implements BaseAssembly {

	private final VBox<BagImpl<CompositePart>> components;

	public BaseAssemblyImpl(int id, String type, int buildDate, Module module, ComplexAssembly superAssembly) {
		super(id, type, buildDate, module, superAssembly);
		components = new VBox<BagImpl<CompositePart>>(new BagImpl<CompositePart>());
	}

	public BaseAssemblyImpl(BaseAssemblyImpl source) {
		super(source);
		throw new Error("BaseAssemblyImpl(BaseAssemblyImpl<E> source) not implemented");
	}

	public void addComponent(CompositePart component) {
		components.get().add(component);
		component.addAssembly(this);
	}

	public boolean removeComponent(CompositePart component) {
		boolean componentExists = components.get().remove(component);
		if(!componentExists) return false;

		component.removeAssembly(this);
		return true;
	}

	public ImmutableCollection<CompositePart> getComponents() {
		return new ImmutableCollectionImpl<CompositePart>(components.get());
	}

	@Override
	public void clearPointers() {
		super.clearPointers();
		components.put(null);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BaseAssembly)) return false;
		return super.equals(obj);
	}

	@Override
	public Object clone() {
		throw new Error(this.getClass().getCanonicalName() + ".clone() not implemented");
	}

	@Override
	public String toString() {
		String componentIds = "{ ";
		for(CompositePart component : components.get()) componentIds += component.getId() + " ";
		componentIds += "}";
		return super.toString() + ", components=" + componentIds;
	}
}
