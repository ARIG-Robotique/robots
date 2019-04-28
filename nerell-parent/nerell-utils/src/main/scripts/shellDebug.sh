#!/bin/sh
./stopAll
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8787,suspend=y -jar nerell-utils-1.1.0-SNAPSHOT.jar
