#!/bin/bash
set -e

ROBOTS=$(
  (
    echo "nerell"
    echo "pami-triangle"
    echo "pami-carre"
    echo "pami-rond"
  ) | fzf -m --prompt="Choisir le nom des robots (tab pour selectionner)"
)

ACTION=$(
  (
    echo "run"
    echo "monitoring"
    echo "poweroff"
    echo "reboot"
    echo "exit"
  ) | fzf --prompt="Action a executer"
)

echo "$(date)"

for ROBOT in ${ROBOTS} ; do
  echo "Lancement de ${ROBOT} en ${ACTION}"
  if [ ${ACTION} == "poweroff" ] || [ ${ACTION} == "reboot" ] ; then
    ssh "${ROBOT}.local" sudo "${ACTION}" || true
  else
    ssh "${ROBOT}.local" touch "/tmp/external-dir/${ACTION}"
  fi
done
