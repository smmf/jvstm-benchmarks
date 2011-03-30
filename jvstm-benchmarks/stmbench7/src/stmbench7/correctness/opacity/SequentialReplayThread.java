package stmbench7.correctness.opacity;

import java.util.ArrayList;

import stmbench7.BenchThread;
import stmbench7.OperationId;
import stmbench7.Setup;
import stmbench7.ThreadRandom;
import stmbench7.annotations.NonAtomic;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

/**
 * Replays sequentially a concurrent execution. Used to check
 * whether a given concurrent execution ensures opacity, i.e., 
 * whether the synchronization method used in the execution
 * was correctly synchonizing threads during this execution.
 */
@NonAtomic
public class SequentialReplayThread extends BenchThread {

	public SequentialReplayThread(Setup setup, double[] operationCDF,
			ArrayList<ReplayLogEntry> replayLog) {
		super(setup, operationCDF);
		this.replayLog = replayLog;
		ThreadRandom.startSequentialReplayPhase();
	}
	
	public void run() {
		int opNum = 1, numOfOps = replayLog.size();
		for(ReplayLogEntry entry : replayLog) {
			System.err.print("Operation " + (opNum++) + " out of " + numOfOps + "\r");
			short threadNum = entry.threadNum;
			ThreadRandom.setVirtualThreadNumber(threadNum);
			
			int operationNumber = getNextOperationNumber();
			int result = 0;
			boolean failed = false;
			
			try {
				result = operations[operationNumber].execute();
			}
			catch(OperationFailedException e) {
				failed = true;
			}
			
			if(result != entry.result || failed != entry.failed) {
				String opName = OperationId.values()[operationNumber].toString();
				throw new RuntimeError("Different operation result in the sequential execution (" +
						"operation " + opName + "): " +
						"Sequential: result = " + result + ", failed = " + failed + ". " + 
						"Concurrent: result = " + entry.result + ", failed = " + entry.failed + ".");
			}
		}
		System.err.println();
	}
}
