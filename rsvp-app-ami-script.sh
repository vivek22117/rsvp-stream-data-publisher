#!/usr/bin/env bash


echo ================================== Info =====================================================
echo "The below script will create RSVP App AMI with necessary Software Installed"
echo -e "===============================================================================================\n\n"


echo ============================== Reading AWS Default Profile ========================================
aws configure list --profile default >/dev/null 2>&1
EXIT_CODE=$?

if [ $EXIT_CODE -eq 256 ]; then
    echo "'default' aws profile does not exit, please create!"
    exit 1
else
  echo "'default' aws profile exists! Let's create AMI for RSVP App."
fi


echo -e "\n\n =========================== Fetch AWS Account Id ======================================"

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text --profile default)
if [ -z $AWS_ACCOUNT_ID  ]; then
    echo "Credentials are not valid!"
    exit 1
else
  echo $AWS_ACCOUNT_ID
fi


echo -e "\n\n =========================== Choose Packer Execution Type ==========================="
PS3="Select the packer execution type by inserting number: "

select EXEC_TYPE in create_ami destroy_ami
do
    echo "You have decided to $EXEC_TYPE!"
    break
done


echo -e "\n\n ============================= Choose AWS Region ======================================="

PS3="Select aws region to deploy the AMI by inserting number: "

select AWS_REGION in us-east-1 us-east-2 eu-central-1 eu-west-1 eu-west-2 ap-south-1
do
    echo "You have selected $AWS_REGION to deploy the AMI!"
    break
done


function create_rsvp_app_ami() {

      echo -e "You have decided to create AMI for RSVP App host."

      echo -e "\n\n ====================== Creating RSVP App AMI using Packer ========================="
      echo "Checking whether AMI exists"
      RSVP_APP_AMI_ID=$(aws ec2 describe-images --filters "Name=tag:Name,Values=RSVP-App-Server" --query 'Images[*].ImageId' --region $AWS_REGION --profile default --output text)

      if [ -z $RSVP_APP_AMI_ID ]; then
        echo "Creating AMI named bastion-host-YYYY-MM-DD using packer as it is being used in Terraform script"

        cd packer/app-server
        packer validate bastion-template.json
        packer build -var "aws_profile=default" -var "default_region=$AWS_REGION" app-server-template.json
        cd ../..
      else
        echo "AMI exits with id $RSVP_APP_AMI_ID, now creating VPC resources.."
      fi

  echo -e "============================== Completed ================================================ \n\n"
}


if [ $EXEC_TYPE == 'create_ami' ]; then

  create_rsvp_app_ami

fi



if [ $EXEC_TYPE == 'destroy_ami' ]; then

  echo -e "\n\n ========================= =============================== =============================="
      RSVP_APP_AMI_ID=$(aws ec2 describe-images --filters "Name=tag:Name,Values=RSVP-App-Server" --query 'Images[*].ImageId' --region $AWS_REGION --profile default --output text)

      if [ ! -z $RSVP_APP_AMI_ID ]; then
          aws ec2 deregister-image --image-id $RSVP_APP_AMI_ID --region $AWS_REGION

          AMI_SNAPSHOT=$(aws ec2 describe-snapshots --owner-ids self --filters Name=tag:Name,Values=RSVP-App-Server --query "Snapshots[*].SnapshotId" --output text --region $AWS_REGION)
          for ID in $AMI_SNAPSHOT;
          do
            aws ec2 delete-snapshot --snapshot-id $ID --region $AWS_REGION
            echo ====================== Bastion Host AMI Delete Successfully =============================
          done
      fi

fi
