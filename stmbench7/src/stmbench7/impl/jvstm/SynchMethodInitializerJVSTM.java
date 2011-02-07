package stmbench7.impl.jvstm;

import stmbench7.OperationExecutorFactory;
import stmbench7.SynchMethodInitializer;
import stmbench7.backend.BackendFactory;
import stmbench7.core.DesignObjFactory;
import stmbench7.impl.jvstm.backend.BackendFactoryImpl;
import stmbench7.impl.jvstm.core.DesignObjFactoryImpl;

public class SynchMethodInitializerJVSTM implements SynchMethodInitializer{
	
	public BackendFactory createBackendFactory(){
		return new BackendFactoryImpl();
	}
	
	public DesignObjFactory createDesignObjFactory(){
		return new DesignObjFactoryImpl();
	}
	
	public OperationExecutorFactory createOperationExecutorFactory(){
		return new OperationExecutorFactoryImpl();
	}
}
