#!/bin/bash

BENCH=stmbench7
CLASSPATH="classes"
JVSTMS="plain tamtp6b"

\rm -rf $RESULTS
ant clean-all compile

for method in stm; do
    for jvstm in $JVSTMS; do
	OUTDIR=$RESULTS/$BENCH/$method/$jvstm
	mkdir -p $OUTDIR
	for i in $THREADS; do
            for load in w; do
		echo $jvstm: -w $load -t $i --no-traversals --no-sms
		${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH}:lib/jvstm.jar.${jvstm}.commitStats stmbench7.Benchmark -g $method -s stmbench7.impl.jvstm.SynchMethodInitializerJVSTM -w $load -l 120 -t $i --no-traversals --no-sms > $OUTDIR/notrav-${load}-nosms-${jvstm}-${i}.txt 2>&1
	    done
        done
    done
done

for method in coarse medium; do
    OUTDIR=$RESULTS/$BENCH/$method
    mkdir -p $OUTDIR
    for i in $THREADS; do
        for load in w; do
            echo $method: -w $load -t $i --no-traversals --no-sms
            ${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH} stmbench7.Benchmark -g $method -w $load -l 120 -t $i --no-traversals --no-sms > $OUTDIR/notrav-${load}-nosms-${method}-${i}.txt 2>&1
        done
    done
done
