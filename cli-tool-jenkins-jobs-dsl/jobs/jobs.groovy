import com.ericsson.de.taf.jenkins.dsl.builders.*
import com.ericsson.de.tools.cli.jenkins.dsl.builders.DocsBuildJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.DocsPublishJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.GerritItestJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.GerritJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.MasterBuildFlowBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.ReleaseJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.SimpleJobBuilder
import com.ericsson.de.tools.cli.jenkins.dsl.builders.SonarQubeGerritJobBuilder
import javaposse.jobdsl.dsl.DslFactory

/*
Full API documentation:
https://jenkinsci.github.io/job-dsl-plugin/

Job DSL playground:
http://job-dsl.herokuapp.com/
*/


def mvnUnitTest = "clean install -T 4"
def mvnITest = "test -Pitest -Dtaf.clusterId=239 -T 1C"
def mvnDeploy = "clean deploy -DskipTests"

def unitTests = 'Unit tests'
def iTests = 'Integration tests'
def snapshots = 'Snapshots deployment'


//Gerrit flow
def aa = new GerritJobBuilder('AA-gerrit-unit-tests', unitTests, mvnUnitTest)
//def ab = new GerritItestJobBuilder('AB-gerrit-integration-tests', iTests, mvnITest)
def ac = new SonarQubeGerritJobBuilder('AC-gerrit-sonar-qube')

//Build flow
def ba = new SimpleJobBuilder('BA-unit-tests', unitTests, mvnUnitTest)
//def bb = new SimpleJobBuilder('BB-integration-tests', iTests, mvnITest)
def bc = new SimpleJobBuilder('BC-deploy-snapshots', snapshots, mvnDeploy)
def bd = new DocsBuildJobBuilder('BD-docs-build')
def be = new DocsPublishJobBuilder('BE-docs-publish', bd.name)


def build = new MasterBuildFlowBuilder('B-build-flow','cli-tool.*-release',
        """\
           build '${ba.name}'
           build '${bc.name}'
           build '${bd.name}'
           build '${be.name}'
        """.stripIndent())

def release = new ReleaseJobBuilder('XX-release', build.name)

[aa, ac, ba, bc, bd, be, build, release]*.build(this as DslFactory)
