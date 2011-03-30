public class ProgramArgs {

    boolean tryReadOnly = true;
    int arraySize = 10000000;
    int nThreads = 8;
    int nTx = 10000;
    int txRatioWOnly = 0;
    int txRatioRW = 3;
    /* WRITE-ONLY */
    int txWOnlyNumOps = 5000;
    int txWOnlyNumOpsDev = 0;
    /* READ-ONLY */
    int txROnlyNumOps = 6000;
    int txROnlyNumOpsDev = 0;
    /* READ-WRITE */
    int txRWNumReads = 50000;
    int txRWNumReadsDev = 0;
    int txRWNumWrites = 35;
    int txRWNumWritesDev = 0;
    boolean show = false;

    // derived values
    int nTxWOnly, nTxRW, nTxROnly;
    int txWOnlyNumOpsDevVal, txROnlyNumOpsDevVal, txRWNumReadsDevVal, txRWNumWritesDevVal;

    public ProgramArgs(String[] args) {
        processCommandLineArgs(args);
        checkArguments();
	computeDerivedValues();
    }

    void checkArguments() {
	StringBuilder errors = new StringBuilder();
	if (arraySize < 0) {
	    errors.append("-arraySize cannot be negative\n");
	}
	if (nThreads < 0) {
	    errors.append("-nThread cannot be negative\n");
	}
	if (nTx < 0) {
	    errors.append("-nTx cannot be negative\n");
	}
	if (txRatioWOnly < 0 || txRatioWOnly > 100) {
	    errors.append("-txRatioWOnly must be between 0 and 100\n");
	}
	if (txRatioRW < 0 || txRatioRW > 100) {
	    errors.append("-txRatioRW must be between 0 and 100\n");
	}
	if (txRatioRW + txRatioWOnly > 100) {
	    errors.append("-txRatioWOnly plus -txRatioRW must not exceed 100\n");
	}
	/* WRITE-ONLY */
	if (txWOnlyNumOps < 0) {
	    errors.append("-txWOnlyNumOps cannot be negative\n");
	}
	if (txWOnlyNumOpsDev < 0 || txWOnlyNumOpsDev > 100) {
	    errors.append("-txWOnlyNumOps must be between 0 and 100\n");
	}
	/* READ-ONLY */
	if (txROnlyNumOps < 0) {
	    errors.append("-txROnlyNumOps cannot be negative\n");
	}
	if (txROnlyNumOpsDev < 0 || txROnlyNumOpsDev > 100) {
	    errors.append("-txROnlyNumOps must be between 0 and 100\n");
	}
	/* READ-WRITE */
	if (txRWNumReads < 0) {
	    errors.append("-txRWNumReads cannot be negative\n");
	}
	if (txRWNumReadsDev < 0 || txRWNumReadsDev > 100) {
	    errors.append("-txRWNumReads must be between 0 and 100\n");
	}
	if (txRWNumWrites < 0) {
	    errors.append("-txRWNumWrites cannot be negative\n");
	}
	if (txRWNumWritesDev < 0 || txRWNumWritesDev > 100) {
	    errors.append("-txRWNumWrites must be between 0 and 100\n");
	}

	if (errors.length() > 0) {
	    errors.insert(0, "\n");
	    error(errors.toString());
	}
    }

    void computeDerivedValues() {
	nTxWOnly = nTx * txRatioWOnly / 100;
	nTxRW = nTx * txRatioRW / 100;
	nTxROnly = nTx - nTxWOnly - nTxRW;

	txWOnlyNumOpsDevVal = txWOnlyNumOps * txWOnlyNumOpsDev / 100;
	txROnlyNumOpsDevVal = txROnlyNumOps * txROnlyNumOpsDev / 100;
	txRWNumReadsDevVal = txRWNumReads * txRWNumReadsDev / 100;
	txRWNumWritesDevVal = txRWNumWrites * txRWNumWritesDev / 100;
	
    }
    
    void dumpRunArgs() {
	computeDerivedValues();

	StringBuilder str = new StringBuilder();
	str.append("// parameters\n");
	str.append("\ntryReadOnly      : " + tryReadOnly);
	str.append("\narraySize        : " + arraySize);
	str.append("\nnThreads         : " + nThreads);
	str.append("\nnTx              : " + nTx);
	str.append("\ntxRatioWOnly     : " + txRatioWOnly);
	str.append("\ntxRatioRW        : " + txRatioRW);
	str.append("\ntxWOnlyNumOps    : " + txWOnlyNumOps);
	str.append("\ntxWOnlyNumOpsDev : " + txWOnlyNumOpsDev);
	str.append("\ntxROnlyNumOps    : " + txROnlyNumOps);
	str.append("\ntxROnlyNumOpsDev : " + txROnlyNumOpsDev);
	str.append("\ntxRWNumReads     : " + txRWNumReads);
	str.append("\ntxRWNumReadsDev  : " + txRWNumReadsDev);
	str.append("\ntxRWNumWrites    : " + txRWNumWrites);
	str.append("\ntxRWNumWritesDev : " + txRWNumWritesDev);
	str.append("\nshow             : " + show);
	str.append("\n\n\nDerived parameters:\n");
	str.append("\nnTxWOnly         : " + nTxWOnly);
	str.append("\nnTxRW            : " + nTxRW);
	str.append("\nnTxROnly         : " + nTxROnly);
	str.append("\ntxWOnlyNumOpsDevVal : " + txWOnlyNumOpsDevVal);
	str.append("\ntxROnlyNumOpsDevVal : " + txROnlyNumOpsDevVal);
	str.append("\ntxRWNumReadsDevVal  : " + txRWNumReadsDevVal);
	str.append("\ntxRWNumWritesDevVal : " + txRWNumWritesDevVal);
	
	System.out.println(str);
	System.exit(0);
    }

    void processCommandLineArgs(String[] args) {
        int num = 0;
        while (num < args.length) {
	    num = processOption(args, num);
        }
    }
    
    int processOption(String[] args, int pos) {
	if (args[pos].equals("-tryReadOnly")) {
	    tryReadOnly = parseBool(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-arraySize")) {
	    arraySize = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-nThreads")) {
	    nThreads = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-nTx")) {
	    nTx = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRatioWOnly")) {
	    txRatioWOnly = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRatioRW")) {
	    txRatioRW = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txWOnlyNumOps")) {
	    txWOnlyNumOps = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txWOnlyNumOpsDev")) {
	    txWOnlyNumOpsDev = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txROnlyNumOps")) {
	    txROnlyNumOps = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txROnlyNumOpsDev")) {
	    txROnlyNumOpsDev = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRWNumReads")) {
	    txRWNumReads = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRWNumReadsDev")) {
	    txRWNumReadsDev = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRWNumWrites")) {
	    txRWNumWrites = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-txRWNumWritesDev")) {
	    txRWNumWritesDev = parseInt(getNextArgument(args, pos));
	    return pos + 2;
	} else if (args[pos].equals("-show")) {
	    show = true;
	    return pos + 1;
	} else if (args[pos].equals("-dump")) {
	    dumpRunArgs();
            return pos + 1;
	} else if (args[pos].equals("-help")) {
	    dumpHelp();
            return pos + 1;
        } else {
	    error("unrecognized option " + args[pos] + ". Try -help");
	}
        return Integer.MAX_VALUE;
    }
    
    String getNextArgument(String[] args, int pos) {
        int nextPos = pos + 1;
        if (nextPos < args.length) {
            return args[nextPos];
        } else {
            error("option " + args[pos] + " requires argument");
        }
        return null;
    }
    
    int parseInt(String intStr) {
	int value = -1;
	try {
	    value = Integer.parseInt(intStr);
	} catch (NumberFormatException nfe) {
	    error("invalid integer number as argument");
	}
	return value;
    }

    boolean parseBool(String boolStr) {
	return Boolean.parseBoolean(boolStr);
    }

    void dumpHelp() {
	StringBuilder str = new StringBuilder();
	str.append("Available options:                                                      : Current value");
	str.append("\n-tryReadOnly      Whether to speculatively start a tx as read-only    : " + tryReadOnly);
	str.append("\n-arraySize        The size of the data array (in number of positions) : " + arraySize);
	str.append("\n-nThreads         The number of worker threads in the pool.           : " + nThreads);
	str.append("\n-nTx              The number of transactions to run.                  : " + nTx);
	str.append("\n-txRatioWOnly     The percentage (0..100) of write-only transactions. : " + txRatioWOnly);
	str.append("\n-txRatioRW        The percentage (0..100) of read-write transactions. : " + txRatioRW);
	/* WRITE-ONLY */
	str.append("\n-txWOnlyNumOps    The number of boxes to write in a write-only tx     : " + txWOnlyNumOps);
	str.append("\n-txWOnlyNumOpsDev The deviation allowed (percentage) in WOnlyNumOps   : " + txWOnlyNumOpsDev);
	/* READ-ONLY */
	str.append("\n-txROnlyNumOps    The number of boxes to read in a read-only tx       : " + txROnlyNumOps);
	str.append("\n-txROnlyNumOpsDev The deviation allowed (percentage) in ROnlyNumOps   : " + txROnlyNumOpsDev);
	/* READ-WRITE */
	str.append("\n-txRWNumReads     The number of boxes to read in a r/w tx             : " + txRWNumReads);
	str.append("\n-txRWNumReadsDev  The deviation allowed (percentage) in RWNumReads    : " + txRWNumReadsDev);
	str.append("\n-txRWNumWrites    The number of boxes to write in a r/w tx            : " + txRWNumWrites);
	str.append("\n-txRWNumWritesDev The deviation allowed (percentage) in RWNumWrites   : " + txRWNumWritesDev);
	str.append("\n-show             Print the final array after the benchmark           : " + show);

	System.out.println(str);
	System.exit(0);
    }

    void error(String msg) {
        System.err.println("Error: " + msg);
        System.exit(1);
    }
}