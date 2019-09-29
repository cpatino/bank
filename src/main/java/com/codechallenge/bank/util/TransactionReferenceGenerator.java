package com.codechallenge.bank.util;

import java.util.Random;

/**
 * This class is intended to generate a random reference for the transactions that not provide one.
 *
 * @author Carlos Rodriguez
 * @since 26/07/2019
 */
public class TransactionReferenceGenerator {

    /**
     * Generates the random reference.
     * @return A random reference with format (12345A)
     */
    public static String generate() {
        return generateRandomInt() + generateRandomChar();
    }

    /**
     * Generates a random integer between 1 to 20000
     * @return A random integer
     */
    private static int generateRandomInt() {
        return new Random().nextInt(20000);
    }

    /**
     * Generates a random String with one char from A-Z
     * @return A random String
     */
    private static String generateRandomChar() {
        return Character.toString(((char) (new Random().nextInt(26) + 'a'))).toUpperCase();
    }
}