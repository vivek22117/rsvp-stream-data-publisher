#####====================Global Variables===================#####
variable "profile" {
  type        = string
  description = "AWS Profile name for credentials"
}

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

#####======================Application Variables=================#####
variable "ec2-webapp-bucket-key" {
  type        = string
  description = "Location of S3 key which holds the deployable zip file"
}


variable "rsvp_asg_max_size" {
  type        = string
  description = "ASG max size"
}

variable "rsvp_asg_min_size" {
  type        = string
  description = "ASG min size"
}

variable "rsvp_asg_desired_capacity" {
  type        = string
  description = "ASG desired capacity"
}

variable "rsvp_asg_health_check_grace_period" {
  type        = string
  description = "ASG health check grace period"
}

variable "health_check_type" {
  type        = string
  description = "ASG health check type"
}

variable "rsvp_asg_wait_for_elb_capacity" {
  type        = string
  description = "ASG wait for ELB capacity"
}

variable "default_cooldown" {
  type        = number
  description = "Cool-down value of ASG"
}

variable "termination_policies" {
  description = "A list of policies to decide how the instances in the auto scale group should be terminated"
  type        = list(string)
}

variable "suspended_processes" {
  description = "The allowed values are Launch, Terminate, HealthCheck, ReplaceUnhealthy, AZRebalance, AlarmNotification, ScheduledActions, AddToLoadBalancer"
  type        = list(string)
}

variable "wait_for_capacity_timeout" {
  description = "A maximum duration that Terraform should wait for ASG instances to be healthy before timing out"
  type        = string
}

variable "app_instance_name" {
  type        = string
  description = "Instance name tag to propagate"
}


variable "resource_name_prefix" {
  type        = string
  description = "Application resource name prefix"
}

variable "ami_id" {
  type        = string
  description = "AMI id to create EC2"
}

variable "instance_type" {
  type        = string
  description = "Instance type to launc EC2"
}

variable "key_name" {
  type        = string
  description = "Key pair to use SSh access"
}

variable "volume_size" {
  type        = string
  description = "Volume size"
}

variable "max_price" {
  type        = string
  description = "Spot price for EC2 instance"
}

variable "instance_tenancy" {
  type        = string
  description = "Type of EC2 instance tenancy 'default' or 'dedicated'"
}

variable "target_type" {
  type        = string
  description = "Target group instance type 'ip', 'instance', 'lambda'"
}

#####=============ALB Variables===================#####
variable "lb_name" {
  type        = string
  description = "Name of the load balancer"
}

variable "lb_type" {
  type        = string
  description = "Type of load balance to be configure 'application' or 'network'"
}

variable "target_group_path" {
  type        = string
  description = "Health check path"
}

variable "target_group_port" {
  type        = number
  description = "The port on which targets receive traffic"
}

variable "health_check_port" {
  type        = number
  description = "The port to use to connect with the target for health check"
}


#####=============ASG Standards Tags===============#####
variable "custom_tags" {
  description = "Custom tags to set on the Instances in the ASG"
  type        = map(string)
  default = {
    owner      = "Vivek"
    team       = "LearningTF"
    tool       = "Terraform"
    monitoring = "true"
    Name       = "rsvp-collection-tier"
    Project    = "RSVP-Collection-Tier"
  }
}

#####=============Default Variables=============#####
variable "default_region" {
  type        = string
  description = "AWS region to deploy infra and application"
}


#####=============Local variables===============#####
locals {
  common_tags = {
    owner       = "Vivek"
    team        = var.owner_team
    environment = var.environment
  }
}