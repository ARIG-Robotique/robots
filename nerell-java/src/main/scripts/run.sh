#!/bin/sh
sudo rm -f logs/*.log
sudo rm -f logs/paths/*

sudo i2cset -y 1 0x3D 0xF0
sudo java -cp "nerell.jar:./libs/*" org.arig.eurobot.RobotApplication
