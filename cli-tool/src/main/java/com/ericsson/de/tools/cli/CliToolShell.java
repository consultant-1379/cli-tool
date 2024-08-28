package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;

/**
 * This interface provides means for CLI command execution
 * <p>
 */
@API(API.Quality.Experimental)
public interface CliToolShell extends CliTool {

    /**
     * Write shell command when user input required and return result without exit code
     * <p>
     * Example:<br/>
     * <code>writeLine("echo 'Hello CliToolShell'");</code>
     * </p>
     *
     * @param command
     * @return {@link CliIntermediateResult}
     * @throws TimeoutException if default timeout ({@link AbstractCliToolBuilder#DEFAULT_TIMEOUT}) expired
     */
    CliIntermediateResult writeLine(String command);

    /**
     * Write shell command when user input required and return result without exit code
     * <p>
     * Example:<br/>
     * <code>writeLine("ls", 10);</code>
     * </p>
     *
     * @param command
     * @param timeout (in seconds)
     * @return {@link CliIntermediateResult}
     * @throws TimeoutException if timeout expired
     */
    CliIntermediateResult writeLine(String command, long timeout);

    /**
     * Write shell command with wait condition when user input required and return result without exit code
     * <p>
     * Example:<br/>
     * <code>writeLine("echo 'Hello CliToolShell', WaitConditions.substring("Hello CliToolShell"))</code>
     * </p>
     *
     * @param command
     * @param waitCondition
     * @return {@link CliIntermediateResult}
     * @throws TimeoutException if timeout expired
     */
    CliIntermediateResult writeLine(String command, WaitCondition waitCondition);

    /**
     * This method return instance of {@link CliHostHopper} which allows do hops
     *
     * @return {@link CliHostHopper}
     * */
    CliHostHopper hopper();

    /**
     * Shell command to switch user with provided username, returns result without exit code
     *
     * @param username
     * @return UserLoginOutput
     */
    String switchUser(String username);

    /**
     * Shell command to switch user with provided username and password, returns result without exit code
     *
     * @param username
     * @param password
     * @return UserLoginOutput
     */
    String switchUser(String username, String password);

    /**
     * Shell command to sudo to root user, returns result without exit code
     *
     * @return UserLoginOutput
     */
    String sudoRootUser();

    /**
     * Shell command to sudo to root user with provided password, returns result without exit code
     *
     * @param password
     * @return UserLoginOutput
     */
    String sudoRootUser(String password);

    /**
     * Shell Command to switch to buffered Shell.
     *
     * @return {@link BufferedCliTool}
     */
    BufferedCliTool getBufferedShell();
}
