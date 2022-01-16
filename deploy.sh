#!/bin/bash
set -e

if [[ -z ${1} ]] ; then
    echo "Il faut préciser le nom du robot à déployer"
    exit 1
fi

echo "$(date)"

echo "Compilation ..."
JAVA_HOME=~/apps/jdk-11 ./gradlew assemble --offline

HOME_DIR=/home/pi
DESKTOP_DIR=${HOME_DIR}/Desktop

for ROBOT_NAME in "${@}" ; do
  INSTALL_DIR=${HOME_DIR}/${ROBOT_NAME}

  echo "Déploiement de ${ROBOT_NAME} ..."
  echo "Cleaning ..."
  ssh ${ROBOT_NAME} rm -vf ${INSTALL_DIR}/*.sh

  if [ -d "./${ROBOT_NAME}-parent" ] ; then

    if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot" ] ; then
      echo "Déploiement Applicatif ..."
      scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/build/libs/${ROBOT_NAME}-robot-2022-SNAPSHOT-exec.jar ${ROBOT_NAME}:${INSTALL_DIR}/${ROBOT_NAME}-robot-2022-SNAPSHOT.jar
      if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/scripts" ] ; then
        scp -r ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
      fi
      if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/desktop" ] ; then
        scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-robot/src/main/desktop/*.desktop ${ROBOT_NAME}:${DESKTOP_DIR}/
      fi
    fi

    if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils" ] ; then
      echo "Déploiement Utils ..."
      scp ./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/build/libs/${ROBOT_NAME}-utils-2022-SNAPSHOT.jar ${ROBOT_NAME}:${INSTALL_DIR}/
      if [ -d "./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/src/main/scripts" ] ; then
        scp -r ./${ROBOT_NAME}-parent/${ROBOT_NAME}-utils/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
      fi
    fi
  fi
  echo "---------------------------"
  echo ""
done
