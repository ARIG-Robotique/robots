#!/bin/sh
sudo rm -f logs/*.log
sudo rm -f logs/paths/*

sudo i2cset -y 1 0x3D 0xF0

# -Dlog4j.configuration=file:./log4j-debug.xml
sudo java -cp "nerell.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y -Dspring.profiles.active=monitoring org.arig.robot.RobotApplication
