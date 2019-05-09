#!/bin/sh
./stopAll
sudo rm -f /tmp/lidar.sock
java -Dspring.profiles.active=default,monitoring -Dequipe=VIOLET -Dstrategies=$1 -jar nerell-robot-2019-SNAPSHOT.jar
