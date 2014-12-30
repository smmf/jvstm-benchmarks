#!/bin/bash

# Each script bench.sh has available the following variables:
#
# JAVA: path to the JVM
# JAVA_OPTS: configuration options to pass to the JVM
# CLASSPATH: should already include the application code and JVSTM.
#
# THREADS: the list of threads to test
# nthreads: the current number of threads to test
# SCRIPT_NAME: the name given to this benchmark script
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
#
# It is expected that this script will write its output to OUTDIR

TRY_READ_ONLY=true
ARRAYSIZE=100000
NTX=1000
TX_RATIO_WONLY=0
TX_RATIO_RW=0
TX_WONLY_NOPS=0
TX_WONLY_NOPS_DEV=0
TX_RONLY_NOPS=1000
TX_RONLY_NOPS_DEV=0
TX_RW_NREADS=1000
TX_RW_NREADS_DEV=0
TX_RW_NWRITES=10
TX_RW_NWRITES_DEV=0

${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH} Main \
    -tryReadOnly ${TRY_READ_ONLY} -arraySize ${ARRAYSIZE} -nThreads ${nthreads} -nTx ${NTX}\
    -txRatioWOnly ${TX_RATIO_WONLY} -txRatioRW ${TX_RATIO_RW}\
    -txWOnlyNumOps ${TX_WONLY_NOPS} -txWOnlyNumOpsDev ${TX_WONLY_NOPS_DEV}\
    -txROnlyNumOps ${TX_RONLY_NOPS} -txROnlyNumOpsDev ${TX_RONLY_NOPS_DEV}\
    -txRWNumReads ${TX_RW_NREADS} -txRWNumReadsDev ${TX_RW_NREADS_DEV}\
    -txRWNumWrites ${TX_RW_NWRITES} -txRWNumWritesDev ${TX_RW_NWRITES_DEV}\
    > $OUTDIR/array-${nthreads}

