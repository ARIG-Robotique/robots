#!/bin/bash
. common.sh
java ${JVM_ARGS} ${DEBUG_ARGS} -Dspring.profiles.active=default,monitoring -jar nerell-robot-BUILD-SNAPSHOT.jar
