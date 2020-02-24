package com.virhon.fintech.gl.api.postreservation;

import com.virhon.fintech.gl.api.SeparatedDate;

import java.math.BigDecimal;

public class PostReservationRequest {
    private BigDecimal repAmount;
    private SeparatedDate reportedOn;

    public BigDecimal getRepAmount() {
        return repAmount;
    }

    public void setRepAmount(BigDecimal repAmount) {
        this.repAmount = repAmount;
    }

    public SeparatedDate getReportedOn() {
        return reportedOn;
    }

    public void setReportedOn(SeparatedDate reportedOn) {
        this.reportedOn = reportedOn;
    }
}
