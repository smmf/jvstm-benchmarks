package stmbench7.impl.jvstm;

import stmbench7.Parameters;

public class JVSTMStats {
	private static int[] rwTransactions 	= new int[Parameters.numThreads];
	private static int[] roTransactions 	= new int[Parameters.numThreads];
	private static int[] conflicts 	= new int[Parameters.numThreads];
	private static int[] restarts 	= new int[Parameters.numThreads];

	public static void noteTransaction(boolean readOnly, Short id) {
		if (readOnly) noteReadOnlyTransaction(id);
		else noteReadWriteTransaction(id);
	}

	private static void noteReadWriteTransaction(int id) {
		rwTransactions[id] = rwTransactions[id] + 1;
	}

	private static void noteReadOnlyTransaction(int id) {
		roTransactions[id] = roTransactions[id] + 1;
	}

	public static void noteConflict(int id) {
		conflicts[id] = conflicts[id] + 1;
	}

	public static void noteRestart(int id) {
		restarts[id] = restarts[id] + 1;
	}

	private static int getRwTransactions() {
		int res = 0;

		for (int i = 0; i < rwTransactions.length; ++i) {
			res += rwTransactions[i];
		}

		return res;
	}

	private static int getRoTransactions() {
		int res = 0;

		for (int i = 0; i < roTransactions.length; ++i) {
			res += roTransactions[i];
		}

		return res;
	}

	private static int getConflicts() {
		int res = 0;

		for (int i = 0; i < conflicts.length; ++i) {
			res += conflicts[i];
		}

		return res;
	}

	private static int getRestarts() {
		int res = 0;

		for (int i = 0; i < restarts.length; ++i) {
			res += restarts[i];
		}

		return res;
	}

	public static void printStats() {
		System.out.printf("RW = %d, RO = %d, Conflicts = %d (%f%%), Restarts = %d (%f%%)\n",
				getRwTransactions(),
				getRoTransactions(),
				getConflicts(),
				((getConflicts() * 100.0) / getRwTransactions()),
				getRestarts(),
				((getRestarts() * 100.0) / getRwTransactions()));
	}
}
