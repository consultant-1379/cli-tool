package com.ericsson.de.tools.cli.examples;

import org.junit.Test;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.TargetHost;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.de.tools.cli.BufferedCliTool;
import com.ericsson.de.tools.cli.CliIntermediateResult;
import com.ericsson.de.tools.cli.CliToolShell;
import com.ericsson.de.tools.cli.CliTools;
import com.ericsson.de.tools.cli.WaitConditions;

public class CliToolShellExamples {

    private static final String INTERACTIVE_SCRIPT_NAME = "cli-tool-complete-example.sh";

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

        // START SNIPPET: CLI_TOOL_SHELL_INSTANTIATION_USING_HOSTNAME
        CliToolShell shell = CliTools.sshShell(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();
        // END SNIPPET: CLI_TOOL_SHELL_INSTANTIATION_USING_HOSTNAME

        shell.close();
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

        // START SNIPPET: CLI_TOOL_SHELL_COMMAND_EXECUTION
        CliToolShell shell = CliTools.sshShell(hostAddress)
                .withUsername(username)
                .withPassword(password)
                .build();

        shell.execute("ls -a");
        // END SNIPPET: CLI_TOOL_SHELL_COMMAND_EXECUTION

        shell.close();
    }

    @Test
    public void interactiveCommand() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        CliToolShell shell = CliTools.sshShell(hostAddress).withUsername(username).withPassword(password).build();
        shell.execute("useradd cli_tool_example_user");

        // START SNIPPET: CLI_TOOL_SHELL_INTERACTIVE_COMMAND_EXECUTION
        shell.writeLine("passwd cli_tool_example_user", WaitConditions.substring("New Password", 20));
        shell.writeLine("new_password", WaitConditions.substring("Reenter New Password"));
        // END SNIPPET: CLI_TOOL_SHELL_INTERACTIVE_COMMAND_EXECUTION

        shell.close();
    }

    /*
     * Getting result
     */
    @Test
    public void cliIntermediateResult() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: CLI_INTERMEDIATE_RESULT
        CliToolShell shell = CliTools.sshShell(hostAddress).withUsername(username).withPassword(password).build();
        CliIntermediateResult result = shell.writeLine("ls -a");

        String output = result.getOutput();
        // END SNIPPET: CLI_INTERMEDIATE_RESULT
    }

    /*
     * Complete Examples
     * */
    @Test
    public void completeExample() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        CliToolShell shell = CliTools.sshShell(hostAddress).withUsername(username).withPassword(password).build();
        uploadInteractiveScript(shell, host);

        // START SNIPPET: SHELL_COMPLETE_EXAMPLE
        // Execute the script test.sh and WaitCondition which checks the standard output for prompt string
        shell.writeLine("./" + INTERACTIVE_SCRIPT_NAME, WaitConditions.substring("Enter your name:"));
        // Provide the input to the script prompt
        shell.writeLine("Joe", WaitConditions.substring("Enter your age:"));
        shell.writeLine("28", WaitConditions.substring("Enter your date of birth"));
        //In this case command is finalized but exit code is unavailable
        CliIntermediateResult result = shell.writeLine("14-02-1986");

        String output = result.getOutput();

        //Close SSH connection
        shell.close();
        // END SNIPPET: SHELL_COMPLETE_EXAMPLE
    }

    private void uploadInteractiveScript(CliToolShell shell, Host host) {
        RemoteObjectHandler remoteObjectHandler = new RemoteObjectHandler(host, host.getUsers(UserType.ADMIN).get(0));
        if (!remoteObjectHandler.remoteFileExists("/root/" + INTERACTIVE_SCRIPT_NAME)) {
            remoteObjectHandler.copyLocalFileToRemote("scripts/" + INTERACTIVE_SCRIPT_NAME, "/root/" + INTERACTIVE_SCRIPT_NAME);
        }
        shell.execute("chmod +x " + INTERACTIVE_SCRIPT_NAME);
    }

    /*
     * CliHostHopper
     */
    @Test
    public void cliHostHopper() {
        Host gateway = DataHandler.getHostByName("gateway");
        User gatewayUser = gateway.getUsers(UserType.ADMIN).get(0);
        String gatewayHostAddress = gateway.getIp();
        String gatewayUsername = gatewayUser.getUsername();
        String gatewayPassword = gatewayUser.getPassword();

        Host destinationHost = DataHandler.getHostByName("netsim");
        User user = destinationHost.getUsers(UserType.ADMIN).get(0);
        String destinationHostAddress = destinationHost.getIp();
        String destinationUsername = user.getUsername();
        String destinationPassword = user.getPassword();
        int destinationPort = gateway.getPort(Ports.SSH);

        // START SNIPPET: HOST_HOPPER_ONE_LEVEL
        CliToolShell shell = CliTools.sshShell(gatewayHostAddress).withUsername(gatewayUsername).withPassword(gatewayPassword).build();
        shell.hopper().hop(new TargetHost(destinationUsername, destinationPassword, destinationHostAddress, destinationPort));

        shell.execute("whoami");
        // END SNIPPET: HOST_HOPPER_ONE_LEVEL
    }

    @Test
    public void multipleCliHostHopper() {
        Host gateway = DataHandler.getHostByName("gateway");
        User gatewayUser = gateway.getUsers(UserType.ADMIN).get(0);
        String gatewayHostAddress = gateway.getIp();
        String gatewayUsername = gatewayUser.getUsername();
        String gatewayPassword = gatewayUser.getPassword();

        Host destinationHost = DataHandler.getHostByName("netsim");
        User user = destinationHost.getUsers(UserType.ADMIN).get(0);
        String destinationHostAddress = destinationHost.getIp();
        String destinationUsername = user.getUsername();
        String destinationPassword = user.getPassword();
        int destinationPort = gateway.getPort(Ports.SSH);

        Host ms1Host = DataHandler.getHostByName("ms1");
        User ms1User = destinationHost.getUsers(UserType.ADMIN).get(0);
        String ms1HostAddress = ms1Host.getIp();
        String ms1Username = ms1User.getUsername();
        String ms1Password = ms1User.getPassword();
        int ms1Port = ms1Host.getPort(Ports.SSH);

        // START SNIPPET: HOST_HOPPER_MULTIPLE_HOP
        CliToolShell shell = CliTools.sshShell(gatewayHostAddress).withUsername(gatewayUsername).withPassword(gatewayPassword).build();
        shell.hopper().hop(new TargetHost(ms1Username, ms1Password, ms1HostAddress, ms1Port));
        shell.hopper().hop(new TargetHost(destinationUsername, destinationPassword, destinationHostAddress, destinationPort));

        shell.execute("whoami");
        // END SNIPPET: HOST_HOPPER_MULTIPLE_HOP
    }

    @Test
    public void CliBufferedExample() {
        Host host = DataHandler.getHostByName("netsim");
        User user = host.getUsers(UserType.ADMIN).get(0);
        String hostAddress = host.getIp();
        String username = user.getUsername();
        String password = user.getPassword();

        // START SNIPPET: CLI_TOOL_SHELL_BUFFERED_SHELL
        CliToolShell shell = CliTools.sshShell(hostAddress).withUsername(username).withPassword(password).build();
        BufferedCliTool bufferedShell = shell.getBufferedShell();
        bufferedShell.send("top");
        String output = bufferedShell.getOutput();

        bufferedShell.interrupt();

        // Close shell connection
        shell.close();
        // END SNIPPET: CLI_TOOL_SHELL_BUFFERED_SHELL

    }

}
