public class ReadOnlyTransactionTask extends TransactionTask {
    
    int numOps;

    ReadOnlyTransactionTask(int txNumber, Main main, boolean tryReadOnly, int numOps) {
	super(txNumber, main, tryReadOnly);
	this.numOps = numOps;
    }

    @Override
    protected void doTx() {
	for (int i = 0; i < numOps; i++) {
	    readRandom();
	}
    }

    @Override
    protected void updateFinalStats(FinalStats stats, int restarts) {
	stats.nTxRO++;
    }
}

