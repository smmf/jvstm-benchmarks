package stmbench7;

import stmbench7.annotations.Immutable;
import stmbench7.core.RuntimeError;

/**
 * This is a central repository for thread-local random number generators. No
 * other class should create an instance of class Random, but should use the
 * methods in ThreadRandom instead. This way we can centrally control the
 * (un)determinism of the benchmark and the implementation of a random number
 * generator used.
 */
@Immutable
public class ThreadRandom {

	private static enum Phase {
		INIT, CONCURRENT, SEQUENTIAL_REPLAY
	}

	private static Phase phase = Phase.INIT;
	private static int INITIAL_SEED = 3;
	private static RandomState initRandom = new RandomState(INITIAL_SEED);
	private static short currentVirtualThreadNumber;
	private static RandomState[] virtualRandom;

	private ThreadRandom() {
	}

	private static final ThreadLocal<RandomState> random = new ThreadLocal<RandomState>() {
		@Override
		protected RandomState initialValue() {
			return new RandomState(INITIAL_SEED);
		}
	};

	public static int nextInt(int n) {
		int k = getCurrentRandom().nextInt(n);
		// if(phase != Phase.INIT)
		// System.out.println("int " + n + " = " + k + " " +
		// getCurrentRandom().currentState);
		return k;
	}

	public static double nextDouble() {
		double k = getCurrentRandom().nextDouble();
		// if(phase != Phase.INIT)
		// System.out.println("double = " + k + " " +
		// getCurrentRandom().currentState);
		return k;
	}

	public static void reset() {
		if (phase != Phase.INIT)
			throw new RuntimeError(
					"Cannot reset ThreadRandom after the initialization phase");
		initRandom = new RandomState(INITIAL_SEED);
	}

	public static void startConcurrentPhase() {
		phase = Phase.CONCURRENT;
	}

	public static void startSequentialReplayPhase() {
		phase = Phase.SEQUENTIAL_REPLAY;
		virtualRandom = new RandomState[Parameters.numThreads];
		for (int n = 0; n < Parameters.numThreads; n++)
			virtualRandom[n] = new RandomState(INITIAL_SEED);
	}

	public static void setVirtualThreadNumber(short threadNum) {
		currentVirtualThreadNumber = threadNum;
	}

	public static void saveState() {
		getCurrentRandom().saveState();
	}

	public static void restoreState() {
		getCurrentRandom().restoreState();
	}

	private static RandomState getCurrentRandom() {
		/*
		 * switch(phase) { case INIT: return initRandom; case
		 * CONCURRENT: return random.get(); case SEQUENTIAL_REPLAY:
		 * return virtualRandom[currentVirtualThreadNumber]; default:
		 * return null; }
		 */
		switch (((Enum) phase).ordinal()) {
			case 0:
				return initRandom;
			case 1:
				return random.get();
			case 2:
				return virtualRandom[currentVirtualThreadNumber];
			default:
				return null;
		}
	}
}
