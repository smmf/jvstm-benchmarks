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

writerate="25"
structure="linkedlist"
duration="30000"
warmup="5000"
range="65536"
initialsize="256"

echo $jvstm_basename: -d ${duration} -t ${nthreads} -w $writerate --no-traversals --no-sms

${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH} org.deuce.benchmark.Driver -n ${nthreads} -d $duration -w $warmup org.deuce.benchmark.intset.Benchmark LinkedListJvstm -w $writerate -i $initialsize -r $range > $OUTDIR/linkedlist-${writerate}-${nthreads}.txt 2>&1

