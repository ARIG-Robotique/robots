#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,monitoring -jar nerell-robot-2019-SNAPSHOT.jar
