package com.codechallenge.bank.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * This class is intended to be a utility class with some methods common to {@link LocalDate}
 *
 * @author Carlos Rodriguez
 * @since 27/09/2019
 */
public class LocalDateUtils {

    /**
     * Convert the given {@link Date} to {@link LocalDate}
     * @param date the {@link Date} to be converted
     * @return the {@link LocalDate}
     */
    public static LocalDate getLocalDateFromDate(final Date date){
        return (date != null) ? LocalDate.from(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())) : null;
    }
}