#!/bin/sh
sudo rm -f logs/*.log
sudo rm -f logs/paths/*
sudo rm -f logs/influx/*

sudo i2cset -y 1 0x3D 0xF0
sudo java -cp "nerell.jar:./libs/*" -Dspring.profiles.active=monitoring org.arig.robot.RobotApplication