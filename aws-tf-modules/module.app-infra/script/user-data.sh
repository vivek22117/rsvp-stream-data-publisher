#!/bin/bash -xe


yum update -y

sudo yum -y install ruby
sudo yum install wget unzip curl git -y

echo "Install AWS Cli"
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo  ./aws/install -i /usr/local/aws-cli -b /usr/local/bin

wget -O /tmp/install-codedeploy-agent https://aws-codedeploy-us-east-1.s3.amazonaws.com/latest/install
chmod +x /tmp/install-codedeploy-agent
sudo /tmp/install-codedeploy-agent auto
rm /tmp/install-codedeploy-agent

sudo service codedeploy-agent start

echo "export Environment=${environment}" >> /etc/environment
echo "export LOG_DIR=/opt/rsvp/logs/" >> /etc/environment

aws deploy create-deployment --application-name ${rsvp_app_name} \
	--s3-location bucket="${rsvp_deploy_bucket}",key="${rsvp_app_key}",bundleType=zip \
	--deployment-group-name ${rsvp_group_name} --region ${aws_region}