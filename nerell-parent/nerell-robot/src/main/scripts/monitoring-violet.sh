#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,monitoring -Dequipe=VIOLET -jar nerell-robot-2019-SNAPSHOT.jar
