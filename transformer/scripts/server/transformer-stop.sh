#/bin/bash
kill `ps aux | awk '/transformer-1.0-SNAPSHOT\.jar/ { print $2}'` &> /dev/null