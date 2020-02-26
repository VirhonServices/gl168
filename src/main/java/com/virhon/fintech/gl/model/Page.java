package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.exception.LedgerException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Page {
    private ZonedDateTime   startedAt;
    private ZonedDateTime   finishedAt;
    private LocalDate       repStartedOn;
    private LocalDate       repFinishedOn;
    private BigDecimal      startBalance;
    private BigDecimal      finishBalance;
    private BigDecimal      startRepBalance;
    private BigDecimal      finishRepBalance;

    private List<Post>      posts = new ArrayList<>();

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
    public static Page create(ZonedDateTime startedAt, LocalDate reportedOn,
                              BigDecimal startBalance, BigDecimal startRepBalance) {
        final Page page = new Page();
        page.setStartedAt(startedAt);
        page.setFinishedAt(startedAt);
        page.setRepStartedOn(reportedOn);
        page.setRepFinishedOn(reportedOn);
        page.setStartBalance(startBalance);
        page.setFinishBalance(startBalance);
        page.setStartRepBalance(startRepBalance);
        return page;
    }

    public static Page create(BigDecimal startBalance, BigDecimal startRepBalance) {
        return create(ZonedDateTime.now(), LocalDate.now(), startBalance, startRepBalance);
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

    public List<Post> addPost(final Post post) throws LedgerException {
/*
        if (this.startedAt.compareTo(post.getPostedAt()) > 0) {
            throw LedgerException.invalidPostedAt(post);
        }
        if (this.repStartedOn.compareTo(post.getReportedOn()) > 0) {
            throw LedgerException.invalidReportedOn(post);
        }
*/
        this.posts.add(post);
        this.finishBalance = this.finishBalance.add(post.getAmount());
        this.finishRepBalance = this.finishRepBalance.add(post.getLocalAmount());
        if (this.finishedAt.compareTo(post.getPostedAt()) < 0) {
            this.finishedAt = post.getPostedAt();
        }
        if (this.repFinishedOn.compareTo(post.getReportedOn()) < 0) {
            this.repFinishedOn = post.getReportedOn();
        }
        return this.posts;
    }

    public BigDecimal getBalanceAt(ZonedDateTime at) {
        BigDecimal curAmount = this.startBalance;
        if (!posts.isEmpty()) {
            int res = 0;
            for (int i=0; i<posts.size(); i++) {
                Post curPost = posts.get(i);
                res = at.compareTo(curPost.getPostedAt());
                if (res >= 0) {
                    curAmount = curAmount.add(curPost.getAmount());
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
        return !checkOverflow(this.posts.size());
    }

    public boolean hasNext() {
        return !checkOverflow(this.posts.size()+1);
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

    public List<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
