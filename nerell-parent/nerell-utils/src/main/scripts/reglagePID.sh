#!/bin/sh
#sudo i2cset -y 1 0x3D 0xF0
sudo java -cp "nerell-utils.jar:./libs/*" org.arig.robot.ReglagePIDMoteurs
