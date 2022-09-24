####################################################
#        CodeDeploy Module Implementation          #
####################################################
module "code_deploy_resources" {
  source = "../../aws-tf-modules/module.code-deploy"

  default_region = var.default_region


  app_instance_name = var.app_instance_name
  component_name    = var.component_name

  environment = var.environment
  owner_team  = var.owner_team
}