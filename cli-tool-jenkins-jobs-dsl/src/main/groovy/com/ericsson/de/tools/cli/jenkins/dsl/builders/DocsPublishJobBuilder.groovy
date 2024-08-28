package com.ericsson.de.tools.cli.jenkins.dsl.builders

import com.ericsson.de.tools.cli.jenkins.dsl.Constants
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class DocsPublishJobBuilder extends FreeStyleJobBuilder {

    static final String DESCRIPTION = 'Documentation publishing'

    final String docsBuildJobName

    DocsPublishJobBuilder(String name, String docsBuildJobName) {
        super(name, DESCRIPTION)
        this.docsBuildJobName = docsBuildJobName
    }

    @Override
    Job build(DslFactory factory) {
        super.build(factory).with {
            parameters {
                stringParam('TAFUSER_PASSWORD', '${TAFUSER_PASSWORD}', 'This is a global password set in jenkins system configuration')
            }
            wrappers {
                maskPasswords()
                injectPasswords {
                    injectGlobalPasswords()
                }
            }
            steps {
                copyArtifacts(docsBuildJobName) {
                    buildSelector {
                        latestSuccessful()
                    }
                    includePatterns "${Constants.DOCS_DIRECTORY}/${Constants.DOCS_ZIP}"
                }
                shell """\
                    SSHPASS=/proj/PDU_OSS_CI_TAF/tools/sshpass-1.05/sshpass
                    HOST=10.210.27.101
                    USER=tafuser
                    PASSWORD=\${TAFUSER_PASSWORD}
                    targetDir=/proj/pduosstaf/taf
                    \${SSHPASS} -p \$PASSWORD scp target/site/site.zip \${USER}@\${HOST}:/tmp/tafuse/site.zip
                    \${SSHPASS} -p \$PASSWORD ssh \${USER}@\${HOST} <<EOF
                    rm -rf \${targetDir}/cli-tool
                    unzip /tmp/tafuse/site.zip -d \${targetDir}/cli-tool
                    rm -rf /tmp/tafuse/site.zip
                    EOF
                    """.stripIndent()
            }
        }
    }
}
