#!/bin/bash
set -e

echo "Compilation ..."
if [[ "$(uname)" == "Darwin" ]] ; then
  JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew build
else
  JAVA_HOME=~/sogelink/softwares/SDK/jdk/jdk-17.0.4.1 ./gradlew build
fi

echo "Run / Debug ..."
DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=n,address=localhost:8787,suspend=y"
sudo ${JAVA_HOME}/bin/java ${DEBUG_ARGS} -jar tinker-parent/tinker-robot/build/libs/tinker-robot-BUILD-SNAPSHOT-exec.jar
