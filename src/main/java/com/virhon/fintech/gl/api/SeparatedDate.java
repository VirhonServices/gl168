package com.virhon.fintech.gl.api;

import java.time.LocalDate;

public class SeparatedDate extends RequestValidator{
    private Integer year;
    private Integer month;
    private Integer day;

    public LocalDate asLocalDate() {
        return LocalDate.of(this.year, this.month, this.day);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
