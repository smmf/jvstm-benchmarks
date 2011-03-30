package stmbench7.impl;

import stmbench7.OperationExecutor;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;

/**
 * Default implementation of an OperationExecutor.
 * Does not provide any thread-safety.
 */
public class DefaultOperationExecutor implements OperationExecutor {

	private final Operation op;
	private int lastTimestamp = 0;
	
	public DefaultOperationExecutor(Operation op) {
		this.op = op;
	}
	
	public int execute() throws OperationFailedException {
		return op.performOperation();
	}

	public int getLastOperationTimestamp() {
		return lastTimestamp++;
	}

	public Operation getOp() {
		return op;
	}	
}
