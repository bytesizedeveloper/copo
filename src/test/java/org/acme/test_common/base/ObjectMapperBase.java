package org.acme.test_common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ObjectMapperBase {

    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN).withZone(ZoneOffset.UTC);

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));

    public final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(DATE_FORMAT);
}
