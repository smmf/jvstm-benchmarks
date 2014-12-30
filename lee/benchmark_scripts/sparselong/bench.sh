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

####################
# WOULD LIKE TO CONFIGURE NUMBER OF RUNS!!! by sourcing a config file. and boards BTW
############################

BOARDS="src/sparselong.txt"

for board_path in ${BOARDS}; do
    board=`basename $board_path .txt`
    OUTFILE="$OUTDIR/${board}-${nthreads}.txt"
    echo $board: $jvstm_basename, $nthreads 
        # clear output file in case it exists
    echo -n > `pwd`/"$OUTFILE"
    for run in 1 2 3; do
	${JAVA} ${JAVA_OPTS} -cp ${CLASSPATH} jvstm.dual.LeeRouter $nthreads ${board_path} >> "$OUTFILE"
    done
done
