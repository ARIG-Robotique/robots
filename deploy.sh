#!/bin/bash
set -e

if [[ -z ${1} ]] ; then
    echo "Il faut préciser le nom du robot à déployer"
    exit 1
fi

echo "$(date)"

ROBOT_NAME=${1}
HOME_DIR=/home/pi
DESKTOP_DIR=${HOME_DIR}/Desktop
INSTALL_DIR=${HOME_DIR}/${ROBOT_NAME}

echo "Compilation ..."
JAVA_HOME=~/apps/jdk-11 ./gradlew assemble

echo "Cleaning ..."
ssh ${ROBOT_NAME} rm -vf ${INSTALL_DIR}/*.sh

if [ -d "./${ROBOT_NAME}-parent" ] ; then

  if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot" ] ; then
    echo "Déploiement Applicatif ..."
    scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/build/libs/${ROBOT_NAME}-robot-2021-SNAPSHOT-exec.jar ${ROBOT_NAME}:${INSTALL_DIR}/${ROBOT_NAME}-robot-2021-SNAPSHOT.jar
    if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/scripts" ] ; then
      scp -r ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
    fi
    if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/desktop" ] ; then
      scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/desktop/*.desktop ${ROBOT_NAME}:${DESKTOP_DIR}/
    fi
  fi

  if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils" ] ; then
    echo "Déploiement Utils ..."
    scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/build/libs/${ROBOT_NAME}-utils-2021-SNAPSHOT.jar ${ROBOT_NAME}:${INSTALL_DIR}/
    if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/src/main/scripts" ] ; then
      scp -r ./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
    fi
  fi
fi
