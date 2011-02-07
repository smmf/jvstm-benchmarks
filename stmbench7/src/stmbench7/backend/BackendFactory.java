package stmbench7.backend;

import stmbench7.annotations.Immutable;
import stmbench7.core.RuntimeError;

/**
 * Creates the structures of the benchmark backend: indexes,
 * id pools, and large sets.
 */
@Immutable
public abstract class BackendFactory {

	public static BackendFactory instance = null;
	
	public static void setInstance(BackendFactory newInstance) {
		if(instance != null) 
			throw new RuntimeError("BackendFactory already instantiated");
		instance = newInstance;
	}
	
	public abstract <E> LargeSet<E> createLargeSet();
	public abstract <K extends IndexKey, V> Index<K,V> createIndex();
	public abstract IdPool createIdPool(int maxNumberOfIds);
}
