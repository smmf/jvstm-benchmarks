#!/bin/bash

# Each script plot.sh has available the following variables:
#
# THREADS: the list of thread combinations used to test
# JVSTMS: an array with the list of JVSMTS (full path)
#
# SCRIPT_NAME: the name given to this benchmark script
#
# It is expected that this script will write its output inside OUTDIR (a PDF file with some plot)

OUTDIR="$RESULTS/$SCRIPT_NAME"
GP_FILE="$OUTDIR/stmbench7.gp"
EPS_FILE=${GP_FILE/%.gp/.eps}

# Generate the gnuplot file and process it

echo \
"
set term postscript eps enhanced
set output \"$EPS_FILE\"
set autoscale
unset log
unset label" > $GP_FILE

COUNT=0;
MAX=`echo $THREADS | wc -w`
echo -n "set xtics scale 0 (" >> $GP_FILE
for threads in $THREADS; do
    echo -n "\"$threads\" $COUNT">> $GP_FILE
    let COUNT++
    if [ $COUNT -lt $MAX ]; then
        echo -n ", " >> $GP_FILE
    fi
done
echo ")" >> $GP_FILE

echo \
"
unset mxtics
set datafile separator \",\"
set title \"STMBench7: $SCRIPT_NAME\"
set xlabel \"# threads\"
set ylabel \"Throughput (operations/second)\"
set yrange [0:]
set key left top
set boxwidth 0.7
set size 0.6,0.6
set style fill pattern 18 border -1

set style data histograms
set style histogram clustered gap 1" >> $GP_FILE

echo -n "plot " >> $GP_FILE


array_size=${#JVSTMS[@]}
let pos=0
LAST_POS=`expr $array_size - 1`
while [ $pos -lt $array_size ]; do
    jvstm_basename=`basename ${JVSTMS_BASENAMES[pos]} .jar`
    if [ $pos -eq $LAST_POS ]; then
        echo "\"$OUTDIR/$jvstm_basename-prog.dat\" using 4 title '$jvstm_basename'" >> $GP_FILE
    else
        echo "\"$OUTDIR/$jvstm_basename-prog.dat\" using 4 title '$jvstm_basename', \\" >> $GP_FILE
    fi
    let pos++
done

gnuplot "$GP_FILE"
epstopdf "$EPS_FILE"
