package stmbench7.impl.jvstm;

public class JVSTMStats {
	private static int rwTransactions = 0;
	private static int roTransactions = 0;
	private static int conflicts = 0;
	private static int restarts = 0;
	private static int aborts = 0;

	public synchronized static void noteReadWriteTransaction() {
		++rwTransactions;
	}

	public synchronized static void noteReadOnlyTransaction() {
		++roTransactions;
	}

	public synchronized static void noteConflict() {
		++conflicts;
	}

	public synchronized static void noteRestart() {
		++restarts;
	}

	public synchronized static void noteAbort() {
		++aborts;
	}

	public static void printStats() {
		System.out.printf("RW = %d, RO = %d, Conflicts = %d (%f%%), Restarts = %d (%f%%), Aborts = %d\n", 
				rwTransactions, 
				roTransactions, 
				conflicts, 
				((conflicts * 100.0) / rwTransactions),
				restarts,
				((restarts * 100.0) / rwTransactions),
				aborts);
	}
}
