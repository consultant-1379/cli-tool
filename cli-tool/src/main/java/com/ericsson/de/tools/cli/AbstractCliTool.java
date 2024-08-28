package com.ericsson.de.tools.cli;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.ericsson.cifwk.meta.API;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@API(API.Quality.Experimental)
abstract class AbstractCliTool implements CliTool {

    protected static final String JSCH_EXIT_CODE_MARKER = "taf.jsch.exitcode";
    protected static final String JSCH_EXIT_CODE_MARKER_TUNER = "taf.jsch.exitcode=$?";
    protected static final String CHANGED_PROMPT_SUBSTRING = "PRIMARY_PROMPT>#";
    protected static final String UNASSIGNED_JSCH_EXIT_CODE_MATCHER = "[\\S\\s]*?=[\\S\\s]*?\\$[\\S\\s]*?\\?[\\S\\s]*?\"";

    private static final String CHANGE_PROMPT_SUBSTRING_QUOTED = Pattern.quote(CHANGED_PROMPT_SUBSTRING);
    private static final String JSCH_EXIT_CODE_MARKER_QUOTED = Pattern.quote(JSCH_EXIT_CODE_MARKER);
    private static final String CHANGED_PROMPT_SUBSTRING_QUOTED = Pattern.quote(CHANGED_PROMPT_SUBSTRING);


    protected static final String NEWLINE_SEPARATOR = "\n";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCliTool.class);

    protected final CLI cli;
    protected final long defaultTimeout;
    private String host;

    AbstractCliTool(CLI cli, long defaultTimeout, String host) {
        this.cli = cli;
        this.defaultTimeout = defaultTimeout;
        this.host = host;
    }

    protected long calculateExecutionTime(long startTime, long endTime) {
        Preconditions.checkState(endTime >= startTime, "'endTime' must be greater than 'startTime'");
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

    protected String filterOutput(String output, String command) {
        String filteredExitCodeMarkerOutput;
        long startTime = System.nanoTime();
        if (output.contains(JSCH_EXIT_CODE_MARKER)) {
            String filteredExitCodeMarker = output.replaceFirst(UNASSIGNED_JSCH_EXIT_CODE_MATCHER, "");
            filteredExitCodeMarkerOutput = filteredExitCodeMarker;
            if(filteredExitCodeMarker.contains(JSCH_EXIT_CODE_MARKER_TUNER)){
                filteredExitCodeMarkerOutput = filteredExitCodeMarker.replaceFirst(UNASSIGNED_JSCH_EXIT_CODE_MATCHER, "");
            }
        } else {
            filteredExitCodeMarkerOutput = output;
        }
        long endTime = System.nanoTime();
        LOGGER.info("Time taken to filter out the actual output:: " + calculateExecutionTime(startTime, endTime) + "millis");
        calculateExecutionTime(startTime, endTime);
        String quotedCommand = Pattern.quote(command);
        String filterRegEx = String.format("^%s|%s|%s=\\d|^.*%s|\\r", CHANGED_PROMPT_SUBSTRING_QUOTED + quotedCommand, CHANGE_PROMPT_SUBSTRING_QUOTED, JSCH_EXIT_CODE_MARKER_QUOTED, quotedCommand);
        String filteredOutput = filteredExitCodeMarkerOutput.replaceAll(filterRegEx, "").trim();
        return removeFirstAndLastNewLineSymbol(filteredOutput);
    }

    private String removeFirstAndLastNewLineSymbol(String output) {
        return StringUtils.removeStart(StringUtils.removeEnd(output, NEWLINE_SEPARATOR), NEWLINE_SEPARATOR);
    }

    void setCurrentHost(String host) {
        this.host = Preconditions.checkNotNull(host, "'host' is undefined");
    }

    String getCurrentHost() {
        return host;
    }

    @Override
    public void close() {
        cli.close();
    }

}
