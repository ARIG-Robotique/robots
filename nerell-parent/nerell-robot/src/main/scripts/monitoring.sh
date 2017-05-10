#!/bin/sh
sudo java -cp "nerell.jar:./libs/*" -Dspring.profiles.active=monitoring org.arig.robot.RobotNerell
