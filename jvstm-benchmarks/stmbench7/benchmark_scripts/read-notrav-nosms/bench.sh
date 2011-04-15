#!/bin/bash

# Each script bench.sh has available the following variables:
#
# JAVA: path to the JVM
# JAVA_OPTS: configuration options to pass to the JVM
# CLASSPATH: should already include the application code and JVSTM.
#
# THREADS: the list of threads to test
# nthreads: the current number of threads to test
# BENCH_NAME: the name given to this benchmark
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $BENCH_NAME/$jvstm_basename
#
# It is expected that this script will write its output to OUTDIR

method="stm"
load="r"
duration="30"

echo $jvstm: -w $load -t $i --no-traversals --no-sms

${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH} stmbench7.Benchmark -g $method -s stmbench7.impl.jvstm.SynchMethodInitializerJVSTM -w $load -l $duration -t ${nthreads} --no-traversals --no-sms > $OUTDIR/notrav-${load}-nosms-${nthreads}.txt 2>&1

