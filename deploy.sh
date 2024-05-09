#!/bin/bash
set -e

ROBOTS=$(
  (
    echo "nerell"
    echo "pami-triangle"
    echo "pami-carre"
    echo "pami-rond"
  ) | fzf -m --prompt="Choisir le nom des robots à déployer (tab pour selectionner)"
)

UTILS=$(
  (
    echo "oui"
    echo "non"
  ) | fzf -m --prompt="Déployer le shell"
)

echo "$(date)"

echo "Compilation ..."
if [[ "$(uname)" == "Darwin" ]] ; then
  JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew assemble --offline
else
  JAVA_HOME=~/sogelink/softwares/SDK/jdk/jdk-17.0.4.1 ./gradlew assemble --offline
fi

echo "Déploiement ..."
for ROBOT_NAME in ${ROBOTS} ; do
  # If ROBOT_NAME contains 'pami' then we are deploying on a PAMI robot
  if [[ ${ROBOT_NAME} == *"pami"* ]] ; then
    HOME_DIR=/home/pi
    PROJECT_NAME=pami
  else
    HOME_DIR=/home/pi
    PROJECT_NAME=${ROBOT_NAME}
  fi
  INSTALL_DIR=${HOME_DIR}/${ROBOT_NAME}
  DESKTOP_DIR=${HOME_DIR}/Desktop

  echo "Déploiement de ${ROBOT_NAME} ..."
  echo "Cleaning ..."
  ssh ${ROBOT_NAME} mkdir -p ${INSTALL_DIR}
  ssh ${ROBOT_NAME} rm -vf ${INSTALL_DIR}/*.sh

  if [ -d "./${PROJECT_NAME}-parent" ] ; then

    if [ -d "./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot" ] ; then
      echo "Déploiement Applicatif ..."
      scp ./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/build/libs/${PROJECT_NAME}-robot-BUILD-SNAPSHOT-exec.jar ${ROBOT_NAME}:${INSTALL_DIR}/${PROJECT_NAME}-robot-BUILD-SNAPSHOT.jar
      if [ -d "./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/src/main/scripts" ] ; then
        scp -r ./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
      fi
      if [ -d "./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/src/main/desktop" ] ; then
        scp ./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/src/main/desktop/*.desktop ${ROBOT_NAME}:${DESKTOP_DIR}/
      fi
    fi

    if [ -d "./${PROJECT_NAME}-parent/${PROJECT_NAME}-utils" ] && [ "${UTILS}" == "oui" ] ; then
      echo "Déploiement Utils ..."
      scp ./${PROJECT_NAME}-parent/${PROJECT_NAME}-utils/build/libs/${PROJECT_NAME}-utils-BUILD-SNAPSHOT.jar ${ROBOT_NAME}:${INSTALL_DIR}/
      if [ -d "./${PROJECT_NAME}-parent/${PROJECT_NAME}-utils/src/main/scripts" ] ; then
        scp -r ./${PROJECT_NAME}-parent/${PROJECT_NAME}-utils/src/main/scripts/*.sh ${ROBOT_NAME}:${INSTALL_DIR}/
      fi
    fi
  fi
  echo "---------------------------"
  echo ""
done
