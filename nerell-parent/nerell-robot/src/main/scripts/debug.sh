#!/bin/sh
sudo i2cset -y 1 0x3D 0xF0
sudo java -cp "nerell.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y -Dspring.profiles.active=monitoring org.arig.robot.RobotApplication
