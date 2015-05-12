#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies

echo "Déploiement ..."
#scp ./build/dependencies/*.jar 10.42.0.196:/home/pi/nerell/libs/

scp ./build/dependencies/*.jar 192.168.1.50:/home/pi/nerell/libs/
