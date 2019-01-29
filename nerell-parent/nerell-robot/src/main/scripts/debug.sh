#!/bin/sh
./stopAll
sudo java -jar nerell.jar -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y -Dspring.profiles.active=default,ui,monitoring
