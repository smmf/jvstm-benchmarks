public class ReadWriteTransactionTask extends TransactionTask {
    
    int numReads, numWrites, totalOps;
    int reads, writes;

    ReadWriteTransactionTask(int txNumber, Main main, boolean tryReadOnly, int numReads, int numWrites) {
	super(txNumber, main, tryReadOnly);
	this.totalOps = numReads + numWrites;
	this.numReads = numReads;
	this.numWrites = numWrites;
    }

    @Override
    protected void doTx() {
	this.reads = 0;
	this.writes = 0;

	while (reads < numReads && writes < numWrites) {
	    readOrWrite();
	}
	while (reads < numReads) {
	    readRandom();
	    reads++;
	}
	while (writes < numWrites) {
	    writeRandom();
	    writes++;
	}
    }

    private void readOrWrite() {
	int op = this.rand.nextInt(totalOps);
	if (op < numReads) {
	    readRandom();
	    reads++;
	} else {
	    writeRandom();
	    writes++;
	}
    }

    @Override
    protected void updateFinalStats(FinalStats stats, int restarts) {
	stats.nTxRW++;
	stats.rwRestarts += restarts;
    }
}

