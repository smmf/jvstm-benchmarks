public class WriteOnlyTransactionTask extends TransactionTask {
    
    int numOps;

    WriteOnlyTransactionTask(int txNumber, Main main, boolean tryReadOnly, int numOps) {
	super(txNumber, main, tryReadOnly);
	this.numOps = numOps;
    }

    @Override
    protected void doTx() {
	for (int i = 0; i < numOps; i++) {
	    writeRandom();
	}
    }

    @Override
    protected void updateFinalStats(FinalStats stats, int restarts) {
	stats.nTxWO++;
	stats.woRestarts += restarts;
    }
}

