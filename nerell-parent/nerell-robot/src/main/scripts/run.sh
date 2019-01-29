#!/bin/sh
./stopAll
sudo java -jar nerell.jar -Dspring.profiles.active=default,ui
