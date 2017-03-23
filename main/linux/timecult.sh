#!/bin/bash
#
# TimeCult startup script.
# Author: Rustam Vishnyakov <dyadix@gmail.com>
#
# Software requirements:
# - openjdk 7 (or Oracle jdk1.7.x)
# - java-wrappers:
#	sudo apt-get install java-wrappers
#
DESKTOP_FILE=~/.local/share/applications/timecult.desktop
VERSION=1.6
grep "Version=${VERSION}" ${DESKTOP_FILE} > /dev/null
if [ $? -ne 0 ]
then
    echo Creating TimeCult desktop entry...
    mkdir -p ~/.local/share/applications
    pushd `dirname $0` > /dev/null
    SCRIPT_PATH=`pwd`
    popd > /dev/null
    echo [Desktop Entry] > ${DESKTOP_FILE}
    echo Version=${VERSION} >>  ${DESKTOP_FILE}
    echo Type=Application >> ${DESKTOP_FILE}
    echo Terminal=false >> ${DESKTOP_FILE}
    echo Exec=${SCRIPT_PATH}/timecult.sh >> ${DESKTOP_FILE}
    echo Icon=${SCRIPT_PATH}/timecult.ico >> ${DESKTOP_FILE}
    echo Name=Timecult >>  ${DESKTOP_FILE}
    echo Comment=Track time and manage your tasks >> ${DESKTOP_FILE}
    echo Categories=Office\;Productivity\; >> ${DESKTOP_FILE}
    echo Done
fi
. /usr/lib/java-wrappers/java-wrappers.sh
find_java_runtime java7
find_jars ${0%.*}.jar
run_java net.sf.timecult.TimeTracker &

