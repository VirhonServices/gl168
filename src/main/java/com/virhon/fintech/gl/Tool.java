package com.virhon.fintech.gl;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Tool {
    public static ZonedDateTime buildByDefault(Integer year,
                                               Integer month,
                                               Integer day,
                                               Integer hour,
                                               Integer minute,
                                               Integer second,
                                               Integer nano,
                                               String zone) {
        int _year = ZonedDateTime.now().getYear();
        int _month = ZonedDateTime.now().getMonthValue();
        int _day = ZonedDateTime.now().getDayOfMonth();
        int _hour = ZonedDateTime.now().getHour();
        int _minute = ZonedDateTime.now().getMinute();
        int _second = ZonedDateTime.now().getSecond();
        int _nano = ZonedDateTime.now().getNano();
        String _zone = ZoneId.systemDefault().getId();
        if (year != null) {
            _year = year;
        }
        if (month != null) {
            _month = month;
        }
        if (day != null) {
            _day = day;
        }
        if (hour != null) {
            _hour = hour;
        }
        if (minute != null) {
            _minute = minute;
        }
        if (second != null) {
            _second = second;
        }
        if (nano != null) {
            _nano = nano;
        }
        if (zone != null) {
            _zone = zone;
        }
        final ZonedDateTime result = ZonedDateTime.of(_year,_month, _day, _hour, _minute,
                _second, _nano, ZoneId.of(_zone));
        return result;
    }
}
