package stmbench7.impl.core;

import stmbench7.backend.ImmutableCollection;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Module;
import stmbench7.impl.backend.BagImpl;
import stmbench7.impl.backend.ImmutableCollectionImpl;

/**
 * STMBench7 benchmark Base Assembly (see the specification).
 * Default implementation.
 */
public class BaseAssemblyImpl extends AssemblyImpl implements BaseAssembly {

    private BagImpl<CompositePart> components;

    public BaseAssemblyImpl(int id, String type, int buildDate, Module module, ComplexAssembly superAssembly) {
    	super(id, type, buildDate, module, superAssembly);
		components = new BagImpl<CompositePart>();
    }

    public BaseAssemblyImpl(BaseAssemblyImpl source) {
    	super(source);
    	this.components = new BagImpl<CompositePart>(source.components);
    }
    
    public void addComponent(CompositePart component) {
    	components.add(component);
    	component.addAssembly(this);
    }

    public boolean removeComponent(CompositePart component) {
    	boolean componentExists = components.remove(component);
    	if(! componentExists) return false;
    	
    	component.removeAssembly(this);
    	return true;
    }

    public ImmutableCollection<CompositePart> getComponents() {
    	return new ImmutableCollectionImpl<CompositePart>(components);
    }
    
    @Override
    public void clearPointers() {
    	super.clearPointers();
    	components = null;
    }
}
