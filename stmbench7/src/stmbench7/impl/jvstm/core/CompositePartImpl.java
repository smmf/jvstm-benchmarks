package stmbench7.impl.jvstm.core;

import jvstm.VBox;
import stmbench7.backend.BackendFactory;
import stmbench7.backend.ImmutableCollection;
import stmbench7.backend.LargeSet;
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Document;
import stmbench7.impl.jvstm.backend.BagImpl;
import stmbench7.impl.jvstm.backend.ImmutableCollectionImpl;

/**
 * STMBench7 benchmark Composite Part (see the specification).
 */
public class CompositePartImpl extends DesignObjImpl implements CompositePart {

	private final VBox<Document> documentation;
	private final VBox<BagImpl<BaseAssembly>> usedIn;
	private final VBox<LargeSet<AtomicPart>> parts;
	private final VBox<AtomicPart> rootPart;

	public CompositePartImpl(int id, String type, int buildDate, Document documentation) {
		super(id, type, buildDate);

		this.documentation = new VBox<Document>(documentation);
		documentation.setPart(this);
		usedIn = new VBox<BagImpl<BaseAssembly>>(new BagImpl<BaseAssembly>());
		LargeSet<AtomicPart> set = BackendFactory.instance.createLargeSet();
		parts = new VBox<LargeSet<AtomicPart>>(set);
		rootPart = new VBox<AtomicPart>();
	}

	public CompositePartImpl(CompositePartImpl source) {
		super(source);
		throw new Error("CompositePartImpl(CompositePartImpl source) not implemented");
	}

	public void addAssembly(BaseAssembly assembly) {
		usedIn.get().add(assembly);
	}

	public boolean addPart(AtomicPart part) {
		boolean notAddedBefore = parts.get().add(part);
		if(!notAddedBefore) return false;

		part.setCompositePart(this);
		if(rootPart.get() == null) rootPart.put(part);

		return true;
	}

	public AtomicPart getRootPart() {
		return rootPart.get();
	}

	public void setRootPart(AtomicPart part) {
		rootPart.put(part);
	}

	public Document getDocumentation() {
		return documentation.get();
	}

	public LargeSet<AtomicPart> getParts() {
		return parts.get();
	}

	public void removeAssembly(BaseAssembly assembly) {
		usedIn.get().remove(assembly);
	}

	public ImmutableCollection<BaseAssembly> getUsedIn() {
		return new ImmutableCollectionImpl<BaseAssembly>(usedIn.get());
	}

	public void clearPointers() {
		documentation.put(null);
		parts.put(null);
		usedIn.put(null);
		rootPart.put(null);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CompositePart)) return false;
		return super.equals(obj);
	}

	@Override
	public Object clone() {
		throw new Error(this.getClass().getCanonicalName() + ".clone() not implemented");
	}
}
