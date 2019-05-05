#!/bin/sh
./stopAll
sudo rm -f /tmp/lidar.sock
java -Dspring.profiles.active=default,monitoring -Dequipe=VIOLET -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y -jar nerell-robot-2019-SNAPSHOT.jar
