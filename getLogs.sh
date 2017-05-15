#!/bin/sh
LOG_DIR=./$1-parent/$1-robot/logs

echo "Création répertoire de stockage des logs du robot"
mkdir -p $LOG_DIR

echo "Récupération logs ..."
scp -r $1:/home/pi/$1/logs/* $LOG_DIR

echo "Suppression des logs du robots ..."
ssh $1 sudo rm -Rvf /home/pi/$1/logs/*
