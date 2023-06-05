#!/bin/bash

homedir=../${pwd}
projectHome=$homedir/DATSI/SD/BitCascade.2023

openfortivpn &
pid=$!

# Wait for openfortivpn to establish a connection (adjust the condition as needed)
while ! pgrep openfortivpn >/dev/null; do
    sleep 1
done

scp -r $projectHome c200172@triqui1.fi.upm.es:/homefi/alumnos/c/c200172/
yes s | entrega.sd BitCascade.2023
kill pid