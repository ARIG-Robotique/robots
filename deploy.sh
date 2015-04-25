#!/bin/sh
echo "Compilation ..."
./gradlew clean build

echo "Déploiement ..."
scp ./build/libs/nerell-*-SNAPSHOT.jar 192.168.1.104:/home/pi/nerell/
scp ./src/main/scripts/* 192.168.1.104:/home/pi/nerell/
