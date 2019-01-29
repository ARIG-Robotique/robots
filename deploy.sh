#!/bin/sh

if [ -z $1 ] ; then
    echo "Il faut préciser le nom du robot à déployé"
    exit 1
fi

ROBOT_NAME=$1
INSTALL_DIR=/home/pi/$ROBOT_NAME
ssh $ROBOT_NAME "mkdir -p $INSTALL_DIR/libs"

#echo "Déploiement dépendences ..."
#./gradlew clean copyDependencies
ssh $ROBOT_NAME rm -vf /home/pi/$ROBOT_NAME/libs/*
#scp ./$ROBOT_NAME-parent/$ROBOT_NAME-robot/build/dependencies/*.jar $ROBOT_NAME:$INSTALL_DIR/libs/
#scp ./$ROBOT_NAME-parent/$ROBOT_NAME-utils/build/dependencies/*.jar $ROBOT_NAME:$INSTALL_DIR/libs/

echo "Compilation ..."
./gradlew assemble

echo "Déploiement Applicatif ..."
scp ./$ROBOT_NAME-parent/$ROBOT_NAME-robot/build/libs/$ROBOT_NAME-*-SNAPSHOT.jar $ROBOT_NAME:$INSTALL_DIR/
scp -r ./$ROBOT_NAME-parent/$ROBOT_NAME-robot/src/main/scripts/* $ROBOT_NAME:$INSTALL_DIR/

#echo "Déploiement service ..."
#ssh $ROBOT_NAME sudo mv $INSTALL_DIR/$ROBOT_NAME.service /lib/systemd/system/
#ssh $ROBOT_NAME sudo systemctl daemon-reload

#echo "Déploiement Utils ..."
#scp ./$ROBOT_NAME-parent/$ROBOT_NAME-utils/build/libs/$ROBOT_NAME-*-SNAPSHOT.jar $ROBOT_NAME:$INSTALL_DIR/
#scp -r ./$ROBOT_NAME-parent/$ROBOT_NAME-utils/src/main/scripts/*.sh $ROBOT_NAME:$INSTALL_DIR/
