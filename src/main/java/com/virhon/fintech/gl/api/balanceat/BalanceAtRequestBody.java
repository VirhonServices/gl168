package com.virhon.fintech.gl.api.balanceat;

import com.virhon.fintech.gl.api.RequestValidator;

public class BalanceAtRequestBody extends RequestValidator {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Integer second;
    private Integer nanoOfSecond;

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

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getNanoOfSecond() {
        return nanoOfSecond;
    }

    public void setNanoOfSecond(Integer nanoOfSecond) {
        this.nanoOfSecond = nanoOfSecond;
    }
}
