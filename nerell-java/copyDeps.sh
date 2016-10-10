#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies

echo "Déploiement ..."
scp ./build/dependencies/*.jar $1:/home/pi/nerell/libs/
