package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.google.common.base.Preconditions;

@API(API.Quality.Experimental)
class SimpleExecutor extends AbstractCliTool {

    SimpleExecutor(CLI cli, long defaultTimeout, String host) {
        super(cli, defaultTimeout, host);
    }

    @Override
    public CliCommandResult execute(String command) {
        return execute(command, defaultTimeout);
    }

    @Override
    public CliCommandResult execute(String command, long timeout) {
        Preconditions.checkNotNull(command, "'command' is undefined");

        long startTime = System.nanoTime();
        Shell shell = cli.executeCommand(command);
        String output = shell.read(timeout);
        long endTime = System.nanoTime();
        shell.expectClose(timeout);

        int exitValue = shell.getExitValue();
        shell.disconnect();

        return new CliCommandResult(filterOutput(output, command), exitValue, calculateExecutionTime(startTime, endTime));
    }

    @Override
    public void interrupt() {
        throw new UnsupportedOperationException("'interrupt()' is unsupported operation for the SimpleExecutor");
    }
}
