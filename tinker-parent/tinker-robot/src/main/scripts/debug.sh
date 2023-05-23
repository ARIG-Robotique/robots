#!/bin/bash
. common.sh
sudo ${JAVA_HOME}/bin/java ${JVM_ARGS} ${DEBUG_ARGS} -Dspring.profiles.active=default,raspi -jar tinker-robot-BUILD-SNAPSHOT.jar
