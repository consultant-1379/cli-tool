package com.ericsson.de.tools.cli.examples;

import com.ericsson.de.tools.cli.CliTool;
import com.ericsson.de.tools.cli.CliTools;
import org.junit.Test;

public class LocalCliToolExamples {

    /*
     * Instantiation
     */
    @Test
    public void instantiation() {
        // START SNIPPET: LOCAL_CLI_TOOL_INSTANTIATION
        CliTool localCliTool = CliTools.local().build();
        // END SNIPPET: LOCAL_CLI_TOOL_INSTANTIATION
    }

    /*
     * Executing commands
     */
    @Test
    public void commandExecution() {
        // START SNIPPET: LOCAL_CLI_TOOL_COMMAND_EXECUTION
        CliTool localCliTool = CliTools.local().build();

        localCliTool.execute("ls -a");
        // END SNIPPET: LOCAL_CLI_TOOL_COMMAND_EXECUTION
    }
}
