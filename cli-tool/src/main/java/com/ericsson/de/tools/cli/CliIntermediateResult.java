package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

/**
 * This class represents command intermediate result
 * */
@API(API.Quality.Experimental)
public class CliIntermediateResult {
    private final String output;

    CliIntermediateResult(String output) {
        this.output = output;
    }

    /**
     * Return output
     *
     * @return output
     * */
    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "CliIntermediateResult{" +
                "output='" + output + '\'' +
                '}';
    }

}
