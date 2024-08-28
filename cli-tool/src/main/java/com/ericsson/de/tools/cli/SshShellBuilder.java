package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.cifwk.taf.tools.cli.CLI;

/**
 * This class allows to build a {@link CliToolShell} instance
 */
@API(API.Quality.Experimental)
public class SshShellBuilder extends AbstractGenericSshCliToolBuilder<CliToolShell, SshShellBuilder> {

    /**
     * Create {@link SshShellBuilder} with target host using host address
     */
    public SshShellBuilder(String host) {
        super(host);
    }

    @Override
    protected TargetHost prepareObjectForTunneling() {
        return null;
    }

    @Override
    protected CliToolShell builds(CLI cli, String currentHost) {
        final CliToolShell cliTool = new SshCliTool(cli, timeout(), currentHost);
        cliTool.writeLine(SshCliTool.CHANGED_PROMPT_COMMAND, 1);
        return cliTool;
    }

}