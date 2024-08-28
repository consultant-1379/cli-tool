package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

/**
 * This class allows to build a CliToolShell instance
 */
@API(API.Quality.Experimental)
abstract class AbstractCliToolBuilder<C extends CliTool, B extends AbstractCliToolBuilder> {

    protected static final long DEFAULT_TIMEOUT = 15;

    private Long defaultTimeout;

    protected AbstractCliToolBuilder() {
    }

    /**
     * Add default timeout. If value not set then default timeout value is {@value AbstractCliToolBuilder#DEFAULT_TIMEOUT)
     *
     * @param timeout
     * @return specified builder <code>B</code>
     */

    @SuppressWarnings("unchecked")
    public B withDefaultTimeout(Long timeout) {
        this.defaultTimeout = timeout;
        return (B) this;
    }

    protected long timeout() {
        return defaultTimeout == null ? DEFAULT_TIMEOUT : defaultTimeout;
    }

    /**
     * Build the specified {@link CliTool} instance
     *
     * @return specified {@link CliTool} object
     */
    public abstract C build();
}
