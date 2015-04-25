#!/bin/sh
sudo java -cp "nerell.jar:./libs/*" -Dspring.profiles.active=raspi org.arig.eurobot.RobotApplication
