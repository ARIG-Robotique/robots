#!/bin/bash

echo "* Suppression des logs ..."
rm -vf gc.log
echo ""

echo "* Configuration de la JVM"
# JVM Bellsoft
#JAVA_HOME=/opt/bellsoft-jre-9.0.4
#JAVA_HOME=/opt/bellsoft-jre-10.0.2
JAVA_HOME=/opt/bellsoft-jre-11.0.6

# JVM Azul Zulu
#JAVA_HOME=/opt/zulu11.1.8-ca-jdk11-c2-linux_aarch32hf

PATH=${JAVA_HOME}/bin:${PATH}
java -version
JVM_ARGS="-XX:+UseG1GC -XX:MaxGCPauseMillis=10 -Xlog:gc:./gc.log -Xmx256m -Xms256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-exports java.base/jdk.internal.ref=ALL-UNNAMED"
DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8787"

echo "  - JVM Args   : ${JVM_ARGS}"
echo "  - Debug Args : ${DEBUG_ARGS}"
echo ""
