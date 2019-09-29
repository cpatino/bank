package com.codechallenge.bank.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Carlos Rodriguez
 * @since 26/07/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionReferenceGeneratorTest {

    @Test
    public void generate_checkNotNull() {
        String reference = TransactionReferenceGenerator.generate();
        assertNotNull(reference);
    }
}