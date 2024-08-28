package com.ericsson.de.tools.cli.jenkins.dsl.builders

import com.ericsson.de.tools.cli.jenkins.dsl.Constants
import com.ericsson.de.tools.cli.jenkins.dsl.utils.Gerrit
import com.ericsson.de.tools.cli.jenkins.dsl.utils.Git
import com.ericsson.de.tools.cli.jenkins.dsl.utils.Maven
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class GerritItestJobBuilder extends FreeStyleJobBuilder {

    private static final String DESCRIPTION_SUFFIX = 'as a part of Gerrit verification process'

    final String mavenGoal

    GerritItestJobBuilder(String name,
                          String description,
                          String mavenGoal) {
        super(name, "${description} ${DESCRIPTION_SUFFIX}")

        this.mavenGoal = mavenGoal
    }

    @Override
    Job build(DslFactory factory) {
        def job = super.build(factory)
        job.with {
            label Constants.ENM_READY_VAPP
            concurrentBuild()
            scm {
                Git.gerrit delegate
            }
            triggers {
                Gerrit.patchsetCreated delegate
            }
            steps {
                Maven.goal delegate, mavenGoal
            }
        }
        return job
    }
}
