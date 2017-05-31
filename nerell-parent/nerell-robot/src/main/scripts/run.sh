#!/bin/sh
./stopAll
sudo java -cp "nerell.jar:./libs/*" org.arig.robot.RobotNerell
sudo poweroff