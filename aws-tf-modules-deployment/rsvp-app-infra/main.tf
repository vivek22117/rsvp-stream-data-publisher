####################################################
#        RSVP App Module Implementation            #
####################################################
module "code_deploy_resources" {
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
  instance_tenancy = var.instance_tenancy
  instance_type = var.instance_type
  key_name = var.key_name
  lb_name = var.lb_name
  lb_type = var.lb_type
  max_price = var.max_price
  resource_name_prefix = var.resource_name_prefix
  rsvp_asg_desired_capacity = var.rsvp_asg_desired_capacity
  rsvp_asg_health_check_grace_period = ""
  rsvp_asg_max_size = ""
  rsvp_asg_min_size = ""
  rsvp_asg_wait_for_elb_capacity = ""
  suspended_processes = []
  target_group_path = ""
  target_group_port = 0
  target_type = ""
  termination_policies = []
  volume_size = ""
  wait_for_capacity_timeout = ""
}