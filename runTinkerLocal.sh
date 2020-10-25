#!/bin/bash
set -e

echo "Compilation ..."
JAVA_HOME=~/apps/jdk-11 ./gradlew build

echo "Run / Debug ..."
DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=n,address=localhost:8787,suspend=y"
sudo ~/apps/jdk-11/bin/java ${DEBUG_ARGS} -jar tinker-parent/tinker-robot/build/libs/tinker-robot-2020-SNAPSHOT-exec.jar
