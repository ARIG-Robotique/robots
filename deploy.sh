#!/bin/sh

scp main-robot/build/libs/main-robot*-SNAPSHOT.jar 192.168.1.31:/home/pi/prehistobot/
scp main-robot/build/dependencies/*.jar 192.168.1.31:/home/pi/prehistobot/libs/
