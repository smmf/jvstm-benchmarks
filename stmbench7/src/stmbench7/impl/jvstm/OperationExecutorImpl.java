package stmbench7.impl.jvstm;

import java.util.concurrent.atomic.AtomicInteger;

import jvstm.Transaction;
import stmbench7.OperationExecutor;
import stmbench7.Parameters;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class OperationExecutorImpl implements OperationExecutor {

	private final Operation op;
	private boolean readOnly;
	private boolean idNull = false;
	private int lastOperationTimestamp = 0;
	private static final AtomicInteger globalCounter = new AtomicInteger();

	public OperationExecutorImpl(Operation op) {
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
		if(idNull == true)
			return op.performOperation();

		boolean conflictNoted = false;
		while (true) {
			boolean finished = false;
			try{
				if(readOnly) {
					JVSTMStats.noteReadOnlyTransaction();
					Transaction.begin(true);
				} else {
					JVSTMStats.noteReadWriteTransaction();
					//if (conflictNoted) {
					//	Transaction.beginInevitable();
					//} else {
						Transaction.begin();
					//}
				}

				int result = op.performOperation();
				Transaction.commit();
				finished = true;
				return result;

			}  catch (jvstm.CommitException ce) {
				if (readOnly) {
					throw new Error("Read-Only Transactions should never fail!");
				}
				if (!conflictNoted) {
					JVSTMStats.noteConflict();
					conflictNoted = true;
				}
				jvstm.Transaction.abort();
				finished = true;
				JVSTMStats.noteRestart();
			} finally {
				if (!finished) {
					JVSTMStats.noteAbort();
					jvstm.Transaction.abort();
				}
				// FIXME: ???
				if (Parameters.sequentialReplayEnabled)
					lastOperationTimestamp = globalCounter.getAndIncrement();
			}
		}
	}

	public int getLastOperationTimestamp() {
		return lastOperationTimestamp;
	}

	public Operation getOp() {
		// TODO Auto-generated method stub
		return op;
	}	

}
