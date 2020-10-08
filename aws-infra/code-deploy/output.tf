output "rsvp_app_group_name" {
  value = aws_codedeploy_deployment_group.rsvp_code_deploy_group.deployment_group_name
}

output "rsvp_app_name" {
  value = aws_codedeploy_app.rsvp_code_deploy_app.name
}

output "deployment_group_id" {
  value = aws_codedeploy_deployment_group.rsvp_code_deploy_group.id
}