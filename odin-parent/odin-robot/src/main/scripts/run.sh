#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default -jar odin-robot-BUILD-SNAPSHOT.jar
