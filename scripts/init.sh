#!/bin/bash

cd ../DATSI/SD/BitCascade.2023

cd tracker_node
./start_rmiregistry 23456 &
./execute_tracker.sh 23456 mi_tracker && cd .. # nombre del tracker para depurar

cd tracker_node
./start_rmiregistry 23456 &
./execute_tracker.sh 23456 mi_tracker && cd ..  # nombre del tracker para depurar

cd peer_node
./execute_downloader.sh localhost 23456 down1 Fichero 
# en peer_node/bin/down1/Fichero debe quedar una copia del fichero