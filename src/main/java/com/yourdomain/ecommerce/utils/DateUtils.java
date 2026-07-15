package com.yourdomain.ecommerce.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    public static final ZoneId DEFAULT_ZONE = ZoneOffset.UTC;
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtils() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }

    public static Instant toInstant(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atZone(DEFAULT_ZONE).toInstant();
    }

    public static String format(Instant instant) {
        return instant == null ? null : ISO_DATETIME.format(toLocalDateTime(instant));
    }
}
