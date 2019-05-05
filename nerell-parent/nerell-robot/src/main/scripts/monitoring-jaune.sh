#!/bin/sh
./stopAll
sudo rm -f /tmp/lidar.sock
java -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m  -Dspring.profiles.active=default,monitoring -Dequipe=JAUNE -jar nerell-robot-2019-SNAPSHOT.jar
