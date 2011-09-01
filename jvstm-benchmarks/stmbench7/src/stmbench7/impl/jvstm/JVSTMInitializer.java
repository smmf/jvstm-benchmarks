package stmbench7.impl.jvstm;

import stmbench7.OperationExecutorFactory;
import stmbench7.SynchMethodInitializer;
import stmbench7.ThreadFactory;
import stmbench7.backend.BackendFactory;
import stmbench7.core.DesignObjFactory;
import stmbench7.impl.DefaultThreadFactory;
import stmbench7.impl.jvstm.backend.BackendFactoryImpl;
import stmbench7.impl.jvstm.core.DesignObjFactoryImpl;

public class JVSTMInitializer implements SynchMethodInitializer{

	public DesignObjFactory createDesignObjFactory(){
		return new DesignObjFactoryImpl();
	}

	public BackendFactory createBackendFactory(){
		return new BackendFactoryImpl();
	}

	public OperationExecutorFactory createOperationExecutorFactory(){
		return new JVSTMOperationExecutorFactory();
	}

	@Override
	public ThreadFactory createThreadFactory() {
		return new DefaultThreadFactory();
	}
}
