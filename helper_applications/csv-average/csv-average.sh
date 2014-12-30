#!/bin/bash

TARGET=`readlink $0`
RES=`echo $?`

if [ $RES == "0" ]; then
    PROG_DIR=`dirname $TARGET`;
else
    PROG_DIR=`dirname $0`
fi

java -Xmx900m -jar $PROG_DIR/csv-average.jar $*

