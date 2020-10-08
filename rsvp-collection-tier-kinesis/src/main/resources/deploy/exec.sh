#!/usr/bin/env bash

source /etc/environment

DAEMON="java"
NAME="rsvp"
RSVP_LOCATION=/opt/${NAME}
MAIN_CLASS=com.ddsolutions.kinesis.RSVPCollectionAppKinesis
LOG_FOLDER=${RSVP_LOCATION}/logs
PID_FILE=${LOG_FOLDER}/${NAME}.pid
#CONFIG_FOLDER="-Dspring.config.location=${RSVP_LOCATION}/config/application-${Environment}.properties"
DAEMONOPTS="-jar -Dspring.profiles.active=${Environment} /opt/${NAME}/lib/rsvp-collection-tier-kinesis-0.0.1-webapp.jar"


JAVAOPTS=""
if [[ "${Environment}" == "qa" ]]; then
  JAVAOPTS="-Xms512m -Xmx1024m"
elif [[ "${Environment}" == "prod" ]]; then
  JAVAOPTS="-Xms1024m -Xmx2048m"
else
  JAVAOPTS="-Xms512m -Xmx1024m"
fi


case "$1" in
start)
        rm -f ${LOG_FOLDER}/sysout.log
        printf "%-50s" "Starting $NAME..." >> ${LOG_FOLDER}/start.log
        PID=`${DAEMON} ${JAVAOPTS} ${DAEMONOPTS} > ${LOG_FOLDER}/sysout.log 2>&1 & echo $!`
        if [[ -z ${PID} ]]; then
            printf "%s\n" "Fail" >> ${LOG_FOLDER}/start.log
            exit 1
        else
            echo ${PID} >> ${LOG_FOLDER}/start.log
            echo ${PID} > ${PID_FILE}
            printf "%s\n" "Ok" >> ${LOG_FOLDER}/start.log
            exit 0
        fi
;;
status)
        printf "%-50s" "Checking $NAME..." ${LOG_FOLDER}/status.log
        if ! /usr/sbin/lsof -i:2020
        then
            echo "`date` - 2020 is free" >> ${LOG_FOLDER}status.log
        else
            echo "`date` - 2020 is occupied" >> ${LOG_FOLDER}status.log
        fi
;;
stop)
        printf "%-50s" "Stopping $NAME..." >> ${LOG_FOLDER}/status.log
            PID=`cat ${PID_FILE}`
        if [[ -f ${PID_FILE} ]]; then
            kill ${PID}
            printf "%s\n" "Ok"
            rm -f ${PID_FILE}
        else
            printf "%s\n" "pidfile not found"
        fi
;;
restart)
        $0 stop
        $0 start
;;

*)
        echo "Usage: $0 {status|start|stop|restart}"
        exit 1
esac