package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SshLoginTest {

    private static final String CURRENT_HOST = "255.255.255.0";
    private static final String TARGET_HOST = "255.255.255.1";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SshLogin login;


    @Before
    public void init() {
        login = new SshLogin(null, null);
    }

    @Test
    public void validateConnection_NoRouteHost() {
        thrown.expect(CLIToolException.class);
        thrown.expectMessage(String.format("No route from '%s' host to '%s' host", CURRENT_HOST, TARGET_HOST));

        login.validateConnection("Message: \n No route to host. test", CURRENT_HOST, TARGET_HOST);
    }

    @Test
    public void validateConnection_ConnectionTimedOut() {
        thrown.expect(CLIToolException.class);
        thrown.expectMessage(String.format("Connection to '%s' host from '%s' is timed out", TARGET_HOST, CURRENT_HOST));

        login.validateConnection("Message: \n Connection timed out. Additional message", CURRENT_HOST, TARGET_HOST);
    }

    @Test
    public void validateConnection_InvalidArgument() {
        thrown.expect(CLIToolException.class);
        thrown.expectMessage("Invalid argument");

        login.validateConnection("Message: \n Invalid argument. test", CURRENT_HOST, TARGET_HOST);
    }
}
