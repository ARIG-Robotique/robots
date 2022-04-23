#!/bin/bash

echo "* Suppression des logs ..."
rm -vf gc.log
echo ""

echo "* Suppression de la socket Lidar"
sudo rm -vf /tmp/lidar.sock
echo ""

echo "* Suppression de la socket Ecran"
sudo rm -vf /tmp/ecran.sock
echo ""

echo "* Configuration de la JVM"
#JAVA_HOME=/opt/jre-11.0.14
#JAVA_HOME=/opt/jre-13.0.2
#JAVA_HOME=/opt/jre-17.0.3
JAVA_HOME=/opt/jre-18.0.1
PATH=${JAVA_HOME}/bin:${PATH}
java -version
JVM_ARGS="-XX:+UseZGC -XX:MaxGCPauseMillis=10 -Xlog:gc:./gc.log -Xmx1024m -Xms1024m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-exports java.base/jdk.internal.ref=ALL-UNNAMED"
DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8787"

echo "  - JVM Args   : ${JVM_ARGS}"
echo "  - Debug Args : ${DEBUG_ARGS}"
echo ""
