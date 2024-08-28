package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;

@API(API.Quality.Experimental)
public interface CliTool extends InterruptibleCommand, Closable {

    /**
     * Execute shell command in interactive mode and return execution result
     * <p>
     * Example:<br/>
     * <code>execute("ls");<code/><br/>
     * <code>execute("ls -a");</code><br/>
     * <code>execute("cat file.txt");</code><br/>
     * </p>
     *
     * @param command
     * @return {@link CliCommandResult}
     * @throws TimeoutException if default timeout ({@link AbstractCliToolBuilder#DEFAULT_TIMEOUT}) expired
     */
    CliCommandResult execute(String command);

    /**
     * Execute shell command in interactive mode  and return execution result
     * <p>
     * Example:<br/>
     * <code>execute("ls", 10);<code/>
     * <p/>
     *
     * @param command
     * @param timeout (in seconds)
     * @return {@link CliCommandResult}
     * @throws TimeoutException if timeout expired
     */
    CliCommandResult execute(String command, long timeout);
}
