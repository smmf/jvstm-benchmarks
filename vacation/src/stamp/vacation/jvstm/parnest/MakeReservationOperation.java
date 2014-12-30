package stamp.vacation.jvstm.parnest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jvstm.CommitException;
import jvstm.EarlyAbortException;
import jvstm.ParallelTask;
import jvstm.Transaction;

public class MakeReservationOperation extends Operation {

    final private Manager manager;
    final private int[] types;
    final private int[] ids;
    final private int[] maxPrices;
    final private int[] maxIds;
    final private int customerId;
    final private int numQuery;

    public MakeReservationOperation(Manager manager, Random random, int numQueryPerTx, int queryRange) {
	this.manager = manager;
	this.types = new int[numQueryPerTx];
	this.ids = new int[numQueryPerTx];

	this.maxPrices = new int[Definitions.NUM_RESERVATION_TYPE];
	this.maxIds = new int[Definitions.NUM_RESERVATION_TYPE];
	this.maxPrices[0] = -1;
	this.maxPrices[1] = -1;
	this.maxPrices[2] = -1;
	this.maxIds[0] = -1;
	this.maxIds[1] = -1;
	this.maxIds[2] = -1;
	int n;
	this.numQuery = numQueryPerTx;
	this.customerId = random.posrandom_generate() % queryRange + 1;
	
	int[] baseIds = new int[20];
	for (int i = 0; i < 20; i++) {
	    baseIds[i] = (random.random_generate() % queryRange) + 1;
	}
	
	for (n = 0; n < numQuery; n++) {
	    types[n] = random.random_generate() % Definitions.NUM_RESERVATION_TYPE;
	    ids[n] = baseIds[n % 20];
	}
    }

    @Override
    public void doOperation() {
	while (true) {
	    Transaction tx = Transaction.begin();
	    try {
		if (Operation.nestedParallelismOn) {
		    makeReservation();
		} else {
		    makeReservationNotNested();
		}
		tx.commit();
		tx = null;
		return;
	    } catch (EarlyAbortException ce) {
		tx.abort();
		tx = null;
	    } catch (CommitException ce) {
		tx.abort();
		tx = null;
	    } finally {
		if (tx != null) {
		    tx.abort();
		}
	    }
	}
    }

    private class NestedWorker extends ParallelTask<Boolean> {

	private final int min;
	private final int max;

	public NestedWorker(int min, int max) {
	    this.min = min;
	    this.max = max;
	}

	@Override
	public Boolean execute() throws Throwable {
	    boolean isFound = false;
	    int n;
	    for (n = min; n < max; n++) {
		int t = types[n];
		int id = ids[n];
		int price = -1;
		if (t == Definitions.RESERVATION_CAR) {
		    if (manager.manager_queryCar(id) >= 0) {
			price = manager.manager_queryCarPrice(id);
		    }
		} else if (t == Definitions.RESERVATION_FLIGHT) {
		    if (manager.manager_queryFlight(id) >= 0) {
			price = manager.manager_queryFlightPrice(id);
		    }
		} else if (t == Definitions.RESERVATION_ROOM) {
		    if (manager.manager_queryRoom(id) >= 0) {
			price = manager.manager_queryRoomPrice(id);
		    }
		} else {
		    assert (false);
		}
		if (price > maxPrices[t]) {
		    maxPrices[t] = price;
		    maxIds[t] = id;
		    isFound = true;
		}
	    }
	    return isFound;
	}
	
	@Override
	protected boolean isReadOnly() {
	    return true;
	}

    }

    private void makeReservationNotNested() {
	boolean isFound = false;
	int n;
	for (n = 0; n < numQuery; n++) {
	    int t = types[n];
	    int id = ids[n];
	    int price = -1;
	    if (t == Definitions.RESERVATION_CAR) {
		if (manager.manager_queryCar(id) >= 0) {
		    price = manager.manager_queryCarPrice(id);
		}
	    } else if (t == Definitions.RESERVATION_FLIGHT) {
		if (manager.manager_queryFlight(id) >= 0) {
		    price = manager.manager_queryFlightPrice(id);
		}
	    } else if (t == Definitions.RESERVATION_ROOM) {
		if (manager.manager_queryRoom(id) >= 0) {
		    price = manager.manager_queryRoomPrice(id);
		}
	    } else {
		assert (false);
	    }
	    if (price > maxPrices[t]) {
		maxPrices[t] = price;
		maxIds[t] = id;
		isFound = true;
	    }
	}

	if (isFound) {
	    manager.manager_addCustomer(customerId);
	}
	if (maxIds[Definitions.RESERVATION_CAR] > 0) {
	    manager.manager_reserveCar(customerId, maxIds[Definitions.RESERVATION_CAR]);
	}
	if (maxIds[Definitions.RESERVATION_FLIGHT] > 0) {
	    manager.manager_reserveFlight(customerId, maxIds[Definitions.RESERVATION_FLIGHT]);
	}
	if (maxIds[Definitions.RESERVATION_ROOM] > 0) {
	    manager.manager_reserveRoom(customerId, maxIds[Definitions.RESERVATION_ROOM]);
	}
    }

    private void makeReservation() {
	boolean isFound = false;
	int n;
	int queriesPerTx = numQuery / Operation.numberAvailableThreads;
	List<NestedWorker> workers = new ArrayList<NestedWorker>();
	for (n = 0; n < numQuery; n += queriesPerTx) {
	    workers.add(new NestedWorker(n, n + queriesPerTx));
	} /* for n */

	for (Boolean res : Transaction.current().manageNestedParallelTxs(workers, Vacation.threadPool)) {
	    if (res) {
		isFound = true;
		break;
	    }
	}
	
	if (isFound) {
	    manager.manager_addCustomer(customerId);
	}
	if (maxIds[Definitions.RESERVATION_CAR] > 0) {
	    manager.manager_reserveCar(customerId, maxIds[Definitions.RESERVATION_CAR]);
	}
	if (maxIds[Definitions.RESERVATION_FLIGHT] > 0) {
	    manager.manager_reserveFlight(customerId, maxIds[Definitions.RESERVATION_FLIGHT]);
	}
	if (maxIds[Definitions.RESERVATION_ROOM] > 0) {
	    manager.manager_reserveRoom(customerId, maxIds[Definitions.RESERVATION_ROOM]);
	}
    }

}
