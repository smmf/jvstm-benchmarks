package stamp.vacation.jvstm.parnest.annotated;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jvstm.Atomic;
import jvstm.CommitException;
import jvstm.EarlyAbortException;
import jvstm.ParallelTask;
import jvstm.Transaction;
import jvstm.atomic.Combiner;
import jvstm.atomic.ParNest;
import jvstm.atomic.ParallelSpawn;

public class UpdateTablesOperation extends Operation {

    final private Manager managerPtr;
    final private int[] types;
    final private int[] ids;
    final private int[] ops;
    final private int[] prices;
    final private int numUpdate;

    public UpdateTablesOperation(Manager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange) {
	this.managerPtr = managerPtr;
	this.types = new int[numQueryPerTransaction];
	this.ids = new int[numQueryPerTransaction];
	this.ops = new int[numQueryPerTransaction];
	this.prices = new int[numQueryPerTransaction];

	int[] baseIds = new int[20];
	for (int i = 0; i < 20; i++) {
	    baseIds[i] = (randomPtr.random_generate() % queryRange) + 1;
	}
	
	this.numUpdate = numQueryPerTransaction;
	int n;
	for (n = 0; n < numUpdate; n++) {
	    types[n] = randomPtr.posrandom_generate() % Definitions.NUM_RESERVATION_TYPE;
	    ids[n] = baseIds[n % 20];
	    ops[n] = randomPtr.posrandom_generate() % 2;
	    if (ops[n] == 1) {
		prices[n] = ((randomPtr.posrandom_generate() % 5) * 10) + 50;
	    }
	}
    }

    @Atomic(speculativeReadOnly = false)
    public void doOperation() {
	if (Operation.nestedParallelismOn && Operation.parallelizeUpdateTables) {
	    new ForkPoint().exec();
	} else {
	    updateTablesNotNested();
	}
    }

    private class ForkPoint implements ParallelSpawn<Void> {

	@ParNest
	public Void execute(List<Integer> operations) {
	    for (int n : operations) {
		process(n);
	    }
	    return null;
	}

	@Combiner
	public Void combine(List<Void> voids) {
	    return null;
	}
	
	@Override
	public Void exec() {
	    int n;
	    List<Integer> type1 = new ArrayList<Integer>();
	    List<Integer> type2 = new ArrayList<Integer>();
	    List<Integer> type3 = new ArrayList<Integer>();
	    for (n = 0; n < numUpdate; n ++) {
		int t = types[n];
		if (t == Definitions.RESERVATION_CAR) {
		    type1.add(n);
		} else if (t == Definitions.RESERVATION_FLIGHT) {
		    type2.add(n);
		} else if (t == Definitions.RESERVATION_ROOM) {
		    type3.add(n);
		}
	    }
	    execute(type1);
	    execute(type2);
	    execute(type3);
	    return null;
	}

    }

    private void updateTablesNotNested() {
	int n;
	for (n = 0; n < numUpdate; n++) {
	    process(n);
	}
    }

    protected void process(int n) {
	int t = types[n];
	int id = ids[n];
	int doAdd = ops[n];
	if (doAdd == 1) {
	    int newPrice = prices[n];
	    if (t == Definitions.RESERVATION_CAR) {
		managerPtr.manager_addCar(id, 100, newPrice);
	    } else if (t == Definitions.RESERVATION_FLIGHT) {
		managerPtr.manager_addFlight(id, 100, newPrice);
	    } else if (t == Definitions.RESERVATION_ROOM) {
		managerPtr.manager_addRoom(id, 100, newPrice);
	    } else {
		assert (false);
	    }
	} else { /* do delete */
	    if (t == Definitions.RESERVATION_CAR) {
		managerPtr.manager_deleteCar(id, 100);
	    } else if (t == Definitions.RESERVATION_FLIGHT) {
		managerPtr.manager_deleteFlight(id);
	    } else if (t == Definitions.RESERVATION_ROOM) {
		managerPtr.manager_deleteRoom(id, 100);
	    } else {
		assert (false);
	    }
	}
    }

}
