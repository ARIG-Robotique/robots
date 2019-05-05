#!/bin/sh
./stopAll
sudo rm -f /tmp/lidar.sock
java -Dspring.profiles.active=default -Dequipe=VIOLET jar nerell-robot-2019-SNAPSHOT.jar
