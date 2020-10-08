package com.dd.rsvp.publisher.ci.script

import com.dd.rsvp.publisher.ci.builder.RSVPPublisherJobBuilder
import javaposse.jobdsl.dsl.JobParent


def factory = this as JobParent
def listOfEnvironment = ["dev", "qa", "prod"]
def component = "rsvp-publisher-job"

def emailId = "vivekmishra22117@gmail.com"
def description = "Pipeline DSL to create build for RSVP publisher SpringBoot application & AWS Infra!"
def displayName = "RSVP Publisher Job"
def branchesName = "*/master"
def githubUrl = "https://github.com/vivek22117/rsvp-stream-data-publisher.git"


new RSVPPublisherJobBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(0),
        displayName: displayName,
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(0),
        emailId: emailId
).build()

new RSVPPublisherJobBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(1),
        displayName: displayName,
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(1),
        emailId: emailId
).build()

new RSVPPublisherJobBuilder(
        dslFactory: factory,
        description: description,
        jobName: component + "-" + listOfEnvironment.get(2),
        displayName: displayName,
        branchesName: branchesName,
        githubUrl: githubUrl,
        credentialId: 'github',
        environment: listOfEnvironment.get(2),
        emailId: emailId
).build()