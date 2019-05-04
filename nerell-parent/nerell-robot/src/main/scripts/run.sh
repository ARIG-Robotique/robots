#!/bin/sh
./stopAll
java -Dspring.profiles.active=default -jar nerell-robot-2019-SNAPSHOT.jar
