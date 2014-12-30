package stamp.vacation.jvstm.parnest.annotated;

import java.util.List;

import jvstm.Atomic;
import jvstm.atomic.Combiner;
import jvstm.atomic.ParNest;
import jvstm.atomic.ParallelSpawn;

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

    @Atomic(speculativeReadOnly = false)
    public void doOperation() {
	boolean isFound = false;
	if (Operation.nestedParallelismOn) {
	    isFound = new ForkPoint().exec();
	} else {
	    isFound = makeReservationNotNested();
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

    private class ForkPoint implements ParallelSpawn<Boolean> {

	@ParNest(readOnly = true)
	public Boolean batchProcess(int min, int max) {
	    boolean isFound = false;
	    int n;
	    for (n = min; n < max; n++) {
		isFound = process(n);
	    }
	    return isFound;
	}

	@Combiner
	public Boolean combine(List<Boolean> results) {
	    for (Boolean res : results) {
		if (res) {
		    return true;
		}
	    }
	    return false;
	}
	
	@Override
	public Boolean exec() {
	    int queriesPerTx = numQuery / Operation.numberAvailableThreads;
	    for (int n = 0; n < numQuery; n += queriesPerTx) {
		batchProcess(n, n + queriesPerTx);
	    }
	    
	    return null;
	}
    }

    private boolean makeReservationNotNested() {
	boolean isFound = false;
	int n;
	for (n = 0; n < numQuery; n++) {
	    isFound = process(n);
	}
	
	return isFound;
    }

    protected boolean process(int n) {
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
	    return true;
	}
	return false;
    }

}
