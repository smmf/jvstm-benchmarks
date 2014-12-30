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

    CLASSPATH="bin/classes:bin/tests:lib/jvstm.jar"

    for bench_script in $BENCH_SCRIPTS_TO_RUN; do
        SCRIPT_NAME=`basename $bench_script`
        OUTDIR="$RESULTS/$SCRIPT_NAME/$jvstm_basename"
        mkdir -p "$OUTDIR"

        # run the bench.sh
        for nthreads in $THREADS; do
            . ./$bench_script/bench.sh
        done

        # run the process_results.sh
        . ./$bench_script/process_results.sh
    done

    let pos++
done

# plot the results per benchmark script
for bench_script in $BENCH_SCRIPTS_TO_RUN; do
    SCRIPT_NAME=`basename $bench_script`

    . ./$bench_script/plot.sh
done


