package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

/**
 * This class represents command result
 * */
@API(API.Quality.Experimental)
public class CliCommandResult {

    private final String output;
    private final int exitCode;
    private final long executionTime;

    /**
     * CliCommandResult constructor takes command execution result values
     *
     * @param stdOut command output
     * @param exitCode command exitCode
     * @param executionTime command execution time in millis
     * */
    CliCommandResult(String stdOut, int exitCode, long executionTime) {
        this.output = stdOut;
        this.exitCode = exitCode;
        this.executionTime = executionTime;
    }

    /**
     * Return output
     *
     * @return output
     * */
    public String getOutput() {
        return output;
    }

    /**
     * Return exit code
     *
     * @return exitCode
     * */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Return command success result
     *
     * @return <code>true</code> if command exit code equals to 0 or <code>false</code> if exit code not equals to 0
     * */
    public boolean isSuccess() {
       return exitCode == 0;
    }

    /**
     * Return execution time in millis
     *
     * @return execution time
     * */
    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        return "CliCommandResult{" +
                "output='" + output + '\'' +
                ", exitCode=" + exitCode +
                ", executionTime=" + executionTime +
                '}';
    }
}
