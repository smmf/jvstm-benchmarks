#!/bin/bash

this_prog_dir=`dirname $0`
current_dir=`pwd`

if ! [ "$this_prog_dir" == "$current_dir" -o "$this_prog_dir" == "." ]; then
    echo "This script MUST be executed from its own directory (i.e. cd to '$this_prog_dir')"
    exit
fi

# where to place the results (both inside the benchmarks and at the top level)
RESULTS="results"

# process configuration
source ./jvstm-benchmarks.conf

echo "--- Cleaning ${RESULTS}..."
\rm -rf $RESULTS
for benchmark in $BENCHMARKS; do
    echo "--- Cleaning ${benchmark}..."
    if [ ! -d "$benchmark" ]; then
        echo "       Warning: NOT A VALID BENCHMARK DIRECTORY: '$benchmark'. I skipped it." 
        continue;
    fi
    if [ ! -f "${benchmark}/clean.sh" ]; then
        echo "       Warning: Could not find '${benchmark}/clean.sh'. No cleaning performed."
        continue;
    fi
    cd "$benchmark"
    . ./clean.sh
    cd ..

done

echo
echo "Done."
echo


