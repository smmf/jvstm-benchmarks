package stmbench7;

import stmbench7.annotations.NonAtomic;

@NonAtomic
public class BenchThreadOps extends BenchThread {

	public final int _numOps;
	int opsExecuted = 0;

	public BenchThreadOps(Setup setup, double[] operationCDF, short myThreadNum) {
		super(setup, operationCDF, myThreadNum);
		_numOps = Parameters.ExecutionType.OPS_PER_TX.getLimit();
	}

	protected BenchThreadOps(Setup setup, double[] operationCDF) {
		super(setup, operationCDF);
		_numOps = Parameters.ExecutionType.OPS_PER_TX.getLimit();
	}

	@Override
	protected boolean shouldContinue(int operationNumber) {
		return opsExecuted++ < _numOps;
	}
}
