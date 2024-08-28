package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.google.common.base.Preconditions;

/**
 * WaitCondition based on substring
 * */
@API(API.Quality.Experimental)
class SubstringWaitCondition implements WaitCondition {

    private final String substring;

    private final long timeout;

    SubstringWaitCondition(String substring, long timeout) {
        this.substring = substring;
        this.timeout = timeout;
    }

    @Override
    public String wait(Shell shell) {
        Preconditions.checkNotNull(shell, "shell is undefined");

        return shell.expect(substring, timeout);
    }
}
