#!/bin/bash -xe


yum update -y

sudo yum -y install ruby
sudo yum install wget unzip curl git -y

CODEDEPLOY_BIN="/opt/codedeploy-agent/bin/codedeploy-agent"
$CODEDEPLOY_BIN stop
yum erase codedeploy-agent -y

cd /home/ec2-user

wget https://aws-codedeploy-us-east-1.s3.us-east-1.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto

sudo service codedeploy-agent start
sudo service codedeploy-agent status


echo "export Environment=${environment}" >> /etc/environment
echo "export LOG_DIR=/opt/rsvp/logs/" >> /etc/environment

aws deploy create-deployment --application-name ${rsvp_app_name} \
	--s3-location bucket="${rsvp_deploy_bucket}",key="${rsvp_app_key}",bundleType=zip \
	--deployment-group-name ${rsvp_group_name} --region ${aws_region}