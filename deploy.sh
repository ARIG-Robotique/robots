#!/bin/sh
echo "Compilation ..."
./gradlew clean copyDependencies assemble

echo "Déploiement dépendences ..."
scp ./$1-parent/$1-robot/build/dependencies/*.jar $1:/home/pi/$1/libs/

echo "Déploiement Applicatif ..."
scp ./$1-parent/$1-robot/build/libs/$1-*-SNAPSHOT.jar $1:/home/pi/$1/
scp -r ./$1-parent/$1-robot/src/main/scripts/* $1:/home/pi/$1/
