#!/bin/sh
echo "Compilation ..."
./gradlew clean build

echo "Déploiement ..."
scp ./nerell-java/build/libs/nerell-*-SNAPSHOT.jar $1:/home/pi/nerell/
scp -r ./src/main/scripts/* $1:/home/pi/nerell/
