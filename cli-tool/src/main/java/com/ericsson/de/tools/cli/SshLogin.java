package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * This class is responsible for connecting to remote host via 'ssh' command using provided CLI tool and target host.
 */
class SshLogin {

    private static final String SSH_COMMAND_PATTERN = "ssh %s %s@%s -p %s";
    private static final String SSH_COMMAND_WITH_IDENTITY_FILE_PATTERN = "ssh -i %s %s@%s %s";

    public static final String STRICT_HOST_KEY_CHECKING_YES = "-o StrictHostKeyChecking=yes";
    public static final String STRICT_HOST_KEY_CHECKING_NO = "-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no";

    private static final String USER_PASSWORD_PROMPT = "(?s).*[Pp]assword";

    private static final String GENERIC_HASH_PROMPT = "^[^#]+#\\s*$";
    private static final String GENERIC_ANGLE_PROMPT = "^[^>]+>\\s*$";
    private static final Pattern INITIAL_LOGIN_PATTERN = Pattern.compile(String.format("Are you sure|[Pp]assword|%s|%s|[$]", GENERIC_HASH_PROMPT, GENERIC_ANGLE_PROMPT));

    private final SshCliTool cliTool;
    private final TargetHost targetHost;

    SshLogin(SshCliTool cliTool, TargetHost targetHost) {
        this.cliTool = cliTool;
        this.targetHost = targetHost;
    }

    void login() {
        Preconditions.checkNotNull(targetHost.getUsername(), "'user' is undefined");
        Preconditions.checkNotNull(targetHost.getHost(), "'host' is undefined");
        Preconditions.checkState(StringUtils.isNotEmpty(targetHost.getHost()), "'host' is empty");
        Preconditions.checkNotNull(targetHost.getPort(), "'port' is undefined");
        Preconditions.checkNotNull(targetHost.getPassword(), "'password' is undefined");

        executeLoginProcess(targetHost.getHost(), targetHost.getPassword());
    }

    private void executeLoginProcess(String host, String password) {
        executeSshCommand(host);
        if (isPasswordRequired()) {
            CliIntermediateResult loginResult = cliTool.writeLine(password);
            if (StringUtils.containsIgnoreCase(loginResult.getOutput(), "denied")) {
                throw new CLIToolException(String.format("Permission denied (host: '%s'). Password **** is incorrect", host));
            }
        }

        changePromptAndSetHost(host);
    }

    @VisibleForTesting
    protected void changePromptAndSetHost(String host) {
        cliTool.writeLine(SshCliTool.CHANGED_PROMPT_COMMAND, WaitConditions.substring(AbstractCliTool.CHANGED_PROMPT_SUBSTRING));
        cliTool.setCurrentHost(host);
    }

    private void executeSshCommand(String host) {
        try {
            CliIntermediateResult result = cliTool.writeLine(createSshCommand(targetHost), WaitConditions.regularExpression(INITIAL_LOGIN_PATTERN));
            validateConnection(result.getOutput(), cliTool.getCurrentHost(), host);
        } catch (TimeoutException ex) {
            throw new CLIToolException(String.format("Unable establish connection to host %s", host), new TimeoutException("Connection timeout is too long", ex));
        }
    }

    @VisibleForTesting
    protected void validateConnection(String commandOutput, String currentHost, String targetHost) {
        if (StringUtils.containsIgnoreCase(commandOutput, "No route to host")) {
            throw new CLIToolException(String.format("No route from '%s' host to '%s' host", currentHost, targetHost));
        } else if (StringUtils.containsIgnoreCase(commandOutput, "Connection timed out")) {
            throw new CLIToolException(String.format("Connection to '%s' host from '%s' is timed out", targetHost, currentHost));
        } else if (StringUtils.containsIgnoreCase(commandOutput, "Invalid argument")) {
            throw new CLIToolException("Invalid argument");
        } else if (StringUtils.containsIgnoreCase(commandOutput, "Name or service not known")) {
            throw new CLIToolException(String.format("Connection to  '%s' Failed. Could not resolve hostname, please "
                    + "specify IP address", targetHost));
        }
    }

    private boolean isPasswordRequired() {
        try {
            cliTool.writeLine(StringUtils.EMPTY, WaitConditions.regularExpression(USER_PASSWORD_PROMPT));
            return true;
        } catch (TimeoutException ex) {
            if (ex.getMessage().contains(String.format("Timeout trying to match '%s'", USER_PASSWORD_PROMPT))) {
                return false;
            } else {
                throw new CLIToolException(String.format("Hop to host %s is failed", targetHost.getHost()), ex);
            }
        }
    }

    private String createSshCommand(TargetHost target) {
        String identityFilePath = StringUtils.defaultString(target.getPathToSshPrivateKeyFile(), StringUtils.EMPTY);
        String strictHostKeyCheckingOption = getStrictHostKeyCheckingOption(target.isEnforceStrictHostKeyChecking());

        final String sshCommand;
        if (StringUtils.isEmpty(identityFilePath)) {
            sshCommand = createSshCommand(SSH_COMMAND_PATTERN, strictHostKeyCheckingOption, target.getUsername(), target.getHost(), String.valueOf(target.getPort()));
        } else {
            sshCommand = createSshCommand(SSH_COMMAND_WITH_IDENTITY_FILE_PATTERN, identityFilePath, target.getUsername(), target.getHost(), strictHostKeyCheckingOption);
        }
        return sshCommand;
    }

    private static String createSshCommand(String commandPattern, String... params) {
        return String.format(commandPattern, params);
    }

    private static String getStrictHostKeyCheckingOption(boolean enforceStrictHostKeyChecking) {
        return enforceStrictHostKeyChecking ? STRICT_HOST_KEY_CHECKING_YES : STRICT_HOST_KEY_CHECKING_NO;
    }
}
