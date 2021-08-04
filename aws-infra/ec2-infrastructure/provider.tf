####################################################
# AWS provider configuration                       #
####################################################
provider "aws" {
  region = var.default_region

  version = ">=2.28.0"
}


###########################################################
# Terraform configuration block is used to define backend #
# Interpolation syntax is not allowed in Backend          #
###########################################################
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">=3.3.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 1.3"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 2.0"
    }
    template = {
      source  = "hashicorp/template"
      version = "~> 2.0"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 3.0"
    }
  }
  required_version = ">= 0.13"


  backend "s3" {
    region  = "us-east-1"
    encrypt = "true"
  }

}

data "aws_caller_identity" "current" {} # used for accessing Account ID and ARN
