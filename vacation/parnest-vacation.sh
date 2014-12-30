#! /bin/sh

if [ $# -ne 1 ]; then
	echo "Pass a parameter with the path to the jvstm jar"
	exit
fi

jvstmlib="$1"
vacation="stamp/vacation/jvstm/parnest"

cd src

rm $vacation/*.class

javac -cp $jvstmlib:. $vacation/Vacation.java

echo ""
echo "Without nesting, 4 top-level transactions"
java -Xms1024m -Xmx2048m -cp $jvstmlib:. $vacation/Vacation -c 4 -n 480000 -q 90 -u 98 -r 10485 -t 40 -nest false -sib 1 -updatePar false

echo ""
echo "With parallel nesting, 1 top-level transaction, with 4 siblings"
java -Xms1024m -Xmx2048m -cp $jvstmlib:. $vacation/Vacation -c 1 -n 480000 -q 90 -u 98 -r 10485 -t 40 -nest true -sib 4 -updatePar true

echo ""
echo "With serial parallel nesting, 4 top-level transactions, with 1 sibling each"
java -Xms1024m -Xmx2048m -cp $jvstmlib:. $vacation/Vacation -c 4 -n 480000 -q 90 -u 98 -r 10485 -t 40 -nest true -sib 1 -updatePar true

rm $vacation/*.class
