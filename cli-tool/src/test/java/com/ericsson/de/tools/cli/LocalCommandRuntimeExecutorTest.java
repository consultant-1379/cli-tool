package com.ericsson.de.tools.cli;

import com.ericsson.de.tools.cli.LocalCommandRuntimeExecutor;
import org.junit.Test;

import java.util.List;

import static com.ericsson.de.tools.cli.LocalCommandRuntimeExecutor.isWindows;
import static org.assertj.core.api.Assertions.assertThat;

public class LocalCommandRuntimeExecutorTest {

    private static final String TEST_COMMAND = "test command -a";

    private final LocalCommandRuntimeExecutor executor = new LocalCommandRuntimeExecutor(TEST_COMMAND, 5);

    @Test
    public void testOsSpecificCommandConverter() {
        List<String> command = executor.createOsSpecificProcessBuilder(TEST_COMMAND).command();
        if (isWindows()) {
            assertThat(command.get(0)).isEqualTo("cmd");
            assertThat(command.get(1)).isEqualTo("/c");
            assertThat(command.get(2)).isEqualTo(TEST_COMMAND);
        } else {
            assertThat(command.get(0)).isEqualTo("bash");
            assertThat(command.get(1)).isEqualTo("-c");
            assertThat(command.get(2)).isEqualTo(TEST_COMMAND);
        }
    }
}
