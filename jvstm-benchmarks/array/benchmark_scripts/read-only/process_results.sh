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

# CUT_VALUE=500

# ## array stats
# for thread in $THREADS; do
#     cat ${OUTDIR}/array-$thread | grep .*,.*,.* | sed -e "s/^/array,$thread,/" 
# done > "${OUTDIR}-stats.all"

# "$HELPER_SCRIPTS_DIR/csv-average/csv-average.sh" $CUT_VALUE "${OUTDIR}-stats.all" K0 K1 V2 V3 V4 | sort -g -k2 -t, > "${OUTDIR}-stats.dat"
# \rm "${OUTDIR}-stats.all"

## array total time and restarts
for thread in $THREADS; do
    TIME=`cat ${OUTDIR}/array-$thread | grep "Benchmark took" | cut -d\  -f3`
    RESTARTS=`cat ${OUTDIR}/array-$thread | grep "restarts" | awk -F' ' '{print $3}' | tail -n 1`
    echo $jvstm_basename,$thread,$TIME,$RESTARTS
done > "${OUTDIR}-prog.dat"

