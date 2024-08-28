package com.ericsson.de.tools.cli;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@API(API.Quality.Experimental)
class LocalCommandRuntimeExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCommandRuntimeExecutor.class);

    private final String command;
    private final long timeout;

    private String stdOut;
    private String stdErr;
    private Integer exitCode;

    LocalCommandRuntimeExecutor(String command, long timeout) {
        this.command = command;
        this.timeout = timeout;
    }

    public void execute() {
        Timer timer = new Timer();
        timer.schedule(createTimerTask(Thread.currentThread()), this.timeout);

        Process process = null;
        try {
            process = createAndStartProcess(command);
            waitForProcessExecutionAndSetResult(process);

            process.destroy();
        } catch (IOException e) {
            LOGGER.error("Failed command execution", e);
            throw new CLIToolException("Failed command execution", e);
        } catch (InterruptedException e) {
            LOGGER.error("Command execution interruption caused by timeout", e);
            throw new TimeoutException(String.format("Command execution exceeds available time %s seconds", TimeUnit.MILLISECONDS.toSeconds(timeout)));
        } finally {
            if (process != null) {
                process.destroy();
            }
            process = null;
            timer.cancel();
        }
    }

    private static TimerTask createTimerTask(final Thread thread) {
        return new TimerTask() {
            @Override
            public void run() {
                thread.interrupt();
            }
        };
    }

    private Process createAndStartProcess(String command) throws IOException {
        ProcessBuilder pb = createOsSpecificProcessBuilder(command);
        return pb.start();
    }

    private void waitForProcessExecutionAndSetResult(Process process) throws InterruptedException, IOException {
        exitCode = process.waitFor();
        stdOut = new String(ByteStreams.toByteArray(process.getInputStream()));
        stdErr = new String(ByteStreams.toByteArray(process.getErrorStream()));
    }

    @VisibleForTesting
    protected ProcessBuilder createOsSpecificProcessBuilder(String command) {
        return isWindows() ? new ProcessBuilder("cmd", "/c", command) : new ProcessBuilder("bash", "-c", command);
    }

    @VisibleForTesting
    protected static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    String getStdOut() {
        return stdOut;
    }

    String getStdErr() {
        return stdErr;
    }

    Integer getExitCode() {
        return exitCode;
    }

}
