package stamp.vacation.jvstm.parnest.annotated;

import jvstm.Atomic;

public class DeleteCustomerOperation extends Operation {

    final private Manager managerPtr;
    final private int customerId;
    
    public DeleteCustomerOperation(Manager managerPtr, Random randomPtr, int queryRange) {
	this.managerPtr = managerPtr; 
	this.customerId = randomPtr.posrandom_generate() % queryRange + 1;
    }
    
    @Atomic(speculativeReadOnly = false)
    public void doOperation() {
	int bill = managerPtr.manager_queryCustomerBill(customerId);
	if (bill >= 0) {
	    managerPtr.manager_deleteCustomer(customerId);
	}
    }

}
