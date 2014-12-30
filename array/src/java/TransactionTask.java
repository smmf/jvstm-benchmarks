
import java.util.concurrent.Callable;
import java.util.Random;

import jvstm.CommitException;
import jvstm.Transaction;
import jvstm.VBox;
import jvstm.WriteOnReadException;

public abstract class TransactionTask implements Callable<Void>, Runnable {
    int txNumber; // used to update the statistics array
    Main main;
    boolean tryReadOnly;
    Random rand;
    TxStatistics stats;

    // this tries to avoid any bytecode optimization that could remove code in the doTx method
    int result;

    TransactionTask(int txNumber, Main main, boolean tryReadOnly) {
	this.txNumber = txNumber;
	this.main = main;
	this.tryReadOnly = tryReadOnly;
	this.rand = new Random();
	this.stats = new TxStatistics(this);
    }

    public void run() {
	call();
    }

    public Void call() {
	try {
	    while (true) {
		Transaction.begin(this.tryReadOnly);
		boolean txFinished = false;
		try {
		    doTx();
		    Transaction.commit();
		    txFinished = true;
		    main.stats[txNumber] = stats;
//		    jvstm.CommitStats.dumpToResults();
		    return null;
		} catch (CommitException ce) {
		    stats.restarts++;
		    Transaction.abort();
		    txFinished = true;
		} catch (WriteOnReadException wore) {
		    stats.restarts++;
		    Transaction.abort();
		    txFinished = true;
		    this.tryReadOnly = false;
		} finally {
		    if (! txFinished) {
			Transaction.abort();
		    }
		}
	    }
	} catch (Throwable t) {
	    t.printStackTrace();
	    System.exit(1);
	}
	return null;
    }

    protected abstract void doTx();

    protected abstract void updateFinalStats(FinalStats stats, int restarts);

    protected void writeRandom() {
	int pos = main.randomPositionInArray(this.rand);
	VBox<Integer> box = main.array[pos];
	box.put(pos);
    }

    protected void readRandom() {
	VBox<Integer> box = main.array[main.randomPositionInArray(this.rand)];
	result = box.get();
    }

}

