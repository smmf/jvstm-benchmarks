package stmbench7;

import java.util.Random;

import stmbench7.annotations.Immutable;
import stmbench7.core.RuntimeError;

/**
 * This is a central repository for thread-local random
 * number generators. No other class should create an instance
 * of class Random, but should use the methods in ThreadRandom
 * instead. This way we can centrally control the (un)determinism
 * of the benchmark and the implementation of a random number
 * generator used.
 */
@Immutable
public class ThreadRandom {

	private static enum Phase {
		INIT,
		CONCURRENT,
		SEQUENTIAL_REPLAY
	}
	
	private static Phase phase = Phase.INIT;
	private static Random initRandom = new Random(3);
	private static short currentVirtualThreadNumber;
	private static Random[] virtualRandom;
	
	protected ThreadRandom() { }
	
	protected static ThreadLocal<Random> random = 
		new ThreadLocal<Random>() {
			@Override
			protected Random initialValue() {
				return new Random(3);
			}
	};
	
	public static int nextInt() {
		return getCurrentRandom().nextInt();
	}
	
	public static int nextInt(int n) {
		return getCurrentRandom().nextInt(n);
	}
	
	public static double nextDouble() {
		return getCurrentRandom().nextDouble();
	}

	public static void reset() {
		if(phase != Phase.INIT) 
			throw new RuntimeError("Cannot reset ThreadRandom after the initialization phase");
		initRandom = new Random(3);
	}
	
	public static void startConcurrentPhase() {
		phase = Phase.CONCURRENT;
	}
	
	public static void startSequentialReplayPhase() {
		phase = Phase.SEQUENTIAL_REPLAY;
		virtualRandom = new Random[Parameters.numThreads];
		for(int n = 0; n < Parameters.numThreads; n++)
			virtualRandom[n] = new Random(3);
	}

	public static void setVirtualThreadNumber(short threadNum) {
		currentVirtualThreadNumber = threadNum;
	}
	
	private static Random getCurrentRandom() {
		switch(phase) {
		case INIT: return initRandom;
		case CONCURRENT: return random.get();
		case SEQUENTIAL_REPLAY: return virtualRandom[currentVirtualThreadNumber];
		default: return null;
		}
	}
}
