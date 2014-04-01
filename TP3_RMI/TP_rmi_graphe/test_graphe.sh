#!/bin/bash

cd bin

rmiregistry &

sleep 0.1
java server.CreateNode 1 &
sleep 0.1
java server.CreateNode 2 &
sleep 0.1
java server.CreateNode 3 &
sleep 0.1
java server.CreateNode 4 &
sleep 0.1
java server.CreateNode 5 &
sleep 0.1
java server.CreateNode 6 &
sleep 0.1

java client.CreateEdge 1 2
java client.CreateEdge 2 3
java client.CreateEdge 2 4
java client.CreateEdge 1 5
java client.CreateEdge 5 6
java client.CreateEdge 4 6

java client.SendMsg 3 monMessage

javapid=`ps | grep java | cut -d ' ' -f 1`
killall rmiregistry
kill $javapid

cd ..

