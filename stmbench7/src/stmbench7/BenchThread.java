package stmbench7;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import stmbench7.annotations.NonAtomic;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

/**
 * A single thread of the STMBench7 benchmark. Executes operations assigned to
 * it one by one, randomly choosing the next operation and respecting the
 * expected ratios of operations' counts.
 */
@NonAtomic
public class BenchThread implements Runnable {

	public static final ThreadLocal<Short> ID = new ThreadLocal<Short>();

	protected volatile boolean stop = false;
	protected double[] operationCDF;
	protected OperationExecutor[] operations;
	protected final short myThreadNum;

	public int[] successfulOperations, failedOperations;
	public int[][] operationsTTC, operationsHighTTCLog;

	public class ReplayLogEntry implements Comparable<ReplayLogEntry> {
		public final short threadNum;
		public final int timestamp, result;
		public final boolean failed;
		public final int opNum;
		public final boolean readOnly;
		public final int localTs;

		public ReplayLogEntry(int timestamp, int result, boolean failed, int opNum, boolean readOnly, int localTs) {
			this.threadNum = myThreadNum;
			this.timestamp = timestamp;
			this.result = result;
			this.failed = failed;
			this.opNum = opNum;
			this.readOnly = readOnly;
			this.localTs = localTs;
		}

		public int compareTo(ReplayLogEntry otherEntry) {
			int globalOrder = timestamp - otherEntry.timestamp;
			if (globalOrder != 0) {
				return globalOrder;
			}

			// If we get here, both ReplayLogEntries have the
			// same timestamp, meaning that both correspond to
			// the same version of the transactional system.

			if (otherEntry == this) return 0;

			// If both entries were created by the same BenchThread,
			// we use the execution order local to the thread to order them.
			if (threadNum == otherEntry.threadNum) {
				return localTs - otherEntry.localTs;
			}

			// If both are read-only, we use the threadNum to order them.
			// This is necessary to ensure that a.compareTo(b) is consistent
			// with b.compareTo(a) .
			if (readOnly && otherEntry.readOnly) {
				return threadNum - otherEntry.threadNum;
			}

			// If we get here, one of the entries is read-only
			// and is ordered last.
			return readOnly ? 1 : -1;
		}
	}

	public ArrayList<ReplayLogEntry> replayLog;

	public BenchThread(Setup setup, double[] operationCDF, short myThreadNum) {
		this.operationCDF = operationCDF;

		int numOfOperations = OperationId.values().length;
		operationsTTC = new int[numOfOperations][Parameters.MAX_LOW_TTC + 1];
		operationsHighTTCLog = new int[numOfOperations][Parameters.HIGH_TTC_ENTRIES];
		successfulOperations = new int[numOfOperations];
		failedOperations = new int[numOfOperations];
		operations = new OperationExecutor[numOfOperations];
		this.myThreadNum = myThreadNum;

		createOperations(setup);

		if (Parameters.sequentialReplayEnabled)
			replayLog = new ArrayList<ReplayLogEntry>();
	}

	protected BenchThread(Setup setup, double[] operationCDF) {
		this.operationCDF = operationCDF;
		operations = new OperationExecutor[OperationId.values().length];
		createOperations(setup);
		myThreadNum = 0;
	}

	public void run() {
		ID.set(this.myThreadNum);
		int operationNumber;
		while (shouldContinue(operationNumber = getNextOperationNumber())) {
			OperationExecutor currentExecutor = operations[operationNumber];
			int result = 0;
			boolean failed = false;

			try {
				long startTime = System.currentTimeMillis();

				result = currentExecutor.execute();

				long endTime = System.currentTimeMillis();
				// System.out.println("success");

				successfulOperations[operationNumber]++;
				int ttc = (int) (endTime - startTime);
				if (ttc <= Parameters.MAX_LOW_TTC)
					operationsTTC[operationNumber][ttc]++;
				else {
					double logHighTtc = (Math.log(ttc) - Math
							.log(Parameters.MAX_LOW_TTC + 1))
							/ Math.log(Parameters.HIGH_TTC_LOG_BASE);
					int intLogHighTtc = Math
							.min((int) logHighTtc,
									Parameters.HIGH_TTC_ENTRIES - 1);
					operationsHighTTCLog[operationNumber][intLogHighTtc]++;
				}
			} catch (OperationFailedException e) {
				// System.out.println("failed");
				failedOperations[operationNumber]++;
				failed = true;
			}

			if (Parameters.sequentialReplayEnabled) {
				ReplayLogEntry newEntry = new ReplayLogEntry(
						currentExecutor.getLastOperationTimestamp(),
						result, failed, operationNumber,
						currentExecutor.isOperationReadOnly(),
						currentExecutor.getLastLocalOperationTimestamp());
				replayLog.add(newEntry);
				// System.out.println("ts: " +
				// newEntry.timestamp);
			}
		}
		System.err.println("Thread #" + myThreadNum + " finished.");
		// int i = 0;
		// for (ReplayLogEntry entry : replayLog)
		// System.out.println(i++ + " % " +
		// OperationId.values()[entry.opNum]
		// + " -- " + entry.timestamp);
	}

	protected boolean shouldContinue(int operationNumber) {
		return !stop;
	}

	public void stopThread() {
		stop = true;
	}

	protected void createOperations(Setup setup) {
		for (OperationId operationDescr : OperationId.values()) {
			Class<? extends Operation> operationClass = operationDescr
					.getOperationClass();
			int operationIndex = operationDescr.ordinal();

			try {
				Constructor<? extends Operation> operationConstructor = operationClass
						.getConstructor(Setup.class);
				Operation operation = operationConstructor
						.newInstance(setup);

				operations[operationIndex] = OperationExecutorFactory.instance
						.createOperationExecutor(operation);
				assert (operation.getOperationId()
						.getOperationClass()
						.equals(operationClass));
			} catch (Exception e) {
				throw new RuntimeError(
						"Error while creating operation "
								+ operationDescr,
						e);
			}
		}
	}

	protected int getNextOperationNumber() {
		double whichOperation = ThreadRandom.nextDouble();
		int operationNumber = 0;
		while (whichOperation >= operationCDF[operationNumber])
			operationNumber++;
		return operationNumber;
	}
}
