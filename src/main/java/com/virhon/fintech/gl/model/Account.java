package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.AttrRepo;
import com.virhon.fintech.gl.repo.CurPagesRepo;
import com.virhon.fintech.gl.repo.HistPageRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Account {
    private Long                accountId;

    private AttrRepo            attrRepo;
    private CurPagesRepo        curPagesRepo;
    private HistPageRepo        histPageRepo;

    public static Account getById(Long accountId,
                                  AttrRepo attrRepo, CurPagesRepo curPagesRepo, HistPageRepo histPageRepo) {
        final Account account = new Account(attrRepo, curPagesRepo, histPageRepo);
        account.accountId = accountId;
        return account;
    }

    private Account(AttrRepo attrRepo, CurPagesRepo curPagesRepo, HistPageRepo histPageRepo) {
        this.attrRepo = attrRepo;
        this.curPagesRepo = curPagesRepo;
        this.histPageRepo = histPageRepo;
    }

    /**
     * Returns the balance of the account from current or historical pages
     *
     * @param at
     * @return
     * @throws LedgerException
     */
    public BigDecimal getAccountBalanceAt(ZonedDateTime at) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> attributes = getAttributes();
        final IdentifiedEntity<CurrentPage> currentPage = getCurrentPage();
        if (currentPage.getEntity().contains(at)) {
            return currentPage.getEntity().getBalanceAt(at);
        } else {
            final IdentifiedEntity<HistoricalPage> historicalPage = histPageRepo.getByAccountId(attributes.getId(), at);
            if (historicalPage == null) {
                throw LedgerException.invalidHistoricalData(this, at);
            } else {
                return historicalPage.getEntity().getBalanceAt(at);
            }
        }
    }

    public void commit() {
        this.attrRepo.commit();
        this.curPagesRepo.commit();
    }

    /**
     * Checks if specified balance accords to account's type
     *
     * @param pBalance
     * @return
     */
    private boolean isValidBalance(AccountAttributes attributes, BigDecimal pBalance) {
        final BigDecimal balance = pBalance.add(attributes.getReservedAmount());
        return (attributes.getAccountType().equals(AccountType.ACTIVEPASSIVE)) ||
                (attributes.getAccountType().equals(AccountType.ACTIVE) && (balance.signum() ==   1 ||
                        balance.signum() == 0)) ||
                (attributes.getAccountType().equals(AccountType.PASSIVE) && (balance.signum() == -1 ||
                        balance.signum() == 0));
    }

    /**
     * Register the post into a current page
     *
     * @param post
     * @throws LedgerException
     */
    private void registerPost(Post post) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> attributes = this.attrRepo.getByIdExclusive(this.accountId);
        final IdentifiedEntity<CurrentPage> currentPage = this.curPagesRepo.getByIdExclusive(this.accountId);
        final BigDecimal newBalance = attributes.getEntity().getBalance().add(post.getAmount());
        if (!isValidBalance(attributes.getEntity(), newBalance)) {
            throw LedgerException.redBalance(attributes.getEntity().getAccountNumber());
        } else {
            currentPage.getEntity().addPost(post);
            attributes.getEntity().setBalance(newBalance);
            this.attrRepo.put(attributes);
            this.curPagesRepo.put(currentPage);
            commit();
        }
    }

    /**
     * Credit account
     *
     * @param documentId
     * @param postedAt
     * @param reportedAt
     * @param amount        positive only!!!!
     * @return              resulted account balance
     */
    public BigDecimal credit(Long documentId, ZonedDateTime postedAt, LocalDate reportedAt, BigDecimal amount)
            throws LedgerException {
        Post post = new Post(documentId, postedAt, reportedAt, amount);
        registerPost(post);
        return getAttributes().getEntity().getBalance();
    }

    public BigDecimal debit(Long documentId, ZonedDateTime postedAt, LocalDate reportedAt, BigDecimal amount)
            throws LedgerException {
        return credit(documentId, postedAt, reportedAt, amount.negate());
    }

    public IdentifiedEntity<AccountAttributes> getAttributes() {
        return this.attrRepo.getById(this.accountId);
    }

    public IdentifiedEntity<CurrentPage> getCurrentPage() {
        return this.curPagesRepo.getById(this.accountId);
    }
}
