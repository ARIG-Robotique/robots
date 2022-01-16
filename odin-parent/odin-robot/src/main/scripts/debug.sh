#!/bin/bash
. common.sh
java ${JVM_ARGS} ${DEBUG_ARGS} -Dspring.profiles.active=default,monitoring -jar odin-robot-2022-SNAPSHOT.jar
