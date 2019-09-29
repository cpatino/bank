package com.codechallenge.bank.util;

/**
 * This class is intended to be a utility class with some methods common to {@link StringBuilder}
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
public class StringBuilderUtils {

    /**
     * Append the given message to the {@link StringBuilder} passed as parameter.
     *
     * @param builder the {@link StringBuilder} being used
     * @param message the message to be append
     */
    public static void append(final StringBuilder builder, final String message) {
        if (builder != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            if (message != null) {
                builder.append(message);
            }
        }
    }
}