package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

/**
 * Factory class for the CliTool builder
 * */
@API(API.Quality.Experimental)
public class CliTools {

    private CliTools() {}

    /**
     * Create {@link LocalCliToolBuilder} instance
     *
     * @return {@link LocalCliToolBuilder}
     */
    public static LocalCliToolBuilder local() {
        return new LocalCliToolBuilder();
    }

    /**
     * Create {@link SshShellBuilder} instance with target host using host address
     *
     * @param targetHost
     * @return {@link SshShellBuilder}
     */
    public static SshShellBuilder sshShell(String targetHost) {
        return new SshShellBuilder(targetHost);
    }

    /**
     * Create {@link SimpleExecutorBuilder} instance with target host using host address
     *
     * <br>
     * simpleExecutor is stateless tool for executing standalone commands, but is more performant then SshShell
     * <br/>
     *
     * @param targetHost
     * @return {@link SimpleExecutorBuilder}
     */
    public static SimpleExecutorBuilder simpleExecutor(String targetHost) {
        return new SimpleExecutorBuilder(targetHost);
    }

}
