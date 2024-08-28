package com.ericsson.de.tools.cli;

import java.util.concurrent.TimeUnit;

import com.ericsson.cifwk.meta.API;

@API(API.Quality.Experimental)
class LocalCliTool implements CliTool {

    private final long defaultTimeout;

    LocalCliTool(long timeout) {
        this.defaultTimeout = timeout;
    }

    @Override
    public void close() {
        // NOSONAR
    }

    private static CliCommandResult executeCommandAndGetResult(String command, long timeout) {
        LocalCommandRuntimeExecutor executor = new LocalCommandRuntimeExecutor(command, TimeUnit.SECONDS.toMillis(timeout));
        long startTime = System.nanoTime();
        executor.execute();
        long endTime = System.nanoTime();

        String output = executor.getExitCode() == 0 ? executor.getStdOut() : executor.getStdErr();
        return new CliCommandResult(output, executor.getExitCode(), calculateTime(startTime, endTime));
    }

    private static long calculateTime(long startTime, long endTime) {
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

    @Override
    public CliCommandResult execute(String command) {
        return executeCommandAndGetResult(command, defaultTimeout);
    }

    @Override
    public CliCommandResult execute(String command, long timeout) {
        return executeCommandAndGetResult(command, timeout);
    }

    @Override
    public void interrupt() {
        throw new UnsupportedOperationException("'interrupt()' is unsupported operation for the SimpleExecutor");
    }
}
