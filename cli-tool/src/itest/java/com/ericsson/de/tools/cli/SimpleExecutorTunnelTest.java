package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.TargetHost;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SimpleExecutorTunnelTest {

    private CliTool cliTool;

    private String netsimIp;

    @Before
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        User user = host.getUsers(UserType.ADMIN).get(0);

        SimpleExecutorBuilder builder = CliTools.simpleExecutor(host.getIp()).withUsername(user.getUsername()).withPassword(user.getPassword());
        Host tunnelNodeHost = TafDataHandler.findHost().withHostname("gateway").get();
        User firstAdminUser = tunnelNodeHost.getFirstAdminUser();
        builder.withTunnelHost(new TargetHost(firstAdminUser.getUsername(), firstAdminUser.getPassword(), tunnelNodeHost.getIp(), tunnelNodeHost.getPort(Ports.SSH)));
        cliTool = builder.build();

        netsimIp = host.getIp();
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_019")
    public void connectionViaTunnel() {
        assertThat(cliTool.execute("ifconfig").getOutput()).contains(String.format("inet addr:%s", netsimIp));
    }

    @After
    public void tearDown() {
        cliTool.close();
    }
}
