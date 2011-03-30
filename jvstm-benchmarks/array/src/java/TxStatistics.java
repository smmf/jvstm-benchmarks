public class TxStatistics {
    TransactionTask tx;
    int restarts = 0;

    TxStatistics(TransactionTask tx) {
	this.tx = tx;
    }

    void updateFinalStats(FinalStats stats) {
	tx.updateFinalStats(stats, restarts);
    }

}