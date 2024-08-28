package com.ericsson.de.tools.cli;

import com.ericsson.cifwk.meta.API;

import java.util.regex.Pattern;

/**
 * Factory class for the WaitCondition
 */
@API(API.Quality.Experimental)
public class WaitConditions {

    private WaitConditions() {
    }

    /**
     * Factory method for the substring wait condition
     * <p/>
     * @param substring
     * @return wait condition
     */
    public static WaitCondition substring(String substring) {
        return substring(substring, AbstractCliToolBuilder.DEFAULT_TIMEOUT);
    }

    /**
     * Factory method for the substring wait condition with timeout
     * <p/>
     * @param substring
     * @param timeout wait time
     * @return wait condition
     */
    public static WaitCondition substring(String substring, long timeout) {
        return new SubstringWaitCondition(substring, timeout);
    }

    /**
     * Factory method for the substring wait condition
     * <p/>
     * @param regEx regular expression in string format
     * @return wait condition
     */
    public static WaitCondition regularExpression(String regEx) {
        return regularExpression(regEx, AbstractCliToolBuilder.DEFAULT_TIMEOUT);
    }

    /**
     * Factory method for the regular expression wait condition with timeout
     * <p/>
     * @param regEx regular expression in string format
     * @param timeout wait time
     * @return wait condition
     */
    public static WaitCondition regularExpression(String regEx, long timeout) {
        return new RegexpWaitCondition(regEx, timeout);
    }

    /**
     * Factory method for the regular expression wait condition
     * <p/>
     * @param pattern compiled regular expression
     * @return wait condition
     */
    public static WaitCondition regularExpression(Pattern pattern) {
        return regularExpression(pattern, AbstractCliToolBuilder.DEFAULT_TIMEOUT);
    }

    /**
     * Factory method for the regular expression wait condition with timeout
     * <p/>
     * @param pattern compiled regular expression
     * @param timeout wait time
     * @return wait condition
     */
    public static WaitCondition regularExpression(Pattern pattern, long timeout) {
        return new RegexpWaitCondition(pattern, timeout);
    }
}
