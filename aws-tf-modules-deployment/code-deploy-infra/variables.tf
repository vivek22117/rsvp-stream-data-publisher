#####==============Global Variables=====================#####
variable "environment" {
  type        = string
  description = "Environment to be configured 'dev', 'qa', 'prod'"
}

variable "owner_team" {
  type        = string
  description = "Name of owner team"
}

variable "component_name" {
  type        = string
  description = "Component name for resources"
}


#####===================Default Variables==============#####
variable "default_region" {
  type    = string
  default = "us-east-1"
}


variable "app_instance_name" {
  type        = string
  description = "Instance name tag to propagate"
}

locals {
  common_tags = {
    owner       = "Vivek"
    team        = var.owner_team
    environment = var.environment
  }
}
