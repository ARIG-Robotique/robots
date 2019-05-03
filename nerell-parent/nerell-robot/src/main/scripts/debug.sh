#!/bin/sh
./stopAll
java -Dspring.profiles.active=default,ui,monitoring -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y -jar nerell-robot-2019-SNAPSHOT.jar
