#!/bin/bash

# Each script process_results.sh has available the following variables:
#
# THREADS: the list of thread combinations used to test
# JVSTMS: an array with the list of JVSMTS (full path)
#
# SCRIPT_NAME: the name given to this benchmark script
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
# HELPER_SCRIPTS_DIR: directory containing any helper scripts used in processing the results
#
# It is expected that this script will write its output inside OUTDIR

CUT_VALUE=0
BOARDS="src/memboard.txt src/mainboard.txt"

# prog
for board_path in $BOARDS; do
    board=`basename $board_path .txt`
    for threads in $THREADS; do
	cat $OUTDIR/$board-$threads.txt | grep .*,.*,.*, | sed -e "s/^/$SCRIPT_NAME,/"
    done > $OUTDIR/${board}-prog.all 

    $HELPER_SCRIPTS_DIR/csv-average/csv-average.sh $CUT_VALUE $OUTDIR/${board}-prog.all K0 K1 V2 V8 | sort -g -k2 -t, > $OUTDIR/${board}-prog.dat
done

