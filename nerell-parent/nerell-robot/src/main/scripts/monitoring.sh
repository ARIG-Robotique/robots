#!/bin/bash
. common.sh
java ${JVM_ARGS} -enableassertions:org.arig.robot... -Dspring.profiles.active=default,monitoring -jar nerell-robot-BUILD-SNAPSHOT.jar
