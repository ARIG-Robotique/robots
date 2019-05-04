#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,monitoring -Dequipe=JAUNE -jar nerell-robot-2019-SNAPSHOT.jar
