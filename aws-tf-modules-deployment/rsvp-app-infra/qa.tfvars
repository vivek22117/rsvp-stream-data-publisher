owner_team     = "DoubleDigitTeam"
component_name = "rsvp-processor-app"
default_region = "us-east-1"

resource_name_prefix  = "rsvp-collection-tier-"
app_instance_name     = "rsvp-collection-tier"
ec2-webapp-bucket-key = "ec2/codedeploy/rsvp-collection-tier-kinesis-0.0.1-webapp.zip"

instance_type    = "t3a.small"
key_name         = "rsvp-processor-key"
volume_size      = "30"
max_price        = "0.0075"
instance_tenancy = "default"

rsvp_asg_max_size                  = "3"
rsvp_asg_min_size                  = "1"
rsvp_asg_desired_capacity          = "1"
health_check_type                  = "ELB"
rsvp_asg_health_check_grace_period = "400"
rsvp_asg_wait_for_elb_capacity     = "1"
default_cooldown                   = 300
termination_policies               = ["NewestInstance", "Default"]
suspended_processes                = []
wait_for_capacity_timeout          = "7m"


lb_name           = "rsvp-collection-tier-lb"
target_group_path = "/internal/health"
target_group_port = 9006
health_check_port = 9006
lb_type           = "application"

target_type = "instance"