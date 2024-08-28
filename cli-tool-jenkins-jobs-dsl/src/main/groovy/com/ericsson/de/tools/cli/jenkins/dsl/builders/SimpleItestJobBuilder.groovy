package com.ericsson.de.tools.cli.jenkins.dsl.builders

import com.ericsson.de.tools.cli.jenkins.dsl.Constants
import com.ericsson.de.tools.cli.jenkins.dsl.utils.Git
import com.ericsson.de.tools.cli.jenkins.dsl.utils.Maven
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class SimpleItestJobBuilder extends FreeStyleJobBuilder {

    final String mavenGoal

    SimpleItestJobBuilder(String name,
                          String description,
                          String mavenGoal) {
        super(name, description)

        this.mavenGoal = mavenGoal
    }

    @Override
    Job build(DslFactory factory) {
        super.build(factory).with {
            label Constants.ENM_READY_VAPP
            scm {
                Git.simple delegate
            }
            steps {
                Maven.goal delegate, mavenGoal
            }
        }
    }
}
