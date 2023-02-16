package ru.practicum.explorewithme.util;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class TimeFormatter implements Formatter<LocalDateTime> {

    private final static String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return LocalDateTime.parse(text,
                DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN));
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return null;
    }
}
