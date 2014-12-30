package stmbench7.impl.jvstm;

import jvstm.Atomic;
import stmbench7.OperationExecutor;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class JVSTMOperationExecutor implements OperationExecutor {

	public static final ThreadLocal<Integer> lastLocalOperationTimestamp = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() { return 0; }
	};

	public static final ThreadLocal<Integer> lastOperationTimestamp = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() { return 0; }
	};

	public static final ThreadLocal<Boolean> wasWriteTransaction = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() { return Boolean.FALSE; }
	};

	private final Operation op;

	private boolean idNull = false;
	private boolean readOnly;

	public JVSTMOperationExecutor(Operation op) {
		this.op = op;

		if(op.getOperationId() == null){
			idNull = true;
			return;
		}

		switch(op.getOperationId().getType()) {
			case OPERATION_RO:
			case SHORT_TRAVERSAL_RO:
			case TRAVERSAL_RO:
				readOnly = true;
				break;
			case OPERATION:
			case SHORT_TRAVERSAL:
			case TRAVERSAL:
			case STRUCTURAL_MODIFICATION:
				readOnly = false;
				break;
			default:
				throw new RuntimeError("Unexpected operation type");
		}
	}

	public int execute() throws OperationFailedException {
		if(idNull == true) return op.performOperation();
		else return txExecute();
	}

	private int txExecute() throws OperationFailedException {
		if (readOnly) return txExecuteReadOnly();
		else return txExecuteReadWrite();
	}

	@Atomic(readOnly=true, adviceFactory=JVSTMAdviceFactory.class)
	private int txExecuteReadOnly() throws OperationFailedException {
		return op.performOperation();
	}

	@Atomic(readOnly=false, adviceFactory=JVSTMAdviceFactory.class)
	private int txExecuteReadWrite() throws OperationFailedException {
		return op.performOperation();
	}

	public int getLastOperationTimestamp() {
		return lastOperationTimestamp.get();
	}

	public int getLastLocalOperationTimestamp() {
		return lastLocalOperationTimestamp.get();
	}

	@Override
	public boolean isOperationReadOnly() {
		return !wasWriteTransaction.get();
	}

}