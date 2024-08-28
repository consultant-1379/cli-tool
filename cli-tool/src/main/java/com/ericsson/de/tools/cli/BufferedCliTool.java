package com.ericsson.de.tools.cli;

public interface BufferedCliTool extends InterruptibleCommand {

    /**
     * Write shell command
     *
     * @param command to execute
     */
    void send(String command);

    /**
     * Returns the standard output, will also clear the buffer
     *
     * @return standard output
     */
    String getOutput();
}



