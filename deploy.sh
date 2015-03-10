#!/bin/sh
echo "Compilation ..."
./gradlew clean build

echo "Déploiement ..."
scp main-robot/build/libs/main-robot*-SNAPSHOT.jar 192.168.1.31:/home/pi/prehistobot/
scp main-robot-gui/build/libs/*.jar 192.168.1.31:/home/pi/prehistobot/libs/
