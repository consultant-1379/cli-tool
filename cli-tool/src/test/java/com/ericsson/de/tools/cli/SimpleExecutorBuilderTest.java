package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.tools.TargetHost;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SimpleExecutorBuilderTest {

    @Test
    public void testTunnelPathMustBeEmptyIfTunnelHostIsNotSet() {
        SimpleExecutorBuilder builder = CliTools.simpleExecutor("destinationHost");
        assertThat(builder.prepareObjectForTunneling()).isNull();
    }

    @Test
    public void testTunnelPathIsCorrect() {
        SimpleExecutorBuilder builder = CliTools.simpleExecutor("destinationHost");
        builder.withUsername("destinationHostUsername");
        builder.withPassword("destinationHostPassword");
        builder.withPort(999);
        builder.withKeyFile("destinationHostKeyFile");
        builder.withStrictHostKeyChecking(true);

        builder.withTunnelHost(new TargetHost("tunnelUsername", "tunnelUserPassword", "tunnelHost", 888, "tunnelSshPrivateKeyFile", true));

        TargetHost tunnelHost = builder.prepareObjectForTunneling();

        assertThat(tunnelHost).isNotNull();
        assertTunnelNode(tunnelHost, "destinationHost", "destinationHostPassword", "destinationHostUsername", 999, true, "destinationHostKeyFile");
        assertDestinationHost(builder, "tunnelHost", "tunnelUserPassword", "tunnelUsername", 888, true, "tunnelSshPrivateKeyFile");
    }

    private void assertTunnelNode(TargetHost tunnelHost, String host, String password, String username, int port, boolean enforceStrictHostKeyChecking, String keyFile) {
        assertThat(tunnelHost.getHost()).isEqualTo(host);
        assertThat(tunnelHost.getPassword()).isEqualTo(password);
        assertThat(tunnelHost.getUsername()).isEqualTo(username);
        assertThat(tunnelHost.getPort()).isEqualTo(port);
        assertThat(tunnelHost.isEnforceStrictHostKeyChecking()).isEqualTo(enforceStrictHostKeyChecking);
        assertThat(tunnelHost.getPathToSshPrivateKeyFile()).isEqualTo(keyFile);
    }

    private void assertDestinationHost(SimpleExecutorBuilder builder, String host, String password, String username, int port, boolean enforceStrictHostKeyChecking, String keyFile) {
        assertThat(builder.getHost()).isEqualTo(host);
        assertThat(builder.getPassword()).isEqualTo(password);
        assertThat(builder.getUsername()).isEqualTo(username);
        assertThat(builder.getPort()).isEqualTo(port);
        assertThat(builder.isEnforceStrictHostKeyChecking()).isEqualTo(enforceStrictHostKeyChecking);
        assertThat(builder.getPathToSshPrivateKeyFile()).isEqualTo(keyFile);
    }
}
