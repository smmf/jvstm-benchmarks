#!/bin/bash

this_prog_dir=`dirname $0`
current_dir=`pwd`

if ! [ "$this_prog_dir" == "$current_dir" -o "$this_prog_dir" == "." ]; then
    echo "This script MUST be executed from its own directory (i.e. cd to '$this_prog_dir')"
    exit
fi

# where to place the results (both inside the benchmarks and at the top level)
RESULTS="results"
HELPER_SCRIPTS_DIR=`pwd`/"helper_applications"

# process configuration
source ./jvstm-benchmarks.conf
JAVA_OPTS="$DEFAULT_JAVA_OPTS"
# if needed, set the default value for JVSTMS
if [ -z "$JVSTMS" ]; then
    let pos=0;
    for line in `ls jvstms_go_in_here/*.jar`; do
        JVSTMS[$pos]=`pwd`/$line
        let pos++
    done
fi
if [ -z "$JVSTMS" ]; then
    echo "I need at least one jar to run any benchmarks. Exiting."
    exit
fi
# compute the JVSTMS basenames
let pos=0
while [ $pos -lt ${#JVSTMS[@]} ]; do
    JVSTMS_BASENAMES[$pos]=`basename "${JVSTMS[pos]}"`
    let pos++
done

echo "Using:"
echo "  JAVA = "$JAVA
echo "  JAVA_OPTS = "$JAVA_OPTS
echo "  THREADS = "$THREADS
echo "  BENCHMARKS = "$BENCHMARKS
echo "  JVSTMS = "${JVSTMS_BASENAMES[@]}
echo "  RESULTS = "$RESULTS
echo

\rm -rf $RESULTS
mkdir -p $RESULTS
for benchmark in $BENCHMARKS; do
    ## default JAVA_OPTS are reset in case someone decides to override the value in an inner script
    JAVA_OPTS="$DEFAULT_JAVA_OPTS"

    \rm -rf "$benchmark/$RESULTS"
    mkdir "$benchmark/$RESULTS"
    if [ ! -d "$benchmark" ]; then
        echo "NOT A VALID BENCHMARK DIRECTORY: '$benchmark'. I skipped it." 
        continue;
    fi
    echo "----------------------------------"
    echo "Running benchmark: "$benchmark;
    echo "----------------------------------"
    cd "$benchmark"
    . ./run.sh  # this script is sourced because bash has no way of exporting arrays as environment variables
    cd ..

    for result in `find "$benchmark/$RESULTS" -name \*.pdf`; do
        DIR=`dirname $result`
        SCRIPT_NAME=${DIR/#$benchmark\/$RESULTS\//}
        TARGET_DIR="$RESULTS/$benchmark/$SCRIPT_NAME"
        mkdir -p "$TARGET_DIR"
        cp "$result" "$TARGET_DIR"/`basename $result`
    done
done

echo
echo "Done with benchmarks. Results are in dir '$RESULTS'."
echo


