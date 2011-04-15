#!/bin/bash

. ./benchmark_scripts.conf

array_size=${#JVSTMS[@]}
let pos=0
while [ $pos -lt $array_size ]; do
    jvstm=${JVSTMS[pos]}
    jvstm_basename=`basename ${JVSTMS_BASENAMES[pos]} .jar`

    cp -f "$jvstm" lib/jvstm.jar
    echo "----------------------------"
    echo "JVSTM="$jvstm_basename
    echo "----------------------------"
    ant clean-all compile

    CLASSPATH="classes:lib/jvstm.jar"

    for bench in $BENCHMARKS_TO_RUN; do
        BENCH_NAME=`basename $bench`
        OUTDIR="$RESULTS/$BENCH_NAME/$jvstm_basename"
        mkdir -p "$OUTDIR"

        # run the bench.sh
        for nthreads in $THREADS; do
            . ./$bench/bench.sh
        done

        # run the process_results.sh
        . ./$bench/process_results.sh
    done

    let pos++
done

# plot the results per benchmark
for bench in $BENCHMARKS_TO_RUN; do
    BENCH_NAME=`basename $bench`

    . ./$bench/plot.sh
done


