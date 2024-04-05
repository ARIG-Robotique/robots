#!/bin/bash
. common.sh
java ${JVM_ARGS} -enableassertions:org.arig.robot... -Dspring.profiles.active=default,monitoring -jar pami-robot-BUILD-SNAPSHOT.jar
