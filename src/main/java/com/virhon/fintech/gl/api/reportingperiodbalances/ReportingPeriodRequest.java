package com.virhon.fintech.gl.api.reportingperiodbalances;

import com.virhon.fintech.gl.api.SeparatedDate;

public class ReportingPeriodRequest {
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
