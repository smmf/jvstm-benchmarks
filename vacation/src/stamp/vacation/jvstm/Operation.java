package stamp.vacation.jvstm;

public abstract class Operation {

    public static boolean nestedParallelismOn;
    public static int numberParallelSiblings;
    public static boolean parallelizeUpdateTables;

    public abstract void doOperation();

}
