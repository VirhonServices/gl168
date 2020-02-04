package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CurrentPage extends AbstractPage {
    public CurrentPage(ZonedDateTime startedAt, BigDecimal startBalance) {
        super(startedAt, startBalance);
    }
}
