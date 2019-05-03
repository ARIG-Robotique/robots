#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,ui -jar nerell-robot-2019-SNAPSHOT.jar
