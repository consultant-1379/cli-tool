package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

/**
 * This class allows to build a {@link CliTool} instance
 */
@API(API.Quality.Experimental)
public class LocalCliToolBuilder extends AbstractCliToolBuilder<CliTool, LocalCliToolBuilder> {

    @Override
    public CliTool build() {
        return new LocalCliTool(timeout());
    }
}
