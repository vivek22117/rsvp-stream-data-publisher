###########################################################
#             Remote state configuration to fetch         #
#             artifactory bucket                          #
###########################################################
data "terraform_remote_state" "backend" {
  backend = "s3"

  config = {
    profile = "admin"
    bucket  = "${var.s3_bucket_prefix}-${var.environment}-${var.default_region}"
    key     = "state/${var.environment}/backend/terraform.tfstate"
    region  = var.default_region
  }
}