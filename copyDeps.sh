#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies

echo "Déploiement ..."
scp ./nerell-java/build/dependencies/*.jar $1:/home/pi/nerell/libs/
