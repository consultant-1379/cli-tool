package com.ericsson.de.tools.cli;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

/**
 * Created by ekongla on 27/04/2017.
 */
public class CliHostHopperTest {

    private static class InvalidCliTool implements CliToolShell {

        @Override
        public void close() {

        }

        @Override
        public CliCommandResult execute(String command) {
            return null;
        }

        @Override
        public CliCommandResult execute(String command, long timeout) {
            return null;
        }

        @Override
        public CliIntermediateResult writeLine(String command) {
            return null;
        }

        @Override
        public CliIntermediateResult writeLine(String command, long timeout) {
            return null;
        }

        @Override
        public CliIntermediateResult writeLine(String command, WaitCondition waitCondition) {
            return null;
        }

        @Override
        public CliHostHopper hopper() {
            return null;
        }

        @Override
        public String switchUser(String username) { return null; }

        @Override
        public String switchUser(String username, String password) { return null; }

        @Override
        public String sudoRootUser() { return null; }

        @Override
        public String sudoRootUser(String password) { return null; }

        @Override
        public BufferedCliTool getBufferedShell() {
            return null;
        }

        @Override
        public void interrupt() {
        }
    }

    @Mock
    private SshCliTool cliTool;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        cliTool = mock(SshCliTool.class);
    }

    @Test
    public void instantiationWithInvalidArgument() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("com.ericsson.de.tools.cli.CliHostHopperTest.InvalidCliTool is invalid argument type. Only com.ericsson.de.tools.cli.SshCliTool is allowed");

        new CliHostHopper(new InvalidCliTool());
    }

    @Test
    public void instantiationWithValidArgument() {
        new CliHostHopper(cliTool);
    }
}
