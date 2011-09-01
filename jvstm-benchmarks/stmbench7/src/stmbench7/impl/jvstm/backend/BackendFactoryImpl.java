package stmbench7.impl.jvstm.backend;

import stmbench7.backend.BackendFactory;
import stmbench7.backend.IdPool;
import stmbench7.backend.Index;
import stmbench7.backend.LargeSet;

/**
 * Implements methods that create objects implementing
 * interfaces defined in stmbench7.backend: VIndex, IdPoolImpl,
 * and LargeSetImpl.
 */
public class BackendFactoryImpl extends BackendFactory {

	@Override
	public <E extends Comparable<E>> LargeSet<E> createLargeSet() {
		return new LargeSetImpl<E>();
	}

	@Override
	public  <K extends Comparable<K>, V> Index<K,V> createIndex() {
		return new VIndex<K,V>();
	}

	@Override
	public IdPool createIdPool(int maxNumberOfIds) {
		return new IdPoolImpl(maxNumberOfIds);
	}
}
