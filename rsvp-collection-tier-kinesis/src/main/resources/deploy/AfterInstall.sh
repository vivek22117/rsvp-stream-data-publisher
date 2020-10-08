#!/bin/bash

echo 'after install script starting....'

mkdir /opt/rsvp/logs
chmod +rw /opt/rsvp/logs
touch /opt/rsvp/logs/stdout.log
touch /opt/rsvp/logs/stderr.log

chown -R ec2-user:ec2-user /opt/rsvp

mv /opt/rsvp/rsvp-*.jar /opt/rsvp/lib/rsvp-collection-tier-kinesis-0.0.1-webapp.jar

