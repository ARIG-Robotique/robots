#!/bin/bash
set -e

if [[ -z ${1} ]] ; then
    echo "Il faut préciser le nom du robot à reboot"
    exit 1
fi

echo "$(date)"

for ROBOT_NAME in "${@}" ; do
  echo "Reboot ${ROBOT_NAME} !!!"
  ssh ${ROBOT_NAME} sudo reboot
done
