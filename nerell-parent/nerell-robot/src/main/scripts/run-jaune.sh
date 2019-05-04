#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,ui -Dequipe=JAUNE jar nerell-robot-2019-SNAPSHOT.jar
