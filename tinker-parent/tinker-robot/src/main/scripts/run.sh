#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default,raspi -jar tinker-robot-2020-SNAPSHOT.jar
