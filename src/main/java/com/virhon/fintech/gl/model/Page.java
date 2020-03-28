package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.exception.LedgerException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Page {
    private String          uuid;
    private String          accountUuid;
    private ZonedDateTime   startedAt;
    private ZonedDateTime   finishedAt;
    private LocalDate       repStartedOn;
    private LocalDate       repFinishedOn;
    private BigDecimal      startBalance;
    private BigDecimal      finishBalance;
    private BigDecimal      startRepBalance;
    private BigDecimal      finishRepBalance;

    private List<Transfer>  transfers = new ArrayList<>();

    private Page() {
    }

    /**
     * Factory method
     *
     * @param startedAt
     * @param reportedOn
     * @param startBalance
     * @return
     */
    public static Page create(String accountUuid, ZonedDateTime startedAt, LocalDate reportedOn,
                              BigDecimal startBalance, BigDecimal startRepBalance) {
        final Page page = new Page();
        page.setUuid(UUID.randomUUID().toString());
        page.setAccountUuid(accountUuid);
        page.setStartedAt(startedAt);
        page.setFinishedAt(startedAt);
        page.setRepStartedOn(reportedOn);
        page.setRepFinishedOn(reportedOn);
        page.setStartBalance(startBalance);
        page.setFinishBalance(startBalance);
        page.setStartRepBalance(startRepBalance);
        page.setFinishRepBalance(startRepBalance);
        return page;
    }

    public static Page create(String accountUuid, BigDecimal startBalance, BigDecimal startRepBalance) {
        return create(accountUuid, ZonedDateTime.now(), LocalDate.now(), startBalance, startRepBalance);
    }

    /**
     * Check if current page can contain the POSTING date
     *
     * @param at
     * @return
     */
    public boolean currentCanContain(ZonedDateTime at) {
        return this.startedAt.compareTo(at) <= 0;
    }

    /**
     * Check if current page can contain the REPORTING date
     *
     * @param on
     * @return
     */
    public boolean currentCanContain(LocalDate on) {
        return this.repStartedOn.compareTo(on) <= 0;
    }

    /**
     * Checks if POSTING date belongs to the page
     *
     * @param at
     * @return
     */
    public boolean contains(ZonedDateTime at) {
        return (currentCanContain(at) && at.compareTo(this.finishedAt) <= 0);
    }

    /**
     * Checks if REPORTING date belongs to the page
     *
     * @param on
     * @return
     */
    public boolean contains(LocalDate on) {
        return (currentCanContain(on) && on.compareTo(this.getRepFinishedOn()) <= 0);
    }

    public List<Transfer> addTransfer(final Transfer transfer) throws LedgerException {
        this.transfers.add(transfer);
        this.finishBalance = this.finishBalance.add(transfer.getAmount());
        this.finishRepBalance = this.finishRepBalance.add(transfer.getLocalAmount());
        if (this.finishedAt.compareTo(transfer.getPostedAt()) < 0) {
            this.finishedAt = transfer.getPostedAt();
        }
        if (this.repFinishedOn.compareTo(transfer.getReportedOn()) < 0) {
            this.repFinishedOn = transfer.getReportedOn();
        }
        return this.transfers;
    }

    public BigDecimal getBalanceAt(ZonedDateTime at) {
        BigDecimal curAmount = this.startBalance;
        if (!transfers.isEmpty()) {
            int res = 0;
            for (int i = 0; i< transfers.size(); i++) {
                Transfer curTransfer = transfers.get(i);
                res = at.compareTo(curTransfer.getPostedAt());
                if (res >= 0) {
                    curAmount = curAmount.add(curTransfer.getAmount());
                } else {
                    break;
                }
            }
        }
        return curAmount;
    }

    /**
     *
     * @param number
     * @return              - true if the page overflowed
     */
    private boolean checkOverflow(int number) {
        return number > Config.getInstance().getMaxNumPostsInBlock();
    }

    /**
     *
     * @return              - true if the block is full
     */
    public boolean isFull() {
        return !checkOverflow(this.transfers.size());
    }

    public boolean hasNext() {
        return !checkOverflow(this.transfers.size()+1);
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getFinishedAt() {
        if (this.finishedAt != null) {
            return this.finishedAt;
        } else {
            return ZonedDateTime.now();
        }
    }

    public void setFinishedAt(ZonedDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDate getRepStartedOn() {
        return this.repStartedOn;
    }

    public void setRepStartedOn(LocalDate repStartedOn) {
        this.repStartedOn = repStartedOn;
    }

    public LocalDate getRepFinishedOn() {
        if (this.repFinishedOn!= null) {
            return this.repFinishedOn;
        } else {
            return Config.getInstance().getReportedOn();
        }

    }

    public Optional<Transfer> locate(final String transferUuid) {
        return this.transfers.stream().filter(t -> t.getTransferUuid().equals(transferUuid)).findFirst();
    }

    public BigDecimal getStartRepBalance() {
        return startRepBalance;
    }

    public void setStartRepBalance(BigDecimal startRepBalance) {
        this.startRepBalance = startRepBalance;
    }

    public BigDecimal getFinishRepBalance() {
        return finishRepBalance;
    }

    public void setFinishRepBalance(BigDecimal finishRepBalance) {
        this.finishRepBalance = finishRepBalance;
    }

    public void setRepFinishedOn(LocalDate repFinishedOn) {
        this.repFinishedOn = repFinishedOn;
    }

    public BigDecimal getStartBalance() {
        return this.startBalance;
    }

    public void setStartBalance(BigDecimal startBalance) {
        this.startBalance = startBalance;
    }

    public BigDecimal getFinishBalance() {
        return this.finishBalance;
    }

    public void setFinishBalance(BigDecimal finishBalance) {
        this.finishBalance = finishBalance;
    }

    public List<Transfer> getTransfers() {
        return this.transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
