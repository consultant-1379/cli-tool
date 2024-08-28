package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class SshCliToolShellTest {

    private static final int TIMEOUT = 5;

    private CliToolShell cliTool;
    private String scriptContent;

    private RemoteObjectHandler remoteObjectHandler;

    private static final String TEST_USER = "taffit_cli_tool_test_user";
    private static final String LARGE_FILE_NAME = "cli_tool_test.txt";
    private static final String REMOTE_LOG_FILE = "ha_01.log";

    @Before
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        User user = host.getUsers(UserType.ADMIN).get(0);
        SshShellBuilder builder = CliTools.sshShell(host.getIp()).withUsername(user.getUsername()).withPassword(user.getPassword());
        cliTool = builder.build();

        URL resource = Resources.getResource("scripts/matchMultipleLines.txt");
        scriptContent = Resources.toString(resource, Charset.defaultCharset());
        uploadRemoteLogFile(host);
    }

    @After
    public void tearDown() {
        cliTool.writeLine(String.format("userdel %s", TEST_USER));
        cliTool.close();
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_001")
    public void commandIntermediateResult() {
        cliTool.writeLine("mkdir test_directory_cli");
        cliTool.writeLine("cd test_directory_cli");
        CliIntermediateResult result = cliTool.writeLine("pwd", WaitConditions.substring("/root/test_directory_cli"));
        assertThat(result.getOutput()).contains("/root/test_directory_cli");
        cliTool.writeLine("rmdir /root/test_directory_cli");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_002")
    public void interactiveProcessOutput() {
        cliTool.writeLine(String.format("useradd %s", TEST_USER));
        cliTool.writeLine(String.format("passwd %s", TEST_USER));
        cliTool.writeLine(TEST_USER, WaitConditions.substring("Reenter New Password:"));
        assertThat(cliTool.writeLine(TEST_USER).getOutput()).contains("Password");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_003")
    public void multilineOutput() {
        try {
            final CliIntermediateResult result = cliTool.writeLine("echo -e '" + createMultilineStringWithKeyWord("tonight") + "'", 5);
            final List<String> lines = Arrays.asList(result.getOutput().split("\n"));
            assertThat(lines.size()).isGreaterThan(1);

            assertThat(result.getOutput()).contains("tonight");
        } catch (TimeoutException e) {
            Assert.fail("'tonight' not found in file");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0020")
    public void expectMatchExpressionOverMultipleLines() {
        String expression = "(?s)to.+night";
        try {
            cliTool.writeLine("echo -e '" + createMultilineStringWithKeyWord("to\nnight") + "'", WaitConditions.regularExpression(expression));
        } catch (TimeoutException e) {
            Assert.fail(String.format("Expression '%s' not found in file", expression));
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0020")
    public void regexCaseInsensitive() {
        String searchingKey = "Tonight";
        String expression = String.format("(?i)%s", searchingKey);
        String keyWord = "toNigHt";
        CliIntermediateResult result = cliTool.writeLine("echo -e '" + createMultilineStringWithKeyWord(keyWord) + "'", WaitConditions.regularExpression(expression));
        assertThat(result.getOutput()).contains(keyWord);
        assertThat(result.getOutput()).doesNotContain(searchingKey);
    }

    private String createMultilineStringWithKeyWord(String keyWord) {
        char enter = '\n';
        return "First line of multiline output" +
                enter +
                "Second line" +
                enter +
                keyWord +
                enter +
                "Last line";
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_004")
    public void expectMatchPattern() {
        try {
            cliTool.writeLine("echo -e '" + scriptContent + "'", WaitConditions.regularExpression(".In"));
        } catch (TimeoutException e) {
            Assert.fail("^In not found in file");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_005")
    public void toolIsStateful() {
        cliTool.execute("export test=testenv");
        CliCommandResult result = cliTool.execute("printenv test", 2);
        assertThat(result.getOutput()).contains("testenv");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_006")
    public void commandNotFoundExitCode() {
        CliCommandResult result = cliTool.execute("error", 1);
        assertThat(result.getExitCode()).isEqualTo(127);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_007")
    public void commandExecutionTimeIsLessOrEqualThanTimeout() {
        long startTime = 0;
        long endTime = 0;
        long executionTime;
        try {
            startTime = System.nanoTime();
            cliTool.writeLine("echo \"Current substring exist in output\"", WaitConditions.substring("Current substring exist in output", TIMEOUT));
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            assertThat(executionTime < TIMEOUT).isTrue();
        } catch (TimeoutException ex) {
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            Assert.fail(String.format("Actual execution time greater than %s second. Expected less or equal to %s but was %s", TIMEOUT, TIMEOUT, executionTime));
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_008")
    public void commandExecutionTimeIsGreaterThanExpectedTimeout() {
        long startTime = 0;
        long endTime;
        long executionTime;
        try {
            startTime = System.nanoTime();
            cliTool.writeLine("sleep 11", WaitConditions.substring("Current substring don't exist in output", TIMEOUT));
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            Assert.fail(String.format("Actual execution time less than %s second. Expected greater than %s but was %s", TIMEOUT, TIMEOUT, executionTime));
        } catch (TimeoutException ex) {
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            assertThat(executionTime).isGreaterThan((long) TIMEOUT);
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_009")
    public void intermediateResultContainsOnlyCommandResult() {
        CliIntermediateResult result = cliTool.writeLine("echo \"Hello world\"", TIMEOUT);
        assertThat(result.getOutput()).isEqualTo("Hello world");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_010")
    public void multipleInputOutput() {
        cliTool.writeLine(String.format("useradd %s", TEST_USER));
        cliTool.writeLine(String.format("passwd %s", TEST_USER), WaitConditions.substring("New Password"));
        cliTool.writeLine(TEST_USER);
        CliIntermediateResult result = cliTool.writeLine(TEST_USER, WaitConditions.substring("Password changed"));

        assertThat(result.getOutput()).isEqualTo("Password changed.");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_012")
    public void notEndedCommandUsingExecuteMustThrowException() {
        cliTool.writeLine(String.format("useradd %s", TEST_USER));
        cliTool.writeLine(String.format("passwd %s", TEST_USER));
        try {
            cliTool.execute(StringUtils.EMPTY);
            Assert.fail();
        } catch (TimeoutException ex) {
            assertThat(ex.getMessage()).contains("Unable to retrieve command exit code.");
        }
    }

    @Ignore
    @Test
    @TestId(id = "TAF_CLI_TOOL2_011")
    public void largeOutput() {
        Host netsim = TafDataHandler.findHost().withHostname("netsim").get();
        remoteObjectHandler = new RemoteObjectHandler(netsim, netsim.getUsers(UserType.ADMIN).get(0));
        if (!remoteObjectHandler.remoteFileExists("/root/" + LARGE_FILE_NAME)) {
            remoteObjectHandler.copyLocalFileToRemote("scripts/" + LARGE_FILE_NAME, "/root/" + LARGE_FILE_NAME);
        }
        final CliCommandResult result = cliTool.execute("cat " + LARGE_FILE_NAME, 120);
        assertOutput(result);
    }

    private void assertOutput(CliCommandResult result) {
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutput().getBytes().length).isGreaterThan(5_000_000);
        assertThat(result.getOutput()).contains("1 [UNIQUE]");
        assertThat(result.getOutput()).contains("23500");
        assertThat(result.getOutput()).contains("47600");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0021")
    public void commandExecutionTimeForTheExecuteMethodIsLessOrEqualThanTimeout() {
        long startTime = 0;
        long endTime = 0;
        long executionTime;
        try {
            startTime = System.nanoTime();
            cliTool.execute("echo \"Current substring exist in output\"", TIMEOUT);
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            assertThat(executionTime < TIMEOUT).isTrue();
        } catch (TimeoutException ex) {
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            Assert.fail(String.format("Actual execution time greater than %s second. Expected less or equal to %s but was %s", TIMEOUT, TIMEOUT, executionTime));
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0022")
    public void commandExecutionTimeForTheExecuteMethodIsGreaterThanExpectedTimeout() {
        long startTime = 0;
        long endTime;
        long executionTime;
        try {
            startTime = System.nanoTime();
            cliTool.execute("sleep 11", TIMEOUT);
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            Assert.fail(String.format("Actual execution time less than %s second. Expected greater than %s but was %s", TIMEOUT, TIMEOUT, executionTime));
        } catch (TimeoutException ex) {
            endTime = System.nanoTime();
            executionTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            assertThat(executionTime).isGreaterThan((long) TIMEOUT);
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0024")
    public void switchUser() {
        cliTool.switchUser("netsim", "netsim");
        CliIntermediateResult resultAfterSwitch = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitch.getOutput()).contains("netsim");

        cliTool.switchUser("root", "shroot");
        CliIntermediateResult resultAfterSwitchBAck = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitchBAck.getOutput()).contains("root");

        cliTool.switchUser("netsim");
        CliIntermediateResult resultAfterSwitch1 = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitch1.getOutput()).contains("netsim");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0025")
    public void switchUserWithPasswordNotRequired() {
        cliTool.switchUser("root", "shroot");
        CliIntermediateResult resultAfterSwitch = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitch.getOutput()).contains("root");

        cliTool.switchUser("netsim");
        CliIntermediateResult resultAfterSwitch1 = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitch1.getOutput()).contains("netsim");

        cliTool.switchUser("root", "shroot");
        CliIntermediateResult resultAfterSwitchBack = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitchBack.getOutput()).contains("root");

        cliTool.switchUser("netsim", "netsim");
        CliIntermediateResult resultAfterSwitchPWNotNeeded = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitchPWNotNeeded.getOutput()).contains("netsim");
    }

    @Test(expected = CLIToolException.class)
    @TestId(id = "TAF_CLI_TOOL2_0026")
    public void switchUserFail() {
        try {
            cliTool.switchUser("netsim", "netsim");
            CliIntermediateResult resultAfterSwitch = cliTool.writeLine("whoami");
            assertThat(resultAfterSwitch.getOutput()).contains("netsim");

            cliTool.switchUser("root");
            cliTool.writeLine("whoami");
        } catch (TimeoutException e) {
            Assert.fail("Password for root user is required");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_0027")
    public void switchUserChangesPrompt() {
        try {
            cliTool.switchUser("netsim", "netsim");
            CliIntermediateResult resultAfterSwitchBAck = cliTool.writeLine("whoami");
            assertThat(resultAfterSwitchBAck.getOutput()).contains("netsim");
            cliTool.writeLine("echo ${PS1}", WaitConditions.substring("wont be found"));
        } catch (TimeoutException e) {
            assertThat(e.getMessage()).contains("PRIMARY_PROMPT>#");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_028")
    public void executeCommandFiltersOutputCorrectly() {
        String command = "/bin/grep 'Nodes used by HA'                               /root/ha_01.log | /usr/bin/tail -1 | /bin/awk -F '[' '{print $2}' | /usr/bin/tr -d \" ]'\" | " +
                "/bin/sed -e 's/,/\\n/g' | /bin/awk -F '_' '{print $2}' | /bin/sort -u | /usr/bin/xargs | /bin/sed -e 's/ /,/g' | /bin/grep -i 'LTE.*ERBS'";
        CliCommandResult result = cliTool.execute(command);
        assertThat(result.getOutput()).contains("LTE22ERBS00002,LTE22ERBS00012,LTE22ERBS00018");
        assertThat(result.getOutput()).doesNotContain("taf.jsch.exitcode");
        assertThat(result.getOutput()).doesNotContain(command);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_029")
    public void executeCommandFiltersOutputCorrectly_netsimNodes() {
        String command = "(/bin/grep 'Nodes used by HA'                           /root/ha_01.log | /usr/bin/tail -1 | /bin/awk -F '[' '{print $2}' | /usr/bin/tr -d \" ]'\" " +
                "| /bin/sed -e 's/,/\\n/g' | /bin/awk -F '_' '{print $1}' | /bin/sort -u | /usr/bin/xargs | /bin/sed -e 's/ /,/g' | /bin/grep -i 'netsim.*\\-')";
        CliCommandResult result = cliTool.execute(command);
        assertThat(result.getOutput()).contains(",ieatnetsimv016-07");
        assertThat(result.getOutput()).doesNotContain("taf.jsch.exitcode");
        assertThat(result.getOutput()).doesNotContain(command);
    }

    private void uploadRemoteLogFile(Host host) {
        RemoteObjectHandler remoteObjectHandler = new RemoteObjectHandler(host, host.getUsers(UserType.ADMIN).get(0));
        if (!remoteObjectHandler.remoteFileExists("/root/" + REMOTE_LOG_FILE)) {
            remoteObjectHandler.copyLocalFileToRemote("scripts/" + REMOTE_LOG_FILE, "/root/" + REMOTE_LOG_FILE);
        }
        cliTool.execute("chmod +x " + REMOTE_LOG_FILE);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_030")
    public void verifyBufferedShell() {
        BufferedCliTool bufferedShell = cliTool.getBufferedShell();
        bufferedShell.send("top");
        String output = bufferedShell.getOutput();
        assertThat(output).contains("Tasks:");

        String output1 = bufferedShell.getOutput();
        assertThat(output).isNotEqualTo(output1);

        bufferedShell.interrupt();

        CliCommandResult result = cliTool.execute("whoami");
        assertThat(result.getOutput()).isEqualTo("root");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_031")
    public void sudoRootUser() {
        cliTool.switchUser("netsim", "netsim");
        CliIntermediateResult resultAfterSwitch = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitch.getOutput()).contains("netsim");

        cliTool.sudoRootUser();
        CliIntermediateResult resultAfterSwitchBAck = cliTool.writeLine("whoami");
        assertThat(resultAfterSwitchBAck.getOutput()).contains("root");
    }
}
