#!/bin/sh
set -e

if [ -z $1 ] ; then
    echo "Il faut préciser le nom du robot à déployé"
    exit 1
fi

ROBOT_NAME=$1
INSTALL_DIR=/home/pi/$ROBOT_NAME

echo "Compilation ..."
./gradlew assemble

echo "Déploiement Applicatif ..."
scp ./$ROBOT_NAME-parent/$ROBOT_NAME-robot/build/libs/$ROBOT_NAME-robot-1.1.0-SNAPSHOT-exec.jar $ROBOT_NAME:$INSTALL_DIR/$ROBOT_NAME-robot-1.1.0-SNAPSHOT.jar
scp -r ./$ROBOT_NAME-parent/$ROBOT_NAME-robot/src/main/scripts/*.sh $ROBOT_NAME:$INSTALL_DIR/

echo "Déploiement Utils ..."
scp ./$ROBOT_NAME-parent/$ROBOT_NAME-utils/build/libs/$ROBOT_NAME-utils-1.1.0-SNAPSHOT.jar $ROBOT_NAME:$INSTALL_DIR/
scp -r ./$ROBOT_NAME-parent/$ROBOT_NAME-utils/src/main/scripts/*.sh $ROBOT_NAME:$INSTALL_DIR/
