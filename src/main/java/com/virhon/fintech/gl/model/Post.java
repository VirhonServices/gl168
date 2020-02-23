package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Post {
    private String          transferUuid;
    private ZonedDateTime   postedAt;
    private LocalDate       reportedOn;
    private BigDecimal      amount;
    private BigDecimal      localAmount;

    /**
     * Post is used only as a part of a account's page
     *
     * @param transferUuid            - if of the document the post belongs to
     * @param postedAt              - calendar zoned datetime when the post was made
     * @param reportedOn            - financial date the post linked with
     * @param amount                - negative says about credit turnover, positive - about debit
     * @param localAmount           - the amount in local currency
     */
    public Post(String          transferUuid,
                ZonedDateTime   postedAt,
                LocalDate       reportedOn,
                BigDecimal      amount,
                BigDecimal      localAmount) {
        this.transferUuid = transferUuid;
        this.postedAt = postedAt;
        this.reportedOn = reportedOn;
        this.amount = amount;
        this.localAmount = localAmount;
    }

    public String getTransferUuid() {
        return transferUuid;
    }

    public ZonedDateTime getPostedAt() {
        return postedAt;
    }

    public LocalDate getReportedOn() {
        return reportedOn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getLocalAmount() {
        return localAmount;
    }
}
