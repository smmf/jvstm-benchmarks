#!/bin/bash

## Each script process_results.sh has available the following variables:
##
## THREADS: the list of thread combinations used to test
## JVSTMS: an array with the list of JVSMTS (full path)
##
## SCRIPT_NAME: the name given to this benchmark script
## jvstm_basename: the JVSTM being tested (without .jar extension)
## OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
## HELPER_SCRIPTS_DIR: directory containing any helper scripts used in processing the results
##
## It is expected that this script will write its output inside OUTDIR
#
#trav="notrav"
#method="stm"
#load="r"
#duration="30"
#
#for thread in $THREADS; do
#    FILE="$OUTDIR/${trav}-${load}-nosms-${thread}.txt"
#    N_THREADS=`echo \`basename $FILE\` | cut -d\- -f 4 | cut -d. -f1`
#    TOTAL_ERROR=`grep "sample error" $FILE | cut -d\  -f 4`
#    TOTAL_ERROR_INCL_FAILED=`grep "sample error" $FILE | cut -d\( -f 2 | cut -d\  -f1`
#    THROUGHPUT=`grep Total\ throughput $FILE | cut -d\  -f 3`
#    THROUGHPUT_INCL_FAILED=`grep Total\ throughput $FILE | cut -d\  -f 5 | cut -d\( -f2`
#    TIME=`grep Elapsed\ time: $FILE | cut -d\  -f3`
#
#    STM_AUX=`grep Conflicts $FILE`
#
#    RW=`echo $STM_AUX | cut -d\  -f3 | cut -d, -f1`
#    RO=`echo $STM_AUX | cut -d\  -f6 | cut -d, -f1`
#    CONFLICTS=`echo $STM_AUX | cut -d\  -f9`
#    CONFLICTS_PERCENT=`echo $STM_AUX | cut -d\  -f10 | cut -d, -f1 | cut -d\( -f2 | cut -d\) -f1`
#    RESTARTS=`echo $STM_AUX | cut -d\  -f13 | cut -d, -f1`
#    RESTARTS_PERCENT=`echo $STM_AUX | cut -d\  -f14 | cut -d, -f1 | cut -d\( -f2 | cut -d\) -f1`
#    ABORTS=`echo $STM_AUX | cut -d\  -f17 | cut -d, -f1`
#
#    echo $trav,$load,$N_THREADS,$TOTAL_ERROR,$TOTAL_ERROR_INCL_FAILED,$THROUGHPUT,$THROUGHPUT_INCL_FAILED,$TIME,$RW,$RO,$CONFLICTS,$CONFLICTS_PERCENT,$RESTARTS,$RESTARTS_PERCENT,$ABORTS
#done > "${OUTDIR}-prog.all"
#
#CUT_VALUE=0
#$HELPER_SCRIPTS_DIR/csv-average/csv-average.sh $CUT_VALUE "${OUTDIR}-prog.all" K0 K1 K2 V5 | sort -g -k3 -t, > "${OUTDIR}-prog.dat"
