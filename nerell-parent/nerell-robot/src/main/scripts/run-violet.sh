#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -Dequipe=VIOLET -Dstrategies=$1 -jar nerell-robot-2019-SNAPSHOT.jar
