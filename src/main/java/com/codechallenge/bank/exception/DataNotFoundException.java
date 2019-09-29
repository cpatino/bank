package com.codechallenge.bank.exception;

/**
 * Exception to be thrown when the data is not found in the database.
 *
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
public class DataNotFoundException  extends RuntimeException{

    public DataNotFoundException(final String objectType, final String id) {
        super("Could not find " + objectType + " using " + id);
    }
}