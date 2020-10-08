package com.dd.rsvp.publisher.ci.script

import com.dd.rsvp.publisher.ci.builder.RSVPPublisherCodeDeployBuilder
import javaposse.jobdsl.dsl.JobParent


def factory = this as JobParent
def listOfEnvironment = ["dev", "qa", "prod"]
def component = "rsvp-publisher-code-deploy-job"

def emailId = "vivekmishra22117@gmail.com"
def description = "Pipeline DSL to create CodeDeploy Infra for RSVP Publisher!"
def displayName = "RSVP Publisher CodeDeploy Job"
def branchesName = "*/master"
def githubUrl = "https://github.com/vivek22117/rsvp-stream-data-publisher.git"


new RSVPPublisherCodeDeployBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(0),
        displayName: displayName + " " + listOfEnvironment.get(0),
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(0),
        emailId: emailId
).build()

new RSVPPublisherCodeDeployBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(1),
        displayName: displayName + " " + listOfEnvironment.get(1),
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(1),
        emailId: emailId
).build()

new RSVPPublisherCodeDeployBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(2),
        displayName: displayName + " " + listOfEnvironment.get(2),
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(2),
        emailId: emailId
).build()