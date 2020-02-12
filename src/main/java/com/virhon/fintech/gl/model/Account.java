package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.AttrRepo;
import com.virhon.fintech.gl.repo.CurPageRepo;
import com.virhon.fintech.gl.repo.HistPageRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Account {
    private Long                accountId;

    private AttrRepo            attrRepo;
    private CurPageRepo         curPageRepo;
    private HistPageRepo        histPageRepo;

    final static Logger LOGGER = Logger.getLogger(Account.class);

    private Account(AttrRepo        attrRepo,
                    CurPageRepo     curPageRepo,
                    HistPageRepo    histPageRepo) {
        this.attrRepo = attrRepo;
        this.curPageRepo = curPageRepo;
        this.histPageRepo = histPageRepo;
    }

    public static Account getExisting(Long          accountId,
                                      AttrRepo      attrRepo,
                                      CurPageRepo   curPageRepo,
                                      HistPageRepo  histPageRepo) throws LedgerException {
        if (attrRepo.getById(accountId)==null) {
            throw LedgerException.invalidAccount(accountId);
        }
        final Account account = new Account(attrRepo, curPageRepo, histPageRepo);
        account.accountId = accountId;
        return account;
    }

    public static Account openNew(String          accountNumber,
                                  String          iban,
                                  AccountType     accountType,
                                  AttrRepo        attrRepo,
                                  CurPageRepo     curPageRepo,
                                  HistPageRepo    histPageRepo) {
        final AccountAttributes attributes = AccountAttributes.createNew(accountNumber, iban, accountType);
        final Page page = Page.create(BigDecimal.ZERO);
        final Account account = new Account(attrRepo, curPageRepo, histPageRepo);
        final Long accountId = attrRepo.insert(attributes);
        account.accountId = accountId;
        final IdentifiedEntity<Page> identifiedPage = new IdentifiedEntity<Page>(accountId, page);
        curPageRepo.put(identifiedPage);
        attrRepo.commit();
        curPageRepo.commit();
        return account;
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
        final IdentifiedEntity<Page> currentPage = getCurrentPage();
        if (currentPage.getEntity().contains(at)) {
            return currentPage.getEntity().getBalanceAt(at);
        } else {
            final IdentifiedEntity<Page> historicalPage = histPageRepo.getByAccountId(attributes.getId(), at);
            if (historicalPage == null) {
                throw LedgerException.invalidHistoricalData(this, at);
            } else {
                return historicalPage.getEntity().getBalanceAt(at);
            }
        }
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
        if (attributes == null) {
            throw LedgerException.invalidAccount(this.accountId);
        }
        final IdentifiedEntity<Page> currentPage = this.curPageRepo.getByIdExclusive(this.accountId);
        final BigDecimal newBalance = attributes.getEntity().getBalance().add(post.getAmount());
        if (!isValidBalance(attributes.getEntity(), newBalance)) {
            throw LedgerException.redBalance(attributes.getEntity().getAccountNumber());
        } else {
            attributes.getEntity().setBalance(newBalance);
            if (currentPage.getEntity().hasNext()) {
                currentPage.getEntity().addPost(post);
                this.curPageRepo.put(currentPage);
                this.curPageRepo.commit();
            } else {
                final Page hPage = currentPage.getEntity();
                final Page cPage = Page.create(hPage.getFinishedAt(),
                        hPage.getRepFinishedOn(), hPage.getFinishBalance());
                cPage.addPost(post);
                final IdentifiedEntity<Page> cidPage = new IdentifiedEntity<>(this.accountId, cPage);
                this.curPageRepo.put(cidPage);
                this.curPageRepo.commit();
                this.histPageRepo.insert(this.accountId, hPage);
                this.histPageRepo.commit();
            }
            this.attrRepo.update(attributes);
            this.attrRepo.commit();
        }
    }

    /**
     * Credit account
     *
     * @param documentId
     * @param postedAt
     * @param reportedOn
     * @param amount        positive says about debiting, negative says about crediting
     * @return              resulted account balance
     */
    private BigDecimal operate(Long documentId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        Post post = new Post(documentId, postedAt, reportedOn, amount);
        registerPost(post);
        return getAttributes().getEntity().getBalance();
    }

    public BigDecimal credit(Long documentId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        LOGGER.info("Crediting account id=".concat(accountId.toString()).concat(" for ".concat(amount.toString())));
        final BigDecimal balance = operate(documentId, postedAt, reportedOn, amount.negate());
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    public BigDecimal debit(Long documentId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        LOGGER.info("Debting account id=".concat(accountId.toString()).concat(" for ".concat(amount.toString())));
        final BigDecimal balance = operate(documentId, postedAt, reportedOn, amount);
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    public IdentifiedEntity<AccountAttributes> getAttributes() {
        return this.attrRepo.getById(this.accountId);
    }

    public IdentifiedEntity<Page> getCurrentPage() {
        return this.curPageRepo.getById(this.accountId);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}