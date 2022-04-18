#!/bin/bash
set -e

if [[ -z ${1} ]] ; then
    echo "Il faut préciser le nom du robot à lancer"
    exit 1
fi

echo "$(date)"

for ROBOT_NAME in "${@}" ; do
  echo "Lancement de ${ROBOT_NAME} en monitoring"
  ssh ${ROBOT_NAME} touch /tmp/external-dir/monitoring
done
