package stmbench7.impl.jvstm.backend;

import jvstm.util.VQueue;
import stmbench7.annotations.Update;
import stmbench7.backend.IdPool;
import stmbench7.core.OperationFailedException;


public class IdPoolImpl implements IdPool {

	protected final VQueue<Integer> idPool;

	public IdPoolImpl(int maxNumberOfIds) {
		idPool = new VQueue<Integer>();
		for(int id = 1; id <= maxNumberOfIds; id++) {
			idPool.offer(id);
		}
	}

	@Update
	public int getId() throws OperationFailedException {
		Integer id = idPool.poll();
		if(id == null) throw new OperationFailedException();

		return id;
	}

	@Update
	public void putUnusedId(int id) {
		idPool.offer(id);
	}

	public String toString() {
		String txt = "IdPool:";
		for(int id : idPool) txt += " " + id;
		return txt;
	}

	@Override
	public Object clone() {
		throw new Error(this.getClass().getCanonicalName() + ".clone() not implemented");
	}
}
