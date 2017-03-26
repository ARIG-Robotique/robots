#!/bin/sh
ssh $1 "mkdir -p /home/pi/$1/libs"

if [ "$2" == "deps" ] ; then
    echo "Déploiement dépendences ..."
    ./gradlew clean copyDependencies
    scp ./$1-parent/$1-robot/build/dependencies/*.jar $1:/home/pi/$1/libs/
    scp ./$1-parent/$1-utils/build/dependencies/*.jar $1:/home/pi/$1/libs/
fi

echo "Compilation ..."
./gradlew assemble

echo "Déploiement Applicatif ..."
scp ./$1-parent/$1-robot/build/libs/$1-*-SNAPSHOT.jar $1:/home/pi/$1/
scp -r ./$1-parent/$1-robot/src/main/scripts/* $1:/home/pi/$1/

echo "Déploiement Utils ..."
scp ./$1-parent/$1-utils/build/libs/$1-*-SNAPSHOT.jar $1:/home/pi/$1/
scp -r ./$1-parent/$1-utils/src/main/scripts/* $1:/home/pi/$1/
