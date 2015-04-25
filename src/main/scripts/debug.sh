#!/bin/sh
sudo java -cp "nerell.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y -Dlog4j.configuration=file:./log4j-debug.xml" -Dspring.profiles.active=raspi org.arig.eurobot.RobotApplication
