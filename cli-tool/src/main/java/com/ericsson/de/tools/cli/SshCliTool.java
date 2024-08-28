package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@API(API.Quality.Experimental)
class SshCliTool extends AbstractCliTool implements CliToolShell {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshCliTool.class);

    protected static final String ECHO_EXIT_CODE_CMD = format("echo \"%s=$?\"", JSCH_EXIT_CODE_MARKER);
    private static final Pattern EXIT_CODE_OUTPUT_PATTERN = Pattern.compile(format("(?s).*%s=(\\d+)", JSCH_EXIT_CODE_MARKER));
    static final String CHANGED_PROMPT_COMMAND = format("PS1=\"%s\"", CHANGED_PROMPT_SUBSTRING);
    private static final Pattern ANY_STRING_PATTERN = Pattern.compile(".*");

    private static final String EXIT_CODE_EXCEPTION_MESSAGE_TEMPLATE = "Unable to retrieve command exit code. Possibly command was not completed in %s seconds";

    final Shell shell;

    public SshCliTool(CLI cli, long defaultTimeout, String host) {
        super(cli, defaultTimeout, host);
        this.shell = cli.openShell();
    }

    @Override
    public CliCommandResult execute(String command) {
        return execute(command, defaultTimeout);
    }

    @Override
    public CliCommandResult execute(String command, long timeout) {
        Preconditions.checkNotNull(command, "'command' is undefined");
        long startTime = System.nanoTime();
        shell.writeln(command + "; " + ECHO_EXIT_CODE_CMD);
        String output = expectOutputWithExitCode(timeout);
        long endTime = System.nanoTime();
        int exitValue = retrieveExitCode(output);

        return new CliCommandResult(filterOutput(output, command + "; " + ECHO_EXIT_CODE_CMD), exitValue, calculateExecutionTime(startTime, endTime));
    }

    private String expectOutputWithExitCode(long timeout) {
        try {
            return shell.expect(EXIT_CODE_OUTPUT_PATTERN, timeout);
        } catch (TimeoutException ex) {
            if (ex.getMessage().contains(EXIT_CODE_OUTPUT_PATTERN.pattern())) {
                String message = format(EXIT_CODE_EXCEPTION_MESSAGE_TEMPLATE, timeout);
                LOGGER.error(message);
                throw new TimeoutException(message, ex);
            }
            throw ex;
        }
    }

    @VisibleForTesting
    protected int retrieveExitCode(String output) {
        Preconditions.checkNotNull(output, "'output' is undefined");

        if (output.contains(JSCH_EXIT_CODE_MARKER)) {
            return retrieveExitCodeFromOutputString(output);
        }
        return Integer.MIN_VALUE;
    }

    @VisibleForTesting
    protected int retrieveExitCodeFromOutputString(String output) {
        Matcher matcher = EXIT_CODE_OUTPUT_PATTERN.matcher(output);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalStateException(format("No match found for exit code pattern '%s'.", JSCH_EXIT_CODE_MARKER));
        }
    }

    @Override
    public CliIntermediateResult writeLine(String command) {
        return writeLine(command, WaitConditions.regularExpression(ANY_STRING_PATTERN));
    }

    @Override
    public CliIntermediateResult writeLine(String command, long timeout) {
        return writeLine(command, WaitConditions.regularExpression(ANY_STRING_PATTERN, timeout));
    }

    @Override
    public CliIntermediateResult writeLine(String command, WaitCondition waitCondition) {
        Preconditions.checkNotNull(command, "command is undefined");
        Preconditions.checkNotNull(waitCondition, "waitCondition is undefined");
        shell.writeln(command);
        String output = waitCondition.wait(shell);
        return new CliIntermediateResult(filterOutput(output, command));
    }

    @Override
    public void close() {
        shell.disconnect();
        super.close();
    }

    @Override
    public CliHostHopper hopper() {
        return new CliHostHopper(this);
    }

    @Override
    public String switchUser(String username) {
        Preconditions.checkNotNull(username, "username is undefined");

        String command = "su - " + username;
        shell.writeln(command);

        return confirmUserAndEditPrompt(username, shell.read(), command);
    }

    @Override
    public String switchUser(String username, String password) {
        Preconditions.checkNotNull(username, "username is undefined");
        Preconditions.checkNotNull(password, "password is undefined");

        String command = "su - " + username;
        String output = checkPasswordRequired(password, command);
        return confirmUserAndEditPrompt(username, output, command);
    }

    @Override
    public String sudoRootUser() {
        String command = "sudo -i";
        shell.writeln(command);
        return confirmUserAndEditPrompt("root", shell.read(), command);
    }

    @Override
    public String sudoRootUser(String password) {
        Preconditions.checkNotNull(password, "password is undefined");
        String command = "sudo -i";
        String output = checkPasswordRequired(password, command);
        return confirmUserAndEditPrompt("root", output, command);
    }

    private String checkPasswordRequired(final String password, final String command) {
        String output = "";
        shell.writeln(command);
        try {
            shell.expect("Password", 5); // NOSONAR
            shell.writeln(password);
            output = shell.read();
        } catch (TimeoutException e) {
            LOGGER.warn("No password prompt received, checking user is logged in");
        }
        return output;
    }

    @Override
    public BufferedCliTool getBufferedShell() {
        return new BufferedShellCliTool(this);
    }

    private String confirmUserAndEditPrompt(String username, String output, String command) {
        String user = getCurrentUser();
        if (user.contains(username)) {
            shell.writeln(format("PS1=\"%s\"", CHANGED_PROMPT_SUBSTRING));
            return filterOutput(output, command);
        }
        throw new CLIToolException(String.format("Unable to switch to user (User: '%s'). Output contained '%s'", username, output));
    }

    private String getCurrentUser() {
        shell.writeln("whoami");
        return shell.read();
    }

    @Override
    public void interrupt() {
        char ctrlC = 0x3;
        String controlC = Character.toString(ctrlC);
        shell.writeln(controlC);
        shell.read(); // clears the buffer
    }

}
