package stamp.vacation.jvstm.parnest;

import jvstm.CommitException;
import jvstm.Transaction;

public class DeleteCustomerOperation extends Operation {

    final private Manager managerPtr;
    final private int customerId;
    
    public DeleteCustomerOperation(Manager managerPtr, Random randomPtr, int queryRange) {
	this.managerPtr = managerPtr; 
	this.customerId = randomPtr.posrandom_generate() % queryRange + 1;
    }
    
    @Override
    public void doOperation() {
	while (true) {
	    Transaction tx = Transaction.begin();
	    try {
		int bill = managerPtr.manager_queryCustomerBill(customerId);
		if (bill >= 0) {
		    managerPtr.manager_deleteCustomer(customerId);
		}
		tx.commit();
		tx = null;
		return;
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

}
