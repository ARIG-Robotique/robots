#!/bin/sh
echo "Compilation ..."
./gradlew clean build

echo "Déploiement ..."
#scp ./build/libs/nerell-*-SNAPSHOT.jar 10.42.0.196:/home/pi/nerell/
#scp -r ./src/main/scripts/* 10.42.0.196:/home/pi/nerell/

scp ./build/libs/nerell-*-SNAPSHOT.jar 192.168.1.50:/home/pi/nerell/
scp -r ./src/main/scripts/* 192.168.1.50:/home/pi/nerell/