#!/bin/sh
./stopAll
sudo java -cp "nerell.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y -Dspring.profiles.active=monitoring org.arig.robot.RobotNerell
