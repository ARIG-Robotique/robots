#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies

echo "Déploiement ..."
scp main-robot/build/dependencies/*.jar 192.168.1.31:/home/pi/prehistobot/libs/
