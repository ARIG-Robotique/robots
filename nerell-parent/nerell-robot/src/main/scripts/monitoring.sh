#!/bin/sh
./stopAll
java -jar nerell-robot-1.1.0-SNAPSHOT.jar -Dspring.profiles.active=default,ui,monitoring
