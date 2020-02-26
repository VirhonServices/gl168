package com.virhon.fintech.gl.api.reportingperiodtransfers;

import com.virhon.fintech.gl.api.maketransfer.TransferData;

import java.util.ArrayList;
import java.util.List;

public class ReportingPeriodTransfersResponse {
    private List<TransferData> transfers = new ArrayList<>();

    public List<TransferData> getTransfers() {
        return transfers;
    }
}
