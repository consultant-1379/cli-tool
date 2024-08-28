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

public class SshCliToolHopTest {

    private SshShellBuilder builder;

    private CliToolShell cliTool;

    private String netsimIp;
    private String gatewayIp;

    @Before
    public void setUp() throws Exception {
        Host netsimHost = TafDataHandler.findHost().withHostname("netsim").get();
        User netsimUser = netsimHost.getUsers(UserType.ADMIN).get(0);
        Host gatewayHost = TafDataHandler.findHost().withHostname("gateway").get();
        User gatewayUser = gatewayHost.getUsers(UserType.ADMIN).get(0);

        builder = CliTools.sshShell(gatewayHost.getIp());
        builder.withUsername(gatewayUser.getUsername());
        builder.withPassword(gatewayUser.getPassword());
        cliTool = builder.build();
        cliTool.hopper().hop(new TargetHost(netsimUser.getUsername(), netsimUser.getPassword(), netsimHost.getIp(), netsimHost.getPort(Ports.SSH)));

        this.netsimIp = netsimHost.getIp();
        this.gatewayIp = gatewayHost.getIp();
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_013")
    public void successfulHop() {
        assertThat(cliTool.execute(String.format("ifconfig | grep '%s'", netsimIp)).getOutput()).contains(netsimIp);
    }

    @Test
    @TestId(id = "TAF_CLI_TOOL2_014")
    public void connectionViaMultipleHosts() {
        Host ms1 = TafDataHandler.findHost().withHostname("ms1").get();
        User user = ms1.getUsers(UserType.ADMIN).get(0);
        cliTool.hopper().hop(new TargetHost(user.getUsername(), user.getPassword(), ms1.getIp(), ms1.getPort(Ports.SSH)));

        assertThat(cliTool.writeLine("exit", WaitConditions.substring("Connection to")).getOutput()).contains(ms1.getIp());
        assertThat(cliTool.writeLine("exit", WaitConditions.substring("Connection to")).getOutput()).contains(netsimIp);
        assertThat(cliTool.execute(String.format("ifconfig | grep '%s'", gatewayIp)).getOutput()).contains(gatewayIp);
    }

    @Test
    public void successfulHopSetNewHost() {
        assertThat(cliTool.execute(String.format("ifconfig | grep '%s'", netsimIp)).getOutput()).contains(netsimIp);
    }


    @After
    public void tearDown() {
        if (cliTool != null) {
            cliTool.close();
        }
    }
}
