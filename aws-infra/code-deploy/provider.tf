provider "aws" {
  region  = var.default_region                         // Interpolation Syntax
  profile = var.profile

  version = ">=2.22"                                    // AWS plugin version
}


######################################################
# Terraform configuration block is used to define backend
# Interpolation sytanx is not allowed in Backend
######################################################
terraform {
  required_version = ">= 0.12"                             // Terraform version

  backend "s3" {
    profile        = "admin"
    bucket         = "doubledigit-tfstate-qa-us-east-1"
    dynamodb_table = "doubledigit-tfstate-qa-us-east-1"
    key            = "state/qa/rsvp-collection-tier/code-deploy/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = "true"
  }
}

