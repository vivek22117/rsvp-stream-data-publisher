resource "aws_iam_role" "rsvp_collection_role" {
  name = "RSVPCollectionEC2Role"
  path = "/"

  assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Effect": "Allow",
            "Principal": {
               "Service": "ec2.amazonaws.com"
            }
        }
    ]
}
EOF
}

#RSVP ec2 instance policy
resource "aws_iam_policy" "rsvp_collection_policy" {
  name        = "RSVPCollectionEC2Policy"
  description = "Policy to access AWS Resources"
  path        = "/"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
          "kinesis:DescribeStream",
          "kinesis:GetShardIterator",
          "kinesis:GetRecords",
          "kinesis:Put*",
          "kinesis:PutRecords"
      ],
      "Resource": [
        "${data.terraform_remote_state.rsvp_lambda_kinesis.outputs.kinesis_arn}"
      ]
    },
	{
	  "Action": [
	    "s3:DeleteObject",
		"s3:Get*",
		"s3:List*",
		"s3:Put*"
	  ],
	  "Effect": "Allow",
	  "Resource": [
	    "arn:aws:s3:::rsvp-records-${var.environment}/*",
        "arn:aws:s3:::qa-artifactory-*/*",
        "arn:aws:s3:::doubledigit-artifactory-*"
	  ]
	},
    {
      "Action": [
        "codedeploy:Batch*",
        "codedeploy:CreateDeployment",
        "codedeploy:Get*",
        "codedeploy:List*",
        "codedeploy:RegisterApplicationRevision"
            ],
       "Effect": "Allow",
       "Resource": [
          "arn:aws:codedeploy:${var.default_region}:${data.aws_caller_identity.current.account_id}:deploymentgroup:RSVPCollectionTier_APP/RSVPCollectionTier",
          "arn:aws:codedeploy:${var.default_region}:${data.aws_caller_identity.current.account_id}:deploymentconfig:CodeDeployDefault.OneAtATime",
          "arn:aws:codedeploy:${var.default_region}:${data.aws_caller_identity.current.account_id}:application:RSVPCollectionTier_APP"
        ]
    },
        {
            "Effect": "Allow",
            "Action": [
                "ssm:DescribeAssociation",
                "ssm:GetDeployablePatchSnapshotForInstance",
                "ssm:GetDocument",
                "ssm:DescribeDocument",
                "ssm:GetManifest",
                "ssm:GetParameter",
                "ssm:GetParameters",
                "ssm:ListAssociations",
                "ssm:ListInstanceAssociations",
                "ssm:PutInventory",
                "ssm:PutComplianceItems",
                "ssm:PutConfigurePackageResult",
                "ssm:UpdateAssociationStatus",
                "ssm:UpdateInstanceAssociationStatus",
                "ssm:UpdateInstanceInformation"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "ssmmessages:CreateControlChannel",
                "ssmmessages:CreateDataChannel",
                "ssmmessages:OpenControlChannel",
                "ssmmessages:OpenDataChannel"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "ec2messages:AcknowledgeMessage",
                "ec2messages:DeleteMessage",
                "ec2messages:FailMessage",
                "ec2messages:GetEndpoint",
                "ec2messages:GetMessages",
                "ec2messages:SendReply"
            ],
            "Resource": "*"
        }
  ]
}
EOF

}

resource "aws_iam_role_policy_attachment" "ec2_policy_role_attach" {
  policy_arn = aws_iam_policy.rsvp_collection_policy.arn
  role       = aws_iam_role.rsvp_collection_role.name
}

resource "aws_iam_role_policy_attachment" "file_ssm_access_policy" {
  role       = aws_iam_role.rsvp_collection_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "rsvp_collection_profile" {
  name = "RSVPCollectionTierProfile"
  role = aws_iam_role.rsvp_collection_role.name
}

