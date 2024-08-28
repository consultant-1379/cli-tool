package com.ericsson.de.tools.cli;


public class BufferedShellCliTool implements BufferedCliTool {

    private final SshCliTool cliTool;

    public BufferedShellCliTool(SshCliTool cliTool) {
        this.cliTool = cliTool;
    }

    @Override
    public void send(String command) {
        cliTool.interrupt();
        cliTool.shell.writeln(command);
    }

    @Override
    public String getOutput() {
        return cliTool.shell.read();
    }

    @Override
    public void interrupt() {
        cliTool.interrupt();
    }
}
