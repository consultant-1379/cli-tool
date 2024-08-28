package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.Shell;

/**
 * This interface allow configure wait conditions
 */
@API(API.Quality.Experimental)
public interface WaitCondition {

    /**
     * Wait for condition to become true
     * <p/>
     * @param shell
     * @return intermediate result
     */
    String wait(Shell shell);

}
