package com.dd.rsvp.publisher.ci.builder

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class RSVPPublisherJobBuilder {

    DslFactory dslFactory
    String jobName
    String description
    String displayName
    String githubUrl
    String branchesName
    String credentialId
    String environment
    String emailId

    Job build() {
        dslFactory.pipelineJob(jobName) {
            description(description)
            displayName(displayName)

            definition {
                cpsScm {
                    scm {
                        git {
                            branch(branchesName)
                            remote {
                                url(githubUrl)
                                credentials(credentialId)
                            }
                        }
                        scriptPath('jenkins/rsvp-publisher/Jenkinsfile')
                        lightweight(true)
                    }
                }
            }
            parameters {
                stringParam('ENVIRONMENT', environment)
                stringParam('EMAIL_TO', emailId)
                labelParam('Node') {
                    description('Select the node to execute the job!')
                    defaultValue(environment)
                }
            }
        }
    }

}
