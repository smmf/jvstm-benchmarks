package stmbench7.impl.jvstm;

import pt.ist.esw.advice.Advice;
import pt.ist.esw.advice.AdviceFactory;
import jvstm.Atomic;

public class JVSTMAdviceFactory extends AdviceFactory<Atomic> {

	private static AdviceFactory<Atomic> instance = new JVSTMAdviceFactory();

	public static AdviceFactory<Atomic> getInstance() {
		return instance;
	}

	public Advice newAdvice(Atomic atomic) {
		if (atomic.readOnly()) return JVSTMAdviceReadOnly.CONTEXT;
		return JVSTMAdviceReadWrite.CONTEXT;
	}
}
