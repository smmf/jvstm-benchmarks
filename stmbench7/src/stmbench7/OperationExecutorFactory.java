package stmbench7;

import stmbench7.annotations.NonAtomic;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

/**
 * Creates an OperationExecutor object, which is used
 * to execute the benchmark operations. For the default
 * implementation, see stmbench7.impl.DefaultOperationExecutorFactory.
 */
@NonAtomic
public abstract class OperationExecutorFactory {

	public static OperationExecutorFactory instance = null;
	
	public static void setInstance(OperationExecutorFactory newInstance) {
		if(instance != null)
			throw new RuntimeError("OperationExecutorFactory already instantiated");
		instance = newInstance;
	}
	
	public abstract OperationExecutor createOperationExecutor(Operation op);
	
	public static int executeSequentialOperation(Operation op) {
		OperationExecutor operationExecutor = 
			instance.createOperationExecutor(op);
		try {
			return operationExecutor.execute();
		}
		catch(OperationFailedException e) {
			throw new RuntimeError("Unexpected failure of a sequential operation " + op);
		}
	}
}
