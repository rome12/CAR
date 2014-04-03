#!/bin/bash

cd bin
rmiregistry &
javapid=""
verbose="-v"
message="monMessage"

echo "##Creation des noeuds"
for i in {1..6}
do
	sleep 0.1
	if [[ $1 == $verbose ]]
		then java server.CreateNode $i &
		else java server.CreateNode $i > ../tests/$i.txt &
	fi
	javapid=$javapid" "$!
done
echo "##Creation des noeuds terminée"

echo "##Creation des branches"
java client.CreateEdge 1 2
java client.CreateEdge 2 3
java client.CreateEdge 2 4
java client.CreateEdge 1 5
java client.CreateEdge 5 6
java client.CreateEdge 4 6

echo "##Envoie du message"
java client.SendMsg 1 $message

echo "##Suppression des noeuds"
for i in {1..6}
do
	java server.RemoveNode $i
done
echo "##Suppression des noeuds terminée"

#javapid=`ps | grep java | cut -d ' ' -f 2`

killall rmiregistry
kill $javapid

cd ..

