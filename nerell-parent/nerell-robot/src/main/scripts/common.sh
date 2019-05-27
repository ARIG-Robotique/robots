#!/bin/bash

echo "* Suppression des logs ..."
rm -vf logs/0-traces.log
echo ""

echo "* Suppression de la socket Lidar"
sudo rm -vf /tmp/lidar.sock
echo ""

echo "* Configuration de la JVM"
#JAVA_HOME=/opt/zulu8.36.0.152-ca-jdk1.8.0_202-linux_aarch32hf
JAVA_HOME=/opt/bellsoft-jre-9.0.4
PATH=${JAVA_HOME}/bin:${PATH}
java -version
JVM_ARGS="-XX:+UseG1GC -XX:MaxGCPauseMillis=5 -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-exports java.base/jdk.internal.ref=ALL-UNNAMED"
DEBUG_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y"

echo "  - JVM Args   : ${JVM_ARGS}"
echo "  - Debug Args : ${DEBUG_ARGS}"
echo ""
