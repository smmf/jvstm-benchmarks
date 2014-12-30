package stmbench7;

import java.util.concurrent.atomic.AtomicIntegerArray;

import stmbench7.annotations.NonAtomic;

@NonAtomic
public class BenchThreadTotalOps extends BenchThread {

	private static AtomicIntegerArray _ops;
	private static int _numOps;
	private static final int INVALID_OP_NUMBER = -1;

	public BenchThreadTotalOps(Setup setup, double[] operationCDF, short myThreadNum) {
		super(setup, operationCDF, myThreadNum);
	}

	public static void buildOps(double[] operationCDF, int numOps) {
		if (_ops == null) {
			_numOps = numOps;
			int[] ops = new int[_numOps];
			for (int i = 0; i < numOps; ++i) {
				double whichOperation = ThreadRandom.nextDouble();
				int operationNumber = 0;
				while (whichOperation >= operationCDF[operationNumber]) {
					operationNumber++;
				}

				ops[i] = operationNumber;
			}

			_ops = new AtomicIntegerArray(ops);
		}
	}

	@Override
	protected boolean shouldContinue(int operationNumber) {
		return operationNumber != INVALID_OP_NUMBER;
	}

	int _nextToExecute = 0;
	@Override
	protected int getNextOperationNumber() {
		int nextOp = INVALID_OP_NUMBER;
		while(_nextToExecute < _numOps && nextOp == INVALID_OP_NUMBER) {
			if (_ops.get(_nextToExecute) != INVALID_OP_NUMBER) {
				nextOp = _ops.getAndSet(_nextToExecute++, INVALID_OP_NUMBER);
			} else {
				++_nextToExecute;
			}
		}

		return nextOp;
	}
}
