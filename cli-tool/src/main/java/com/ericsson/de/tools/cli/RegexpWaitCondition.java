package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * WaitCondition based on regex
 * */
@API(API.Quality.Experimental)
class RegexpWaitCondition implements WaitCondition {

    private final Pattern regexp;

    private final long timeout;

    RegexpWaitCondition(String regexp, long timeout) {
        this.regexp = Pattern.compile(regexp);
        this.timeout = timeout;
    }

    RegexpWaitCondition(Pattern regexp, long timeout) {
        this.regexp = regexp;
        this.timeout = timeout;
    }

    @Override
    public String wait(Shell shell) {
        Preconditions.checkNotNull(shell, "shell is undefined");
        return shell.expect(regexp, timeout);
    }

}
