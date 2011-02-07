package stmbench7;

import stmbench7.annotations.Immutable;
import stmbench7.backend.BackendFactory;
import stmbench7.backend.Index;
import stmbench7.backend.LargeSet;
import stmbench7.core.AssemblyBuilder;
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.CompositePartBuilder;
import stmbench7.core.Document;
import stmbench7.core.IntIndexKey;
import stmbench7.core.Module;
import stmbench7.core.ModuleBuilder;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;
import stmbench7.core.StringIndexKey;
import stmbench7.operations.SetupDataStructure;

/**
 * Sets up the benchmark structures according to given parameters,
 * including indexes.
 */
@Immutable
public class Setup {
    
    protected Module module;

    protected Index<IntIndexKey,AtomicPart> atomicPartIdIndex;
    protected Index<IntIndexKey,LargeSet<AtomicPart>> atomicPartBuildDateIndex;
    protected Index<StringIndexKey,Document> documentTitleIndex;
    protected Index<IntIndexKey,CompositePart> compositePartIdIndex;
    protected Index<IntIndexKey,BaseAssembly> baseAssemblyIdIndex;
    protected Index<IntIndexKey,ComplexAssembly> complexAssemblyIdIndex;

    protected CompositePartBuilder compositePartBuilder;
    protected ModuleBuilder moduleBuilder;
    
    public Setup() {
    	BackendFactory backendFactory = BackendFactory.instance;
    	
    	atomicPartIdIndex = backendFactory.<IntIndexKey,AtomicPart>createIndex();
    	atomicPartBuildDateIndex = backendFactory.<IntIndexKey,LargeSet<AtomicPart>>createIndex();
    	documentTitleIndex = backendFactory.<StringIndexKey,Document>createIndex();
    	compositePartIdIndex = backendFactory.<IntIndexKey,CompositePart>createIndex();
    	baseAssemblyIdIndex = backendFactory.<IntIndexKey,BaseAssembly>createIndex();
    	complexAssemblyIdIndex = backendFactory.<IntIndexKey,ComplexAssembly>createIndex();    	

    	compositePartBuilder = new CompositePartBuilder(compositePartIdIndex,
    			documentTitleIndex,	atomicPartIdIndex, atomicPartBuildDateIndex);
    	moduleBuilder = new ModuleBuilder(baseAssemblyIdIndex, complexAssemblyIdIndex);
    	
    	SetupDataStructure setupOperation = new SetupDataStructure(this);
    	OperationExecutor operationExecutor =
    		OperationExecutorFactory.instance.createOperationExecutor(setupOperation);
    	try {
    		operationExecutor.execute();
    	}
    	catch(OperationFailedException e) {
    		throw new RuntimeError("Unexpected failure of the setup operation");
    	}
    	module = setupOperation.getModule();
    }
    
	public Index<IntIndexKey, LargeSet<AtomicPart>> getAtomicPartBuildDateIndex() {
		return atomicPartBuildDateIndex;
	}

	public Index<IntIndexKey, AtomicPart> getAtomicPartIdIndex() {
		return atomicPartIdIndex;
	}

	public Index<IntIndexKey, BaseAssembly> getBaseAssemblyIdIndex() {
		return baseAssemblyIdIndex;
	}

	public Index<IntIndexKey, ComplexAssembly> getComplexAssemblyIdIndex() {
		return complexAssemblyIdIndex;
	}

	public Index<IntIndexKey, CompositePart> getCompositePartIdIndex() {
		return compositePartIdIndex;
	}

	public Index<StringIndexKey, Document> getDocumentTitleIndex() {
		return documentTitleIndex;
	}

	public Module getModule() {
		return module;
	}
	
	public CompositePartBuilder getCompositePartBuilder() {
		return compositePartBuilder;
	}
	
	public ModuleBuilder getModuleBuilder() {
		return moduleBuilder;
	}
	
	public AssemblyBuilder getAssemblyBuilder() {
		return moduleBuilder.getAssemblyBuilder();
	}
}
