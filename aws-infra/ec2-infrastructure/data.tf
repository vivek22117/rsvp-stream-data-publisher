###########################################################
#             Remote state configuration to fetch         #
#             vpc, rsvp-lambda, artifactory bucket        #
###########################################################
data "terraform_remote_state" "vpc" {
  backend = "s3"

  config = {
    profile = "admin"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/vpc/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "rsvp_lambda_kinesis" {
  backend = "s3"

  config = {
    profile = "admin"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/lambda/rsvp-lambda-fixed-resources/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "backend" {
  backend = "s3"

  config = {
    profile = "admin"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/backend/terraform.tfstate"
    region  = var.default_region
  }
}

data "terraform_remote_state" "code_deploy_backend" {
  backend = "s3"

  config = {
    profile = "admin"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
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
    rsvp_deploy_bucket = data.terraform_remote_state.backend.outputs.artifactory_bucket_name
    rsvp_app_key       = var.ec2-webapp-bucket-key
    rsvp_group_name    = data.terraform_remote_state.code_deploy_backend.outputs.rsvp_app_group_name
    rsvp_app_name      = data.terraform_remote_state.code_deploy_backend.outputs.rsvp_app_name
  }
}