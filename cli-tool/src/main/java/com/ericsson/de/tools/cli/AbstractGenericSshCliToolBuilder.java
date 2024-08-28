package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

@API(API.Quality.Experimental)
abstract class AbstractGenericSshCliToolBuilder<C extends CliTool, B extends AbstractCliToolBuilder> extends AbstractCliToolBuilder<C, B> {
    public static final int DEFAULT_PORT = 22;

    private String username;
    private String password;
    private String host;
    private Integer port;
    private Path keyFile;

    AbstractGenericSshCliToolBuilder(String targetHost) {
        this.host = targetHost;
    }

    /**
     * Add username
     * <p/>
     *
     * @param username
     * @return {@link SshShellBuilder}
     */
    @SuppressWarnings("unchecked")
    public B withUsername(String username) {
        this.username = username;
        return (B) this;
    }

    /**
     * Add user password
     * <p/>
     *
     * @param password
     * @return {@link SshShellBuilder}
     */
    @SuppressWarnings("unchecked")
    public B withPassword(String password) {
        this.password = password;
        return (B) this;
    }

    /**
     * Add ssh key file for public key authentication
     * @param keyFile
     * @return
     */
    public B withKeyFile(Path keyFile){
        this.keyFile = keyFile;
        return (B) this;
    }


    /**
     * Add host connection port. If value not set then default port value is {@value #DEFAULT_PORT}
     *
     * @param port
     * @return {@link SshShellBuilder }
     */
    @SuppressWarnings("unchecked")
    public B withPort(int port) {
        this.port = port;
        return (B) this;
    }

    @Override
    public C build() {
        TargetHost tunnelHost = prepareObjectForTunneling();
        Integer defaultPort = defaultPort(port);
        checkRequiredParameters(username, password, host, defaultPort);
        CLI cli = getCli(tunnelHost, defaultPort);
        return builds(cli, host);
    }

    private CLI getCli(final TargetHost tunnelHost, final Integer defaultPort) {
        CLI cli;
        if (keyFile == null) {
            cli = new CLI(host, defaultPort, username, password, tunnelHost, keyFile);
        } else {
            cli = new CLI(host, defaultPort, username, keyFile);
        }
        return cli;
    }

    protected void checkRequiredParameters(String username, String password, String host, Integer port) {
        Preconditions.checkNotNull(username, "'user' is undefined");
        Preconditions.checkNotNull(password, "'password' is undefined");
        Preconditions.checkNotNull(host, "'hostName' is undefined");
        Preconditions.checkState(StringUtils.isNotEmpty(host), "'host' is empty");
        Preconditions.checkNotNull(port, "'port' is undefined");
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getHost() {
        return host;
    }

    protected void setHost(String host) {
        this.host = host;
    }

    protected Integer getPort() {
        return port;
    }

    protected void setPort(Integer port) {
        this.port = port;
    }

    @VisibleForTesting
    int defaultPort(Integer port) {
        return port != null ? port : DEFAULT_PORT;
    }

    protected abstract TargetHost prepareObjectForTunneling();

    protected abstract C builds(com.ericsson.cifwk.taf.tools.cli.CLI cli, String currentHost);
}
