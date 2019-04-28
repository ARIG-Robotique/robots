#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,ui -jar nerell-robot-1.1.0-SNAPSHOT.jar
