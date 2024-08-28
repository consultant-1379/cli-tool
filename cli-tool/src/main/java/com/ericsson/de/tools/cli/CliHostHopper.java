package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.TargetHost;

/**
 * This class allows to establish ssh connection to the specified host
 */
@API(API.Quality.Experimental)
public class CliHostHopper {

    private final SshCliTool cliTool;

    public CliHostHopper(CliToolShell cliTool) {
        if (cliTool instanceof SshCliTool) {
            this.cliTool = (SshCliTool) cliTool;
        } else {
            throw new IllegalArgumentException(String.format("%s is invalid argument type. Only %s is allowed",cliTool.getClass().getCanonicalName(), SshCliTool.class.getCanonicalName()));
        }
    }

    /**
     * Allows user to ssh to a {@link TargetHost}
     *
     * @param targetHost {@link TargetHost} information in which connection will be made
     */
    public void hop(TargetHost targetHost) {
        SshLogin sshLogin = new SshLogin(cliTool, targetHost);
        sshLogin.login();
    }

}