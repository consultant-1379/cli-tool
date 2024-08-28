package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.google.common.annotations.VisibleForTesting;

/**
 * This class allows to build a {@link CliTool} instance
 */
@API(API.Quality.Experimental)
public class SimpleExecutorBuilder extends AbstractGenericSshCliToolBuilder<CliTool, SimpleExecutorBuilder> {

    private TargetHost tunnelNode;
    private boolean enforceStrictHostKeyChecking;
    private String pathToSshPrivateKeyFile;

    /**
     * Create {@link SimpleExecutorBuilder} with target host using host address
     */
    public SimpleExecutorBuilder(String host) {
        super(host);
    }

    /**
     * Establish connection via tunnel using {@link TargetHost} object. Can be used only once otherwise value will be reassigned
     * <p>
     * Example:<br/>
     * <code>
     * {@link SimpleExecutorBuilder} builder = {@link CliTools}.simpleExecutor({@link Host});<br/>
     * builder.withTunnelHost({@link TargetHost});<br/>
     * builder.build();
     * </code>
     * <br/
     * Result
     * <p/>
     *
     * @param tunnelNode
     * @return {@link SimpleExecutorBuilder}
     */
    public SimpleExecutorBuilder withTunnelHost(TargetHost tunnelNode) {
        this.tunnelNode = tunnelNode;
        return this;
    }

    /**
     * Provide connection to a host with full path to ssh private key file. Must be used when necessary authentication with private key. If this method will be used then the username is unnecessary
     *
     * @param pathToSshPrivateKeyFile full path to ssh private key file
     * @return {@link SimpleExecutorBuilder}
     */
    public SimpleExecutorBuilder withKeyFile(String pathToSshPrivateKeyFile) {
        this.pathToSshPrivateKeyFile = pathToSshPrivateKeyFile;
        return this;
    }

    /**
     * Enforce strict host key checking
     *
     * @param enforceStrictHostKeyChecking default value is false
     * @return {@link SimpleExecutorBuilder}
     */
    public SimpleExecutorBuilder withStrictHostKeyChecking(boolean enforceStrictHostKeyChecking) {
        this.enforceStrictHostKeyChecking = enforceStrictHostKeyChecking;
        return this;
    }

    @Override
    protected TargetHost prepareObjectForTunneling() {
        final TargetHost tunnelHost;
        if (tunnelNode != null) {
            tunnelHost = new TargetHost(getUsername(), getPassword(), getHost(), defaultPort(getPort()), pathToSshPrivateKeyFile, enforceStrictHostKeyChecking);
            tunnelToHostParams(tunnelNode);
        } else {
            tunnelHost = null;
        }
        return tunnelHost;
    }

    @VisibleForTesting
    protected void tunnelToHostParams(TargetHost targetHost) {
        setHost(targetHost.getHost());
        setUsername(targetHost.getUsername());
        setPassword(targetHost.getPassword());
        setPort(defaultPort(targetHost.getPort()));
        setPathToSshPrivateKeyFile(targetHost.getPathToSshPrivateKeyFile());
        setEnforceStrictHostKeyChecking(targetHost.isEnforceStrictHostKeyChecking());
    }

    @VisibleForTesting
    protected boolean isEnforceStrictHostKeyChecking() {
        return enforceStrictHostKeyChecking;
    }

    protected void setEnforceStrictHostKeyChecking(boolean enforceStrictHostKeyChecking) {
        this.enforceStrictHostKeyChecking = enforceStrictHostKeyChecking;
    }

    @VisibleForTesting
    protected String getPathToSshPrivateKeyFile() {
        return pathToSshPrivateKeyFile;
    }

    protected void setPathToSshPrivateKeyFile(String pathToSshPrivateKeyFile) {
        this.pathToSshPrivateKeyFile = pathToSshPrivateKeyFile;
    }

    @Override
    protected CliTool builds(CLI cli, String currentHost) {
        return new SimpleExecutor(cli, timeout(), currentHost);
    }
}
