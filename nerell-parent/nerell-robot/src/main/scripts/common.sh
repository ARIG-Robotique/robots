#!/bin/bash

echo "* Suppression des logs ..."
rm -vf logs/0-traces.log
rm -vf gc.log
echo ""

echo "* Suppression de la socket Lidar"
sudo rm -vf /tmp/lidar.sock
echo ""

echo "* Configuration de la JVM"
#JAVA_HOME=/opt/bellsoft-jre-9.0.4
JAVA_HOME=/opt/zulu11.1.8-ca-jdk11-c2-linux_aarch32hf
PATH=${JAVA_HOME}/bin:${PATH}
java -version
JVM_ARGS="-XX:+UseG1GC -XX:MaxGCPauseMillis=10 -Xlog:gc:./gc.log -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-exports java.base/jdk.internal.ref=ALL-UNNAMED"
DEBUG_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y"

echo "  - JVM Args   : ${JVM_ARGS}"
echo "  - Debug Args : ${DEBUG_ARGS}"
echo ""
