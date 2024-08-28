package com.ericsson.de.tools.cli;

import com.ericsson.de.tools.cli.SshShellBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class CliToolBuilderTest {

    private static final String FIRST_HOST = "1";

    private SshShellBuilder builder;

    @Before
    public void init() {
        builder = new SshShellBuilder(FIRST_HOST);
    }

    @Test
    public void testDefaultPortIfPortIsSet() {
        assertThat(builder.defaultPort(8080)).isEqualTo(8080);
    }

    @Test
    public void testDefaultPortIfPortIsNotSet() {
        assertThat(builder.defaultPort(null)).isEqualTo(SshShellBuilder.DEFAULT_PORT);
    }

}
