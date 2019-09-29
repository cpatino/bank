package com.codechallenge.bank.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalDateUtilsTest {

    @Test
    public void getLocalDateFromDate_whenNull() {
        assertNull(LocalDateUtils.getLocalDateFromDate(null));
    }

    @Test
    public void getLocalDateFromDate_whenNotNull() {
        LocalDate localDate = LocalDateUtils.getLocalDateFromDate(new Date());
        assertEquals(LocalDate.now(), localDate);
    }
}