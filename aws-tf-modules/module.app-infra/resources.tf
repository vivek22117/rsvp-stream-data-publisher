########################################################
#    Key pair to be used for SSH access                #
########################################################
resource "tls_private_key" "app_server_ssh_data" {
  algorithm = "RSA"
}

resource "aws_key_pair" "ssh_key" {
  key_name   = "server-ssh-key"
  public_key = tls_private_key.app_server_ssh_data.public_key_openssh

  tags = merge(local.common_tags, map("Name", "${var.component_name}-ssh-key"))
}


#####============adding the zip/jar to the defined bucket=================#####
resource "aws_s3_object" "ec2-app-package" {
  bucket = data.terraform_remote_state.s3_buckets.outputs.artifactory_s3_name
  key    = var.ec2-webapp-bucket-key
  source = "${path.module}/../../rsvp-collection-tier-kinesis/target/rsvp-collection-tier-kinesis-0.0.1-webapp.zip"
  etag   = filemd5("${path.module}/../../rsvp-collection-tier-kinesis/target/rsvp-collection-tier-kinesis-0.0.1-webapp.zip")
}

##################################################################
#      Application launch template helps us to configure EC2      #
#      instance like, AMI Id, Instance Type, Key-Pair, User-     #
#      Data, Instance-Profile, EBS etc.                          #
##################################################################
resource "aws_launch_template" "rsvp_launch_template" {
  name_prefix = "${var.resource_name_prefix}${var.environment}"

  image_id      = data.aws_ami.ec2_server.id
  instance_type = var.instance_type
  key_name      = aws_key_pair.ssh_key.key_name

  user_data = base64encode(data.template_file.ec2_user_data.rendered)

  instance_initiated_shutdown_behavior = "terminate"

  iam_instance_profile {
    arn = aws_iam_instance_profile.rsvp_collection_profile.arn
  }

  instance_market_options {
    market_type = "spot"

    spot_options {
      max_price = var.max_price
    }
  }

  network_interfaces {
    device_index                = 0
    associate_public_ip_address = false
    security_groups             = [aws_security_group.instance_sg.id]
    delete_on_termination       = true
  }

  placement {
    tenancy = var.instance_tenancy
  }

  block_device_mappings {
    device_name = "/dev/xvda"

    ebs {
      volume_size           = var.volume_size
      volume_type           = "gp2"
      delete_on_termination = true
    }
  }

  lifecycle {
    create_before_destroy = true
  }

  tag_specifications {
    resource_type = "instance"
    tags          = merge(local.common_tags, map("Project", "RSVP-Collection-Tier"))
  }
}

resource "aws_alb" "rsvp_lb" {
  name = var.lb_name

  load_balancer_type = var.lb_type
  subnets            = data.terraform_remote_state.vpc.outputs.public_subnets
  internal           = "false"
  security_groups    = [aws_security_group.lb_sg.id]
  enable_http2       = "true"
  idle_timeout       = 600

  tags = merge(local.common_tags, map("Name", "${var.component_name}-lb"))
}

resource "aws_alb_listener" "rsvp_lb_listener_http" {
  load_balancer_arn = aws_alb.rsvp_lb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.rsvp_lb_target_group.arn
  }
}

resource "aws_alb_listener_rule" "listener_rule" {
  depends_on = [aws_alb_target_group.rsvp_lb_target_group]

  listener_arn = aws_alb_listener.rsvp_lb_listener_http.arn
  priority     = "100"

  action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.rsvp_lb_target_group.arn
  }
  condition {
    path_pattern {
      values = ["/"]
    }
  }
}

resource "aws_alb_target_group" "rsvp_lb_target_group" {
  name = "${var.resource_name_prefix}${var.environment}-tg"

  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id
  port        = var.target_group_port
  target_type = var.target_type
  protocol    = "HTTP"

  health_check {
    enabled             = true
    protocol            = "HTTP"
    healthy_threshold   = 5
    unhealthy_threshold = 10
    timeout             = 5
    interval            = 30
    path                = var.target_group_path
    matcher             = "200,301,302"
  }

  tags = merge(local.common_tags, map("Name", "${var.resource_name_prefix}tg"))
}

resource "aws_autoscaling_group" "rsvp_asg" {
  depends_on = [aws_s3_object.ec2-app-package]

  name_prefix         = "rsvp-asg-${var.environment}"
  vpc_zone_identifier = data.terraform_remote_state.vpc.outputs.private_subnets

  launch_template {
    id      = aws_launch_template.rsvp_launch_template.id
    version = aws_launch_template.rsvp_launch_template.latest_version
  }

  target_group_arns = [aws_alb_target_group.rsvp_lb_target_group.arn]

  termination_policies      = var.termination_policies
  max_size                  = var.rsvp_asg_max_size
  min_size                  = var.rsvp_asg_min_size
  desired_capacity          = var.rsvp_asg_desired_capacity
  health_check_grace_period = var.rsvp_asg_health_check_grace_period
  health_check_type         = var.health_check_type
  wait_for_elb_capacity     = var.rsvp_asg_wait_for_elb_capacity
  wait_for_capacity_timeout = var.wait_for_capacity_timeout

  default_cooldown = var.default_cooldown


  lifecycle {
    create_before_destroy = true
  }

  dynamic "tag" {
    for_each = var.custom_tags
    content {
      key                 = tag.key
      value               = tag.value
      propagate_at_launch = true
    }
  }
}

resource "aws_autoscaling_attachment" "attach_rsvp_asg_tg" {
  autoscaling_group_name = aws_autoscaling_group.rsvp_asg.id
  lb_target_group_arn   = aws_alb_target_group.rsvp_lb_target_group.arn
}

resource "aws_autoscaling_policy" "instance_scaling_up_policy" {
  autoscaling_group_name = aws_autoscaling_group.rsvp_asg.name

  name               = "rsvp_asg_scaling_up"
  scaling_adjustment = 1
  adjustment_type    = "ChangeInCapacity"
  cooldown           = 600
}

resource "aws_autoscaling_policy" "instance_scaling_down_policy" {
  autoscaling_group_name = aws_autoscaling_group.rsvp_asg.name

  name               = "rsvp_asg_scaling_down"
  scaling_adjustment = -1
  adjustment_type    = "ChangeInCapacity"
  cooldown           = 600
}
