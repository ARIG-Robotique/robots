#!/bin/bash

echo "* Suppression des logs ..."
rm -vf logs/0-traces.log
echo ""

echo "* Suppression de la socket Lidar"
sudo rm -vf /tmp/lidar.sock
echo ""

echo "* Configuration de la JVM"
JAVA_HOME=/opt/zulu8.36.0.152-ca-jdk1.8.0_202-linux_aarch32hf
PATH=${JAVA_HOME}/bin:${PATH}
java -version
# -XX:MaxGCPauseMillis=5
JVM_ARGS="-Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m"
DEBUG_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y"
echo "  - JVM Args   : ${JVM_ARGS}"
echo "  - Debug Args : ${DEBUG_ARGS}"
echo ""
