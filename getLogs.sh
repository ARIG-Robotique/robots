#!/bin/bash
ROBOT_NAME=${1}

# If ROBOT_NAME contains 'pami' then we are getting logs from a PAMI robot
if [[ ${ROBOT_NAME} == *"pami"* ]] ; then
  HOME_DIR=/home/arig
  PROJECT_NAME=pami
  LOG_DIR=./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/logs-${PAMI_ROBOT_NAME}
else
  HOME_DIR=/home/pi
  PROJECT_NAME=${ROBOT_NAME}
  LOG_DIR=./${PROJECT_NAME}-parent/${PROJECT_NAME}-robot/logs
fi

echo "Création répertoire de stockage des logs du robot"
mkdir -p ${LOG_DIR}

echo "Récupération logs ..."
scp -r ${ROBOT_NAME}:${HOME_DIR}/${ROBOT_NAME}/logs/* ${LOG_DIR}

echo "Suppression des logs du robots ..."
ssh ${ROBOT_NAME} sudo rm -Rvf ${HOME_DIR}/${ROBOT_NAME}/logs/*
