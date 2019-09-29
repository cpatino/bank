package com.codechallenge.bank.exception;

/**
 * Exception to be thrown when one of the parameters is not valid.
 *
 * @author Carlos Rodriguez
 * @since 25/09/2019
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}