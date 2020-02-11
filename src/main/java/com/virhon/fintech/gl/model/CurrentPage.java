package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class CurrentPage extends AbstractPage {
    public CurrentPage(ZonedDateTime startedAt, LocalDate reportedAt, BigDecimal startBalance) {
        super(startedAt, reportedAt, startBalance);
    }
}
