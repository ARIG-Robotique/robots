#!/bin/bash
#
# Script de lancement pour l'application

# Parametre supplementaires pour la JVM
JVM_OPTS="-Xms64m -Xmx64m -XX:MaxPermSize=16m -Xss512k"
#JVM_OPTS="$JVM_OPTS -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
JVM_OPTS="$JVM_OPTS -Dlog4j.configuration=file:./log4j.xml"
JVM_OPTS="$JVM_OPTS -Dconfig.data.directory=file:./conf"
JVM_OPTS="$JVM_OPTS -Djava.security.egd=file:/dev/./urandom"
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/"

PID_FILE="${project.artifactId}-${project.version}.pid"

if [ ! -f $PID_FILE ]; then
    PID=`echo "$$"`
    echo $PID > $PID_FILE
    java $JVM_OPTS -Dspring.profiles.active=raspi -DlogFileName=app-`date +"%Y%m%y-%T"` -cp "./libs/*" org.arig.prehistobot.MainRobot
    rm -f $PID_FILE
else
	echo "L'application est deja en cours d'execution !"
fi