#############################################
#      Security Group for RSVP EC2          #
#############################################
resource "aws_security_group" "instance_sg" {
  name = "rsvp-sg"

  description = "Allow traffic from port elb and enable SSH"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  lifecycle {
    create_before_destroy = true
  }
  tags = merge(local.common_tags, map("Name", "rsvp-ct-sg"))
}

resource "aws_security_group_rule" "allow_traffic_from_lb" {
  type                     = "ingress"
  from_port                = var.target_group_port
  to_port                  = var.target_group_port
  protocol                 = "tcp"
  security_group_id        = aws_security_group.instance_sg.id
  source_security_group_id = aws_security_group.lb_sg.id
}

resource "aws_security_group_rule" "allow_ssh_traffic" {
  type                     = "ingress"
  from_port                = 22
  to_port                  = 22
  protocol                 = "tcp"
  security_group_id        = aws_security_group.instance_sg.id
  source_security_group_id = data.terraform_remote_state.vpc.outputs.bastion_sg
}

resource "aws_security_group_rule" "master_outbound_rule" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  security_group_id = aws_security_group.instance_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}


##########################################################
#      Security Group for Elastic Load Balancer          #
##########################################################
resource "aws_security_group" "lb_sg" {
  name        = "rsvp-lb-sg"
  description = "load balancer security group"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  tags = merge(local.common_tags, map("Name", "rsvp-ct-lb-sg"))
}

resource "aws_security_group_rule" "allow_http" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  security_group_id = aws_security_group.lb_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow_outbound_traffic_lb" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.lb_sg.id
}

