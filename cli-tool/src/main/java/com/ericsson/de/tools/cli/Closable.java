package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

@API(API.Quality.Experimental)
interface Closable {

    void close();

}