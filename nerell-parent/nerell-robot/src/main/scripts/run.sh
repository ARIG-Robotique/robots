#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -jar nerell-robot-2021-SNAPSHOT.jar
