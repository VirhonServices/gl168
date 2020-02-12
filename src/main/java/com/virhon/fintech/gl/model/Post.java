package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Post {
    private Long            documentId;
    private ZonedDateTime   postedAt;
    private LocalDate       reportedOn;
    private BigDecimal      amount;

    /**
     * Post is used only as a part of a account's page
     *
     * @param documentId            - if of the document the post belongs to
     * @param postedAt              - calendar zoned datetime when the post was made
     * @param reportedOn            - financial date the post linked with
     * @param amount                - negative says about credit turnover, positive - about debit
     */
    public Post(Long documentId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount) {
        this.documentId = documentId;
        this.postedAt = postedAt;
        this.reportedOn = reportedOn;
        this.amount = amount;
    }

    public Long getDocumentId() {
        return documentId;
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
}
