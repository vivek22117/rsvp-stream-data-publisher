###########################################################
#             Remote state configuration to fetch         #
#             vpc, rsvp-lambda, artifactory bucket        #
###########################################################
data "terraform_remote_state" "vpc" {
  backend = "s3"

  config = {
    bucket  = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key     = "state/${var.environment}/vpc/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "rsvp_lambda_kinesis" {
  backend = "s3"

  config = {
    bucket  = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key     = "state/${var.environment}/lambda/rsvp-lambda-fixed-resources/terraform.tfstate"
    region  = var.default_region
  }
}


data "terraform_remote_state" "s3_buckets" {
  backend = "s3"

  config = {
    bucket  = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key     = "state/${var.environment}/s3-buckets/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "code_deploy_backend" {
  backend = "s3"

  config = {
    bucket  = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key     = "state/${var.environment}/rsvp-collection-tier/code-deploy/terraform.tfstate"
    region  = var.default_region
  }
}

############################################################
#           Read user-data script to configure             #
#           ec2 instance within launch template            #
############################################################
data "template_file" "ec2_user_data" {
  template = file("${path.module}/script/user-data.sh")

  vars = {
    aws_region         = var.default_region
    environment        = var.environment
    rsvp_deploy_bucket = data.terraform_remote_state.s3_buckets.outputs.artifactory_s3_name
    rsvp_app_key       = var.ec2-webapp-bucket-key
    rsvp_group_name    = data.terraform_remote_state.code_deploy_backend.outputs.rsvp_app_group_name
    rsvp_app_name      = data.terraform_remote_state.code_deploy_backend.outputs.rsvp_app_name
  }
}
