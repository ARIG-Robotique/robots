#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -jar nerell-robot-BUILD-SNAPSHOT.jar
