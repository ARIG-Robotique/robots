#!/bin/sh
./stopAll
sudo rm -f /tmp/lidar.sock
java -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -XX:MaxGCPauseMillis=5 -Dspring.profiles.active=default -Dequipe=VIOLET -Dstrategies=$1 -jar nerell-robot-2019-SNAPSHOT.jar
