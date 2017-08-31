#!/bin/sh
./stopAll
sudo java -cp "nerell-utils.jar:./libs/*" org.arig.robot.ReglagePIDMoteurs
