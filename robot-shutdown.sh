#!/bin/bash
set -e

if [[ -z ${1} ]] ; then
    echo "Il faut préciser le nom du robot à éteindre"
    exit 1
fi

echo "$(date)"

for ROBOT_NAME in "${@}" ; do
  echo "${ROBOT_NAME} en POWEROFF !!!"
  ssh ${ROBOT_NAME} sudo poweroff
done
