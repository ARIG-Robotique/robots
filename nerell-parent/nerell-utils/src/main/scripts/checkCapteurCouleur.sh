#!/bin/sh
sudo java -cp "nerell-utils.jar:./libs/*" -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y org.arig.robot.CheckCapteurCouleur