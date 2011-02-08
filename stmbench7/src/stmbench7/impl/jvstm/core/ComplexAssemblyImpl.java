package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.Parameters;
import stmbench7.backend.ImmutableCollection;
import stmbench7.core.Assembly;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.Module;
import stmbench7.core.RuntimeError;
import stmbench7.impl.jvstm.backend.ImmutableCollectionImpl;
import stmbench7.impl.jvstm.backend.SmallSetImpl;

public class ComplexAssemblyImpl extends AssemblyImpl implements ComplexAssembly {

	private final VBox<SmallSetImpl<Assembly>> subAssemblies;
	private final short level; 

	public ComplexAssemblyImpl(int id, String type, int buildDate, Module module, ComplexAssembly superAssembly) {
		super(id, type, buildDate, module, superAssembly);

		subAssemblies = new VBox<SmallSetImpl<Assembly>>(new SmallSetImpl<Assembly>());

		if(superAssembly == null)
			level = (short) Parameters.NumAssmLevels;
		else 
			level = (short)(superAssembly.getLevel() - 1);
	}

	public ComplexAssemblyImpl(ComplexAssemblyImpl source) {
		//TODO: really needed???
		super(source);
		throw new Error("ComplexAssemblyImpl(ComplexAssemblyImpl<E> source) not implemented");
	}

	public boolean addSubAssembly(Assembly assembly) {
		if(assembly instanceof BaseAssembly && level != 2)
			throw new RuntimeError("ComplexAssembly.addAssembly: BaseAssembly at wrong level!");

		boolean notAddedBefore = subAssemblies.get().add(assembly);
		return notAddedBefore;
	}

	public boolean removeSubAssembly(Assembly assembly) {
		return subAssemblies.get().remove(assembly);
	}

	public ImmutableCollection<Assembly> getSubAssemblies() {
		return new ImmutableCollectionImpl<Assembly>(subAssemblies.get());
	}

	public short getLevel() {
		return level;
	}

	@Override
	public void clearPointers() {
		super.clearPointers();
		subAssemblies.put(null);
	}	


}
