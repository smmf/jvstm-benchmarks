package stmbench7.impl.jvstm;

import java.util.concurrent.Callable;

import pt.ist.esw.advice.Advice;

import jvstm.Transaction;
import stmbench7.BenchThread;
import stmbench7.Parameters;
import stmbench7.ThreadRandom;

public class JVSTMAdviceReadWrite implements Advice {

	public static JVSTMAdviceReadWrite CONTEXT = new JVSTMAdviceReadWrite();

	private JVSTMAdviceReadWrite() { }

	@Override
	public <Integer> Integer perform(Callable<Integer> method) throws Exception {
		boolean conflictNoted = false;
		Transaction tx = null;

		while (true) {
			boolean eagerAbort = false; // default only changes when CommitException occurs eagerly

			try{
				tx = Transaction.begin(false);
				JVSTMStats.noteTransaction(false, BenchThread.ID.get());
				try {
					ThreadRandom.saveState();
					return method.call();
				} catch (jvstm.CommitException ce) {
					eagerAbort = true;
					throw ce;
				} finally {
					if (!eagerAbort) { // commit unless a CommitException occurred during performOperation()
						if (Parameters.sequentialReplayEnabled) {
							JVSTMOperationExecutor.wasWriteTransaction.set(tx.isWriteTransaction());
						}
						Transaction.commit();
						if (Parameters.sequentialReplayEnabled) {
							JVSTMOperationExecutor.lastLocalOperationTimestamp.set(JVSTMOperationExecutor.lastLocalOperationTimestamp.get() + 1);
							JVSTMOperationExecutor.lastOperationTimestamp.set(tx.getNumber());
						}
					}
				}
			} catch (jvstm.CommitException ce) {
				jvstm.Transaction.abort();
				ThreadRandom.restoreState();

				if (!conflictNoted) {
					JVSTMStats.noteConflict(BenchThread.ID.get());
					conflictNoted = true;
				}

				JVSTMStats.noteRestart(BenchThread.ID.get());
			}
		}
	}
}
