#!/bin/bash

cd /opt/autodoc
./transformer-stop.sh
#cmd="nohup java -jar transformer-1.0-SNAPSHOT.jar"

#eval "${cmd}" &>/dev/null &disown
./transformer-start.sh
