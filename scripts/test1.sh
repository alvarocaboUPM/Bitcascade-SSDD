homedir = ../${pwd}

cd $homedir/tracker_node && \
./start_rmiregistry 23456 & 
./execute_tracker.sh 23456 mi_tracker # nombre del tracker para depurar

cd $homedir/peer_node
./execute_publisher.sh localhost 23456 mi_publisher cualquier_cosa 512 
