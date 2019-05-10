#!/bin/sh
./stopAll
rm logs/0-traces.log

java -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -XX:MaxGCPauseMillis=5 -jar nerell-utils-2019-SNAPSHOT.jar
