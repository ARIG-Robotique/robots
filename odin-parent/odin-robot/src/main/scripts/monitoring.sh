#!/bin/bash
. common.sh
java ${JVM_ARGS} -Dspring.profiles.active=default,monitoring -jar odin-robot-2021-SNAPSHOT.jar