package stmbench7.impl.jvstm;

import jvstm.Transaction;
import stmbench7.BenchThread;
import stmbench7.OperationExecutor;
import stmbench7.Parameters;
import stmbench7.ThreadRandom;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class JVSTMOperationExecutor implements OperationExecutor {

	private static final ThreadLocal<Integer> lastLocalOperationTimestamp = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() { return 0; }
	};

	private final Operation op;

	private boolean readOnly;
	private boolean idNull = false;
	private int lastOperationTimestamp = 0;
	private boolean wasReadOnly;

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
		boolean conflictNoted = false;
		Transaction tx = null;

		while (true) {
			JVSTMStats.noteTransaction(readOnly, BenchThread.ID.get());

			try{
                                boolean opSuccess = true; // default only changes when CommitException occurs eagerly
				Transaction.begin(readOnly);
				tx = Transaction.current();
				try {
					ThreadRandom.saveState();
					int result = op.performOperation();
                                        return result;
                                } catch (jvstm.CommitException ce) {
                                    opSuccess = false;
                                    throw ce;
				} finally {
                                    wasReadOnly = !tx.isWriteTransaction();
                                    if (opSuccess) { // commit unless a CommitException occurred during performOperation()
                                        Transaction.commit();
					if (Parameters.sequentialReplayEnabled) {
						lastLocalOperationTimestamp.set(lastLocalOperationTimestamp.get() + 1);
						lastOperationTimestamp = tx.getNumber();
					}
                                    }
				}
			}  catch (jvstm.CommitException ce) {
				ThreadRandom.restoreState();
				if (readOnly) throw new Error("Read-Only Transactions should never fail!");

				if (!conflictNoted) {
					JVSTMStats.noteConflict(BenchThread.ID.get());
					conflictNoted = true;
				}
				jvstm.Transaction.abort();
				JVSTMStats.noteRestart(BenchThread.ID.get());
			}
		}
	}

	public int getLastOperationTimestamp() {
		return lastOperationTimestamp;
	}

	public int getLastLocalOperationTimestamp() {
		return lastLocalOperationTimestamp.get();
	}

	@Override
	public boolean isOperationReadOnly() {
		return wasReadOnly;
	}

}
