#!/bin/sh
./stopAll
#sudo java -cp "nerell-utils.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y org.arig.robot.SogelinkGame
sudo java -cp "nerell-utils.jar:./libs/*" org.arig.robot.SogelinkGame
sudo poweroff