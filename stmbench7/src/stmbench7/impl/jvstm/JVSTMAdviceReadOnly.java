package stmbench7.impl.jvstm;

import java.util.concurrent.Callable;

import pt.ist.esw.advice.Advice;
import jvstm.Transaction;
import stmbench7.BenchThread;
import stmbench7.Parameters;

public class JVSTMAdviceReadOnly implements Advice {

	public static JVSTMAdviceReadOnly CONTEXT = new JVSTMAdviceReadOnly();

	private JVSTMAdviceReadOnly() { }

	@Override
	public <Integer> Integer perform(Callable<Integer> method) throws Exception {
		Transaction tx = Transaction.begin(true);
		JVSTMStats.noteTransaction(true, BenchThread.ID.get());
		try {
			return method.call();
		} finally {
			if (Parameters.sequentialReplayEnabled) {
				JVSTMOperationExecutor.wasWriteTransaction.set(false);
				JVSTMOperationExecutor.lastLocalOperationTimestamp.set(JVSTMOperationExecutor.lastLocalOperationTimestamp.get() + 1);
				JVSTMOperationExecutor.lastOperationTimestamp.set(tx.getNumber());
			}
			Transaction.commit();
		}
	}
}
