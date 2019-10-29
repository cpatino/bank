package com.codechallenge.bank.util;

import com.codechallenge.bank.exception.DataNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class BankControllerAdviceTest {

    private final BankControllerAdvice bankControllerAdvice = new BankControllerAdvice();

    @Test
    public void dataNotFoundHandler_checkMessageIsDelivered() {
        DataNotFoundException exception = new DataNotFoundException("type", "abc");
        String message = bankControllerAdvice.dataNotFoundHandler(exception);
        assertEquals("Could not find type using abc", message);
    }
}