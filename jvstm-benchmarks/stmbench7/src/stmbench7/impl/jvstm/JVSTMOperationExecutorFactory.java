package stmbench7.impl.jvstm;

import stmbench7.OperationExecutor;
import stmbench7.OperationExecutorFactory;
import stmbench7.annotations.Immutable;
import stmbench7.core.Operation;

@Immutable
public class JVSTMOperationExecutorFactory extends OperationExecutorFactory {
	
	public OperationExecutor createOperationExecutor(Operation op) {
		return new JVSTMOperationExecutor(op);
	}

}
