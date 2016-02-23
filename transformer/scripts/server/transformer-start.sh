#/bin/bash
cmd="nohup java -jar transformer-1.0-SNAPSHOT.jar"
eval "${cmd}" &>/dev/null &disown
