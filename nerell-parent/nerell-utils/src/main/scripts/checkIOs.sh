#!/bin/sh
sudo i2cset -y 1 0x3D 0xF0
sudo java -cp "nerell-utils.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y org.arig.robot.CheckIOs