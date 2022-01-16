#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -jar odin-robot-2022-SNAPSHOT.jar
