#! /bin/sh

if [ $# -ne 1 ]; then
	echo "Pass a parameter with the path to the jvstm jar"
	exit
fi

jvstmlib="$1"
vacation="stamp/vacation/jvstm"

cd src

rm $vacation/*.class

javac -cp $jvstmlib:. $vacation/Vacation.java

java -Xms1024m -Xmx2048m -cp $jvstmlib:. $vacation/Vacation -c 4 -n 480000 -q 90 -u 98 -r 10485 -t 96 -nest false -sib 1 -updatePar false

rm $vacation/*.class
