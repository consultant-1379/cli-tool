package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLIShell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.ericsson.de.tools.cli.AbstractCliTool.JSCH_EXIT_CODE_MARKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SshCliToolTest {

    private SshCliTool cliTool;

    @Mock
    private CLI cli;

    @Mock
    private CLIShell shell;


    @Before
    public void init() {
        cli = mock(CLI.class);
        shell = mock(CLIShell.class);
        when(cli.openShell()).thenReturn(shell);
        cliTool = new SshCliTool(cli, 5, "test_host_1");
    }

    @Test
    public void retrieveExitCode() {
        String exitCodeTestData = createExitCodeTestData("=0");

        int exitCode = cliTool.retrieveExitCodeFromOutputString(exitCodeTestData);
        assertThat(exitCode).isEqualTo(0);
    }

    private String createCommandOutputTestData() {
        StringBuilder builder = new StringBuilder();
        builder.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        builder.append("echo \"Hello world\"");
        builder.append(SshCliTool.NEWLINE_SEPARATOR);
        builder.append("Hello world");
        builder.append(SshCliTool.NEWLINE_SEPARATOR);
        builder.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        return builder.toString();
    }

    private String createExitCodeTestData(String str) {
        StringBuilder builder = new StringBuilder();
        builder.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        builder.append(SshCliTool.ECHO_EXIT_CODE_CMD);
        builder.append(SshCliTool.NEWLINE_SEPARATOR);
        builder.append(JSCH_EXIT_CODE_MARKER);
        builder.append(str);
        builder.append(SshCliTool.NEWLINE_SEPARATOR);
        builder.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        builder.append(SshCliTool.NEWLINE_SEPARATOR);
        return builder.toString();
    }

    @Test(expected = IllegalStateException.class)
    public void frongExitCodePattern() {
        cliTool.retrieveExitCodeFromOutputString(createExitCodeTestData("=qewojgjwe"));
    }

    @Test
    public void filterOutput() {
        String filteredOutput = cliTool.filterOutput(createCommandOutputTestData(), "echo \"Hello world\"");
        assertThat(filteredOutput).isEqualTo("Hello world");
    }

    @Test
    public void filterAdditionalOutput() {
        String command = "PROMPT>#\"\n" +
                "PRIMARY_PROMPT>#whoami; echo \"taf.jsch.exitcode=$?\"\n" +
                "netsim\n" +
                "taf.jsch.exitcode=0\n" +
                "PRIMARY_PROMPT>#";
        String filteredOutput = cliTool.filterOutput(command, "whoami");
        assertThat(filteredOutput).isEqualTo("netsim");
    }

    @Test()
    public void filterOutputWithSpecialSymbolsWithoutExceptions() {
        cliTool.filterOutput(SshCliTool.CHANGED_PROMPT_SUBSTRING + "\\q", "\\q");
    }

    @Test
    public void filterOutputWithPrompt() {
        StringBuilder output = new StringBuilder(String.format("%sls", SshCliTool.CHANGED_PROMPT_SUBSTRING));
        output.append(SshCliTool.NEWLINE_SEPARATOR);
        output.append("ls");
        output.append(SshCliTool.NEWLINE_SEPARATOR);
        output.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        assertThat(cliTool.filterOutput(output.toString(), "ls")).isEqualTo("ls");
    }

    @Test
    public void filterOutputWithoutPrompt() {
        StringBuilder output = new StringBuilder("ls");
        output.append(SshCliTool.NEWLINE_SEPARATOR);
        output.append("ls");
        output.append(SshCliTool.NEWLINE_SEPARATOR);
        output.append(SshCliTool.CHANGED_PROMPT_SUBSTRING);
        assertThat(cliTool.filterOutput(output.toString(), "ls")).isEqualTo("ls");
    }

    @Test
    public void commandResultSuccessTrue() {
        CliCommandResult result = new CliCommandResult("some result", 0, 99);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void commandResultSuccessFalse() {
        CliCommandResult result = new CliCommandResult("some result", 1, 99);
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    public void outputFilterExcludeCommandWithSpecialCharacters() {
        String command = "ls; echo \"taf.jsch.exitcode=$?\"";
        String str = command + "\n" +
                "70-persistent-net.rules                js.bsh\n" +
                "anaconda-ks.cfg                        ks-post.log\n" +
                "cli-tool-demo-logs.txt                 matchMultipleLines.txt\n" +
                "cli-tool-workshop-interactive-bash.sh  puppet\n" +
                "dhclient-exit-hooks                    RPM-GPG-KEY.dag.txt\n" +
                "install.log                            vSPC.py\n" +
                "install.log.syslog";


        assertThat(cliTool.filterOutput(str, command)).doesNotContain(command);
    }
}
