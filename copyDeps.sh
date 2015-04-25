#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies

echo "Déploiement ..."
scp ./build/dependencies/*.jar 192.168.1.104:/home/pi/nerell/libs/
