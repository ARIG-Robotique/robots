#!/bin/bash
. common.sh
java ${JVM_ARGS} ${DEBUG_ARGS} -Dspring.profiles.active=default,monitoring -Dequipe=VIOLET -Dstrategies=$1 -jar nerell-robot-2019-SNAPSHOT.jar
