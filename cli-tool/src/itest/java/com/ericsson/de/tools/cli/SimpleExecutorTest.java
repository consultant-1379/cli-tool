package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SimpleExecutorTest {

    private CliTool cliTool;

    private RemoteObjectHandler remoteObjectHandler;

    private static final String LARGE_FILE_NAME = "cli_tool_test.txt";

    @Before
    public void setUp() {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        User user = host.getUsers(UserType.ADMIN).get(0);

        SimpleExecutorBuilder builder = CliTools.simpleExecutor(host.getIp()).withUsername(user.getUsername()).withPassword(user.getPassword());
        cliTool = builder.build();
    }

    @After
    public void tearDown() {
        cliTool.close();
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_015")
    public void simpleExecuteExitCode() {
        assertThat(cliTool.execute("help").getExitCode()).isEqualTo(0);
        assertThat(cliTool.execute("XXX_Wrong_Command_XXX").getExitCode()).isEqualTo(127);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_016")
    public void emptyCommandExitCode() {
        CliCommandResult result = cliTool.execute(StringUtils.EMPTY);
        assertThat(result.getExitCode()).isEqualTo(0);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_017")
    public void commandNotFound() {
        CliCommandResult result = cliTool.execute("error");
        assertThat(result.getOutput()).contains("command not found");
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_018")
    public void largeOutput() throws IOException {
        Host netsim = DataHandler.getHostByName("netsim");
        remoteObjectHandler = new RemoteObjectHandler(netsim, netsim.getUsers(UserType.ADMIN).get(0));
        if (!remoteObjectHandler.remoteFileExists("/root/" + LARGE_FILE_NAME)) {
            remoteObjectHandler.copyLocalFileToRemote("scripts/" + LARGE_FILE_NAME, "/root/" + LARGE_FILE_NAME);
        }

        final CliCommandResult result = cliTool.execute("cat /root/" + LARGE_FILE_NAME, 40);
        assertOutput(result);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_019")
    public void toolIsStateless() {
        cliTool.execute("export test=testenv");
        CliCommandResult result = cliTool.execute("printenv test", 5);
        assertThat(result.getOutput()).doesNotContain("testenv");
    }

    private void assertOutput(CliCommandResult result) {
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutput().getBytes().length).isGreaterThan(5_000_000);
        assertThat(result.getOutput()).contains("1 [UNIQUE]");
        assertThat(result.getOutput()).contains("23500");
        assertThat(result.getOutput()).contains("47600");
    }

}
