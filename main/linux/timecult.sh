#!/bin/bash
#
# TimeCult startup script.
# Author: Rustam Vishnyakov <dyadix@gmail.com>
#
# Software requirements:
# - openjdk 6 (or Oracle jdk1.6.x)
# - java-wrappers:
#	sudo apt-get install java-wrappers
# - swt libraries:
#	sudo apt-get install libswt-gtk-3-jni libswt-gtk-3-java
#
#
. /usr/lib/java-wrappers/java-wrappers.sh
find_java_runtime java6
find_jars ${0%.*}.jar /usr/lib/java/swt-gtk-*.jar
run_java net.sf.timecult.TimeTracker &

