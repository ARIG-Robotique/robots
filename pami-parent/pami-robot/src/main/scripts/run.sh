#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -jar pami-robot-BUILD-SNAPSHOT.jar
