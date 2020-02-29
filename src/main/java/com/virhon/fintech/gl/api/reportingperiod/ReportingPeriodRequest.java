package com.virhon.fintech.gl.api.reportingperiod;

import com.virhon.fintech.gl.api.RequestValidator;
import com.virhon.fintech.gl.api.SeparatedDate;

public class ReportingPeriodRequest extends RequestValidator {
    private SeparatedDate beginOn;
    private SeparatedDate finishOn;

    public SeparatedDate getBeginOn() {
        return beginOn;
    }

    public void setBeginOn(SeparatedDate beginOn) {
        this.beginOn = beginOn;
    }

    public SeparatedDate getFinishOn() {
        return finishOn;
    }

    public void setFinishOn(SeparatedDate finishOn) {
        this.finishOn = finishOn;
    }
}
