package com.ericsson.de.tools.cli.examples;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.de.tools.cli.CliTool;
import com.ericsson.de.tools.cli.CliTools;
import com.ericsson.de.tools.cli.SimpleExecutorBuilder;
import org.junit.Test;

public class SimpleExecutorExamples {

    /*
     * Instantiation
     */
    @Test
    public void instantiationUsingHostname() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: SIMPLE_EXECUTOR_INSTANTIATION_USING_HOSTNAME
        CliTool simpleExecutor = CliTools.simpleExecutor(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();
        // END SNIPPET: SIMPLE_EXECUTOR_INSTANTIATION_USING_HOSTNAME

        simpleExecutor.close();
    }

    /*
     * Executing commands
     */
    @Test
    public void commandExecution() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: SIMPLE_EXECUTOR_COMMAND_EXECUTION
        CliTool simpleExecutor = CliTools.simpleExecutor(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();

        simpleExecutor.execute("ls -a");
        // END SNIPPET: SIMPLE_EXECUTOR_COMMAND_EXECUTION

        simpleExecutor.close();
    }

    /*
     * Getting result
     */
    @Test
    public void cliCommandResult() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: SIMPLE_EXECUTOR_COMMAND_RESULT
        CliTool simpleExecutor = CliTools.simpleExecutor(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();
        CliCommandResult result = simpleExecutor.execute("ls -a");

        String output = result.getOutput();
        int exitCode = result.getExitCode();
        long executionTime = result.getExecutionTime();
        boolean isSuccess = result.isSuccess();
        // END SNIPPET: SIMPLE_EXECUTOR_COMMAND_RESULT
    }

    /*
     * Closing connection
     */
    @Test
    public void closingConnection() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: CLOSING_CONNECTION
        CliTool cliTool = CliTools.simpleExecutor(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();
        cliTool.close();
        // END SNIPPET: CLOSING_CONNECTION
    }

    /*
    * Complete Examples
    */
    @Test
    public void completeExample() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: SIMPLE_EXECUTOR_COMPLETE_EXAMPLE
        CliTool cliTool = CliTools.simpleExecutor(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();
        CliCommandResult result = cliTool.execute("whoami");

        String output = result.getOutput();
        int exitCode = result.getExitCode();

        cliTool.close();
        // END SNIPPET: SIMPLE_EXECUTOR_COMPLETE_EXAMPLE
    }

    /*
     * Tunneling
     */
    @Test
    public void tunneling() {
        Host gateway = DataHandler.getHostByName("gateway");
        User gateWayUser = gateway.getUsers(UserType.ADMIN).get(0);
        String gatewayHostAddress = gateway.getIp();
        String gatewayUsername = gateWayUser.getUsername();
        String gatewayPassword = gateWayUser.getPassword();
        int gatewayPort = gateway.getPort(Ports.SSH);

        Host destinationHost = DataHandler.getHostByName("netsim");
        User user = destinationHost.getUsers(UserType.ADMIN).get(0);
        String destinationHostAddress = destinationHost.getIp();
        String destinationUsername = user.getUsername();
        String destinationPassword = user.getPassword();

        // START SNIPPET: TUNNELING_EXAMPLE
        SimpleExecutorBuilder builder = CliTools.simpleExecutor(destinationHostAddress).withUsername(destinationUsername).withPassword(destinationPassword);
        builder.withTunnelHost(new TargetHost(gatewayUsername, gatewayPassword, gatewayHostAddress, gatewayPort));
        CliTool cliTool = builder.build();

        cliTool.execute("ls");
        // END SNIPPET: TUNNELING_EXAMPLE
    }


}
