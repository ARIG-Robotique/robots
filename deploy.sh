#!/bin/sh
echo "Compilation ..."
./gradlew clean build

echo "Déploiement ..."
scp ./build/libs/nerell-*-SNAPSHOT.jar 192.168.1.31:/home/pi/nerell/
scp -r ./src/main/scripts/* 192.168.1.31:/home/pi/nerell/
