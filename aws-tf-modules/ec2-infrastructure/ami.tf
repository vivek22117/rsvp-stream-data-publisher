####################################################
#             Bastion host AMI config              #
####################################################
data "aws_ami" "ec2_server" {
  owners      = ["self"]
  most_recent = true

  filter {
    name   = "name"
    values = ["ami-codedeploy-java8"]
  }
}