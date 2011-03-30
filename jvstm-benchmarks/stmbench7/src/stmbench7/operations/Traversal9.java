package stmbench7.operations;


import stmbench7.OperationId;
import stmbench7.Setup;
import stmbench7.annotations.ReadOnly;
import stmbench7.annotations.Transactional;
import stmbench7.core.Manual;

/**
 * Traversal T9 / Operation OP5 (see the specification).
 * Read-only.
 */
public class Traversal9 extends Traversal8 {

    public Traversal9(Setup oo7setup) {
    	super(oo7setup);
    }

	@Override
	@Transactional @ReadOnly
	public int performOperation() {
		Manual manual = module.getManual();
		return traverse(manual);
	}
    
    @Override
    protected int traverse(Manual manual) {
    	return manual.checkFirstLastCharTheSame();
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP5;
    }
}
