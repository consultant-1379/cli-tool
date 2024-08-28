package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.de.tools.cli.LocalCliTool;
import org.junit.Test;

import static com.ericsson.de.tools.cli.LocalCommandRuntimeExecutor.isWindows;
import static com.google.common.truth.Truth.assertThat;

public class LocalCliToolTest {

    private static final long DEFAULT_TIMEOUT = 5;
    private final LocalCliTool cliTool = new LocalCliTool(DEFAULT_TIMEOUT);

    @Test
    public void testStoreCommandOutputCorrectly() {
        CliCommandResult result = cliTool.execute("echo hello");
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutput()).isEqualTo("hello" + System.lineSeparator());
    }

    @Test
    public void testProcessCommandWithArgsCorrectly() {
        CliCommandResult result = cliTool.execute("ls -artl");
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutput()).contains("total");
    }

    @Test
    public void testReturnFalseForInvalidCommand() {
        CliCommandResult result = cliTool.execute("eco hello");
        assertThat(result.isSuccess()).isFalse();
    }

    @Test(expected = TimeoutException.class)
    public void testTimeout() {
        if (isWindows()) {
            cliTool.execute("waitfor SomethingThatIsNeverHappening /t 3", 2);
        } else {
            cliTool.execute("sleep 3", 2);
        }
    }
}
