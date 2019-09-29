package com.codechallenge.bank.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Carlos Rodriguez
 * @since 26/09/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class StringBuilderUtilsTest {

    @Test
    public void append_whenNullBuilder() {
        StringBuilder builder = null;
        StringBuilderUtils.append(builder, "test");
        assertNull(builder);
    }

    @Test
    public void append_whenNullMessage() {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.append(builder, null);
        assertEquals("", builder.toString());
    }

    @Test
    public void append_whenOneMessage() {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.append(builder, "test");
        assertEquals("test", builder.toString());
    }

    @Test
    public void append_whenMultipleMessages() {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.append(builder, "test");
        StringBuilderUtils.append(builder, "other test");
        assertEquals("test, other test", builder.toString());
    }
}