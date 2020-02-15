package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Account {
    private Long                accountId;
    private Ledger              ledger;

    final static Logger LOGGER = Logger.getLogger(Account.class);

    private Account(Ledger ledger) {
        this.ledger = ledger;
    }

    public static Account getExistingById(Ledger        ledger,
                                          Long          accountId) throws LedgerException {
        if (ledger.getAttrRepo().getById(accountId)==null) {
            throw LedgerException.invalidAccount(accountId.toString());
        }
        final Account account = new Account(ledger);
        account.accountId = accountId;
        return account;
    }

    public static Account getExistingByAccountNumber(Ledger        ledger,
                                                     String        accountNumber) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = ledger.getAttrRepo().getByAccountNumber(accountNumber);
        if (aa==null) {
            throw LedgerException.invalidAccount(accountNumber);
        }
        final Account account = new Account(ledger);
        account.accountId = aa.getId();
        return account;
    }

    public static Account getExistingByIban(Ledger        ledger,
                                            String        iban) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = ledger.getAttrRepo().getByIban(iban);
        if (aa==null) {
            throw LedgerException.invalidAccount(iban);
        }
        final Account account = new Account(ledger);
        account.accountId = aa.getId();
        return account;
    }

    public static Account openNew(Ledger          ledger,
                                  String          accountNumber,
                                  String          iban,
                                  AccountType     accountType) {
        final AccountAttributes attributes = AccountAttributes.createNew(accountNumber, iban, accountType);
        final Page page = Page.create(BigDecimal.ZERO);
        final Account account = new Account(ledger);
        final Long accountId = ledger.getAttrRepo().insert(attributes);
        account.accountId = accountId;
        final IdentifiedEntity<Page> identifiedPage = new IdentifiedEntity<Page>(accountId, page);
        ledger.getCurPageRepo().put(identifiedPage);
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
            final IdentifiedEntity<Page> historicalPage = this.ledger.getHistPageRepo()
                    .getByAccountId(attributes.getId(), at);
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
        final BigDecimal balance = pBalance.add(attributes.getReservedDebit());
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
        final IdentifiedEntity<AccountAttributes> attributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (attributes == null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        final IdentifiedEntity<Page> currentPage = this.ledger.getCurPageRepo().getByIdExclusive(this.accountId);
        final BigDecimal newBalance = attributes.getEntity().getBalance().add(post.getAmount());
        if (!isValidBalance(attributes.getEntity(), newBalance)) {
            throw LedgerException.redBalance(attributes.getEntity().getAccountNumber());
        } else {
            attributes.getEntity().setBalance(newBalance);
            if (currentPage.getEntity().hasNext()) {
                currentPage.getEntity().addPost(post);
                this.ledger.getCurPageRepo().put(currentPage);
            } else {
                final Page hPage = currentPage.getEntity();
                final Page cPage = Page.create(hPage.getFinishedAt(),
                        hPage.getRepFinishedOn(), hPage.getFinishBalance());
                cPage.addPost(post);
                final IdentifiedEntity<Page> cidPage = new IdentifiedEntity<>(this.accountId, cPage);
                this.ledger.getCurPageRepo().put(cidPage);
                this.ledger.getHistPageRepo().insert(this.accountId, hPage);
            }
            this.ledger.getAttrRepo().update(attributes);
        }
    }

    /**
     * Credit account
     *
     * @param transferId
     * @param postedAt
     * @param reportedOn
     * @param amount        positive says about debiting, negative says about crediting
     * @return              resulted account balance
     */
    private BigDecimal operate(Long transferId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        Post post = new Post(transferId, postedAt, reportedOn, amount);
        registerPost(post);
        return getAttributes().getEntity().getBalance();
    }

    public BigDecimal credit(Long transferId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        LOGGER.info("Crediting account id=".concat(accountId.toString()).concat(" for ".concat(amount.toString())));
        final BigDecimal balance = operate(transferId, postedAt, reportedOn, amount.negate());
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    public BigDecimal debit(Long transferId, ZonedDateTime postedAt, LocalDate reportedOn, BigDecimal amount)
            throws LedgerException {
        LOGGER.info("Debting account id=".concat(accountId.toString()).concat(" for ".concat(amount.toString())));
        final BigDecimal balance = operate(transferId, postedAt, reportedOn, amount);
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    /**
     * Reserves amount for future debiting
     *
     * @param amount
     * @return
     * @throws LedgerException
     */
    public BigDecimal reserveDebit(BigDecimal amount) throws LedgerException {
        // 1. Calculate new reservedAmount
        final IdentifiedEntity<AccountAttributes> iAttributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (iAttributes==null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        if (amount.signum() == -1) {
            throw LedgerException.invalidReservationAmount(amount);
        }
        final AccountAttributes attributes = iAttributes.getEntity();
        final BigDecimal balance = attributes.getBalance();
        final BigDecimal reserved = attributes.getReservedDebit();
        final BigDecimal newReserved = reserved.add(amount);
        // 2. Check the reservedAmount
        if (attributes.getAccountType() == AccountType.PASSIVE && balance.negate().compareTo(newReserved) < 0) {
            throw LedgerException.wrongReservation(attributes.getAccountNumber());
        }
        // 3. Change reservedAmount
        attributes.setReservedDebit(newReserved);
        final IdentifiedEntity<AccountAttributes> oAttr = new IdentifiedEntity<>(this.accountId, attributes);
        this.ledger.getAttrRepo().update(oAttr);
        return newReserved;
    }

    /**
     * Reserves amount for future crediting
     *
     * @param amount
     * @return
     * @throws LedgerException
     */
    public BigDecimal reserveCredit(BigDecimal amount) throws LedgerException {
        // 1. Calculate new reservedAmount
        final IdentifiedEntity<AccountAttributes> iAttributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (iAttributes==null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        if (amount.signum() == -1) {
            throw LedgerException.invalidReservationAmount(amount);
        }
        final AccountAttributes attributes = iAttributes.getEntity();
        final BigDecimal balance = attributes.getBalance();
        final BigDecimal reserved = attributes.getReservedCredit();
        final BigDecimal newReserved = reserved.add(amount);
        // 2. Check the reservedAmount
        if (attributes.getAccountType() == AccountType.ACTIVE && balance.compareTo(newReserved) < 0) {
            throw LedgerException.wrongReservation(attributes.getAccountNumber());
        }
        // 3. Change reservedAmount
        attributes.setReservedCredit(newReserved);
        final IdentifiedEntity<AccountAttributes> oAttr = new IdentifiedEntity<>(this.accountId, attributes);
        this.ledger.getAttrRepo().update(oAttr);
        return newReserved;
    }

    /**
     * Reserved for checking if the account arrested or blocked
     * @return
     */
    public Boolean canBeOperated() {return true;}

    public IdentifiedEntity<AccountAttributes> getAttributes() {
        return this.ledger.getAttrRepo().getById(this.accountId);
    }

    public IdentifiedEntity<Page> getCurrentPage() {
        return this.ledger.getCurPageRepo().getById(this.accountId);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}