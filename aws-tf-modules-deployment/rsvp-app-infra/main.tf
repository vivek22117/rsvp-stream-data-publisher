####################################################
#        RSVP App Module Implementation            #
####################################################
module "app_server" {
  source = "../../aws-tf-modules/module.app-infra"

  default_region = var.default_region


  app_instance_name = var.app_instance_name
  component_name = var.component_name

  environment = var.environment
  owner_team = var.owner_team

  default_cooldown = var.default_region
  ec2-webapp-bucket-key = var.ec2-webapp-bucket-key
  health_check_port = var.health_check_port
  health_check_type = var.health_check_type
  key_name = var.key_name

  lb_name = var.lb_name
  lb_type = var.lb_type

  instance_tenancy = var.instance_tenancy
  instance_type = var.instance_type
  max_price = var.max_price
  resource_name_prefix = var.resource_name_prefix
  target_group_path = var.target_group_path
  target_group_port = var.target_group_port
  target_type = var.target_type
  volume_size = var.volume_size

  rsvp_asg_desired_capacity = var.rsvp_asg_desired_capacity
  rsvp_asg_health_check_grace_period = var.rsvp_asg_health_check_grace_period
  rsvp_asg_max_size = var.rsvp_asg_max_size
  rsvp_asg_min_size = var.rsvp_asg_min_size
  rsvp_asg_wait_for_elb_capacity = var.rsvp_asg_wait_for_elb_capacity
  suspended_processes = var.suspended_processes
  termination_policies = var.termination_policies
  wait_for_capacity_timeout = var.wait_for_capacity_timeout
}