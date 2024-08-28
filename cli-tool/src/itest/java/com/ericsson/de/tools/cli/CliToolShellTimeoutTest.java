package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.lang.String.format;

public class CliToolShellTimeoutTest {

    private CliToolShell cliTool;
    private static final int TIMEOUT = 2;
    private static final String NON_EXISTING_MATCH_PATTERN = "non-existing-match-pattern";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        User user = host.getUsers(UserType.ADMIN).get(0);
        SshShellBuilder builder = CliTools.sshShell(host.getIp()).withUsername(user.getUsername()).withPassword(user.getPassword());
        cliTool = builder.build();
    }

    @Test
    public void executeThrowHumanReadableTimeoutExceptionMessageForExitCode() {
        thrown.expect(TimeoutException.class);
        thrown.expectMessage(format("Unable to retrieve command exit code. Possibly command was not completed in %s seconds", TIMEOUT));
        cliTool.execute("vi texttest.txt", TIMEOUT);
    }

    @Test
    public void expectShouldThrowDefaultMatchMessage() {
        thrown.expect(TimeoutException.class);
        thrown.expectMessage(format("Timeout trying to match '%s'", NON_EXISTING_MATCH_PATTERN));
        cliTool.writeLine("ls", WaitConditions.substring(NON_EXISTING_MATCH_PATTERN));
    }

}
