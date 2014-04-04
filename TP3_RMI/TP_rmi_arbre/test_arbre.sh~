#!/bin/bash

cd bin
rmiregistry &
javapid=""
verbose="-v"
message="monMessage"
nbNode=6

echo "##Creation des noeuds"
for i in $(seq 1 $nbNode)
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

echo "##Envoie du message"
java client.SendMsg 1 $message

echo "##Suppression des noeuds"
for i in $(seq 1 $nbNode)
do
	java client.RemoveNode $i
done
echo "##Suppression des noeuds terminée"

#javapid=`ps | grep java | cut -d ' ' -f 2`

killall rmiregistry
kill $javapid

if [[ $1 != $verbose ]]
	then
	result=0
	for i in $(seq 1 $nbNode)
	do
			result=$(($result + $(grep -c $message ../tests/$i.txt)))
	done
	if [[ $result < $nbNode ]]
		then echo "*** ECHEC DU TEST *** (au moins un noeud n'a pas eu le message)"
		else echo "*** REUSSITE DU TEST*** (tous les noeuds ont eu le message)"
	fi	
fi

cd ..

