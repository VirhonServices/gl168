package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Ledger {
    private AttrRepo attrRepo;
    private CurPageRepo curPageRepo;
    private HistPageRepo histPageRepo;
    private TransferRepo transferRepo;
    private ReservationRepo reservationRepo;

    final static Logger LOGGER = Logger.getLogger(Ledger.class);

    public Ledger(LedgerRepoFactory factory) throws IOException {
        this.attrRepo = factory.getAccountAttributeRepository();
        this.curPageRepo = factory.getCurrentPageRepository();
        this.histPageRepo = factory.getHistoricalPageRepository();
        this.transferRepo = factory.getTransferRepository();
        this.reservationRepo = factory.getReservationRepository();
    }

    public Account getExistingById(Long accountId) throws LedgerException {
        if (this.getAttrRepo().getById(accountId)==null) {
            throw LedgerException.invalidAccount(accountId.toString());
        }
        final Account account = new Account(this);
        account.setAccountId(accountId);
        return account;
    }

    public Account getExistingByUuid(String uuid) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByUuid(uuid);
        if (aa==null) {
            throw LedgerException.invalidAccount(uuid);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    public Account getExistingByAccountNumber(String accountNumber) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByAccountNumber(accountNumber);
        if (aa==null) {
            throw LedgerException.invalidAccount(accountNumber);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    public Account getExistingByIban(String iban) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByIban(iban);
        if (aa==null) {
            throw LedgerException.invalidAccount(iban);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    public Account openNew(String          accountNumber,
                           String          iban,
                           AccountType     accountType) {
        final AccountAttributes attributes = AccountAttributes.createNew(accountNumber, iban, accountType);
        final Page page = Page.create(BigDecimal.ZERO, BigDecimal.ZERO);
        final Account account = new Account(this);
        final Long accountId = getAttrRepo().insert(attributes);
        account.setAccountId(accountId);
        final IdentifiedEntity<Page> identifiedPage = new IdentifiedEntity<Page>(accountId, page);
        getCurPageRepo().put(identifiedPage);
        return account;
    }

    /**
     *
     * @param transferRef
     * @param debitId
     * @param creditId
     * @param amount
     * @param localAmount
     * @param reportedOn
     * @param description
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Transfer> transferFunds(final String     transferRef,
                                                    final Long       debitId,
                                                    final Long       creditId,
                                                    final BigDecimal amount,
                                                    final BigDecimal localAmount,
                                                    final LocalDate  reportedOn,
                                                    final String     description) throws LedgerException {
        LOGGER.info(transferRef.concat(" Transferring ".concat(amount.toString())
                .concat(" from ".concat(debitId.toString().concat(" to ").concat(creditId.toString())))));
        // Check sign of amount
        if (amount.signum() == -1) {
            throw LedgerException.invalidTransferAmount(amount);
        }
        // Get accounts
        final Account debit  = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        // Check if the transfer is possible
        if (!debit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(debitId);
        }
        if (!credit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(creditId);
        }
        // Do transfer
        final ZonedDateTime postedAt = ZonedDateTime.now();
        final Transfer transfer = new Transfer();
        transfer.setTransferRef(transferRef);
        transfer.setTransferUuid(UUID.randomUUID().toString());
        transfer.setAmount(amount);
        transfer.setLocalAmount(localAmount);
        transfer.setReportedOn(reportedOn);
        transfer.setDescription(description);
        transfer.setPostedAt(postedAt);
        transfer.setDebitUuid(debit.getAttributes().getEntity().getAccountUUID());
        transfer.setCreditUuid(credit.getAttributes().getEntity().getAccountUUID());
        final IdentifiedEntity<Transfer> iTransfer = this.transferRepo.insert(transfer);
        final String transferUuid = UUID.randomUUID().toString();
        debit.debit(transferUuid, postedAt, reportedOn, amount, localAmount);
        credit.credit(transferUuid, postedAt, reportedOn, amount, localAmount);
        LOGGER.info(" Transferring ".concat(transferRef).concat(" SUCCEED"));
        return iTransfer;
    }

    /**
     *
     * @param transferRef
     * @param debitUuid
     * @param creditUuid
     * @param amount
     * @param localAmount
     * @param reportedOn
     * @param description
     * @return
     */
    public IdentifiedEntity<Transfer> transferFunds(final String     transferRef,
                                                    final String     debitUuid,
                                                    final String     creditUuid,
                                                    final BigDecimal amount,
                                                    final BigDecimal localAmount,
                                                    final LocalDate  reportedOn,
                                                    final String     description) throws LedgerException {
        final Account debit = getExistingByUuid(debitUuid);
        final Account credit = getExistingByUuid(creditUuid);
        return transferFunds(transferRef, debit.getAccountId(), credit.getAccountId(),
                amount, localAmount, reportedOn, description);
    }

    /**
     *
     * @param transferRef
     * @param debitUuid
     * @param creditUuid
     * @param amount
     * @param description
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Reservation> reserveFunds(final String     transferRef,
                                                      final String     debitUuid,
                                                      final String     creditUuid,
                                                      final BigDecimal amount,
                                                      final String     description) throws LedgerException {
        final Account debit = getExistingByUuid(debitUuid);
        final Account credit = getExistingByUuid(creditUuid);
        return reserveFunds(transferRef, debit.getAccountId(), credit.getAccountId(), amount, description);
    }

    /**
     *
     * @param transferRef
     * @param debitId
     * @param creditId
     * @param amount
     * @param description
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Reservation> reserveFunds(final String     transferRef,
                                               final Long       debitId,
                                               final Long       creditId,
                                               final BigDecimal amount,
                                               final String     description) throws LedgerException {
        // 1. Create a reservation
        final Reservation reservation = new Reservation();
        reservation.setUuid(UUID.randomUUID().toString());
        reservation.setTransferRef(transferRef);
        reservation.setDebitId(debitId);
        reservation.setCreditId(creditId);
        reservation.setAmount(amount);
        reservation.setExpireAt(ZonedDateTime.now().plusSeconds(Config.getInstance().getReservigDuration()));
        reservation.setDescription(description);
        final IdentifiedEntity<Reservation> iReservation = this.reservationRepo.insert(reservation);
        // 2. Reserve funds
        final Account debit = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        debit.reserveDebit(amount);
        credit.reserveCredit(amount);
        return iReservation;
    }


    /**
     *
     * @param uuid
     * @param localAmount
     * @param reportedOn
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Transfer> postReservation(String       uuid,
                                                      BigDecimal   localAmount,
                                                      LocalDate    reportedOn) throws LedgerException {
        // 1. Get the reservation
        final IdentifiedEntity<Reservation> reservation = reservationRepo.getByUuid(uuid);
        return postReservation(reservation.getId(), localAmount, reportedOn);
    }

    /**
     *
     * @param id
     * @param localAmount
     * @param reportedOn
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Transfer> postReservation(Long         id,
                                                      BigDecimal   localAmount,
                                                      LocalDate    reportedOn) throws LedgerException {
        // 1. Get the reservation
        final IdentifiedEntity<Reservation> reservation = reservationRepo.getById(id);
        final Long debitId = reservation.getEntity().getDebitId();
        final Long creditId = reservation.getEntity().getCreditId();
        final BigDecimal amount = reservation.getEntity().getAmount();
        // 2. Make new transfer
        final IdentifiedEntity<Transfer> transfer = transferFunds(reservation.getEntity().getTransferRef(),
                                                                  debitId,
                                                                  creditId,
                                                                  amount,
                                                                  localAmount,
                                                                  reportedOn,
                                                                  reservation.getEntity().getDescription());
        // 3. Delete the reservation
        final Account debit = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        debit.reserveDebit(amount.negate());
        credit.reserveCredit(amount.negate());
        this.reservationRepo.delete(reservation.getId());
        return transfer;
    }

    void cancelReservation(Long id) {
        this.reservationRepo.delete(id);
    }

    public AttrRepo getAttrRepo() {
        return this.attrRepo;
    }

    public CurPageRepo getCurPageRepo() {
        return this.curPageRepo;
    }

    public HistPageRepo getHistPageRepo() {
        return this.histPageRepo;
    }

    public TransferRepo getTransferRepo() {
        return this.transferRepo;
    }

    public ReservationRepo getReservationRepo() {
        return this.reservationRepo;
    }

    /**
     *
     * @param tr
     * @return
     * @throws LedgerException
     */
    public TransferData createTransferResponseBody(Transfer tr) throws LedgerException {
        final com.virhon.fintech.gl.model.Account debit = this.getExistingByUuid(tr.getDebitUuid());
        final AccountAttributes debAttr = debit.getAttributes().getEntity();
        final com.virhon.fintech.gl.model.Account credit = this.getExistingByUuid(tr.getCreditUuid());
        final AccountAttributes creAttr = credit.getAttributes().getEntity();
        final TransferData response = new TransferData();
        response.setUuid(tr.getTransferUuid());
        response.setTransferRef(tr.getTransferRef());
        response.setPostedAt(tr.getPostedAt().toString());
        response.setReportedOn(tr.getReportedOn().toString());
        response.setAmount(tr.getAmount());
        response.setRepAmount(tr.getLocalAmount());
        response.setDescription(tr.getDescription());
        final TransferData.Account deb = new TransferData.Account();
        deb.setAccUuid(debAttr.getAccountUUID());
        deb.setAccNumber(debAttr.getAccountNumber());
        deb.setIban(debAttr.getIban());
        deb.setAccType(debAttr.getAccountType().toString());
        response.setDebit(deb);
        final TransferData.Account cre = new TransferData.Account();
        cre.setAccUuid(creAttr.getAccountUUID());
        cre.setAccNumber(creAttr.getAccountNumber());
        cre.setIban(creAttr.getIban());
        cre.setAccType(creAttr.getAccountType().toString());
        response.setCredit(cre);
        return response;
    }

    private List<Page> collectPages(Long accountId, LocalDate beginOn, LocalDate finishOn) {
        final Page current = this.curPageRepo.getById(accountId).getEntity();
        final List<IdentifiedEntity<Page>> history = this.histPageRepo.getHistoryPeriod(accountId, beginOn, finishOn);
        final List<Page> result = new ArrayList<>();
        history.forEach(p -> result.add(p.getEntity()));
        if (current.getRepStartedOn().compareTo(beginOn) <= 0 || current.getRepStartedOn().compareTo(finishOn) <= 0) {
            result.add(current);
        }
        return result;
    }

    public static class ReportingCollection {
        private BigDecimal startBalance;
        private BigDecimal startRepBalance;
        private BigDecimal finishBalance;
        private BigDecimal finishRepBalance;
        private LocalDate startedOn;
        private LocalDate finishedOn;

        private List<Post> posts = new ArrayList<>();

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

        public BigDecimal getStartBalance() {
            return startBalance;
        }

        public void setStartBalance(BigDecimal startBalance) {
            this.startBalance = startBalance;
        }

        public BigDecimal getFinishBalance() {
            return finishBalance;
        }

        public void setFinishBalance(BigDecimal finishBalance) {
            this.finishBalance = finishBalance;
        }

        public LocalDate getStartedOn() {
            return startedOn;
        }

        public void setStartedOn(LocalDate startedOn) {
            this.startedOn = startedOn;
        }

        public LocalDate getFinishedOn() {
            return finishedOn;
        }

        public void setFinishedOn(LocalDate finishedOn) {
            this.finishedOn = finishedOn;
        }

        public void setPosts(List<Post> posts) {
            this.posts = posts;
        }

        public List<Post> getPosts() {
            return posts;
        }
    }

    /**
     *
     * @param accountId
     * @param beginOn
     * @param finishOn
     * @return
     */
    public ReportingCollection collectReportingData(Long accountId,
                                                    LocalDate beginOn,
                                                    LocalDate finishOn) {
        final List<Page> pages = collectPages(accountId, beginOn, finishOn);
        final List<Post> previousPosts = pages.stream()
                .map(p -> p.getPosts())
                .flatMap(posts -> posts.stream())
                .filter(post -> post.getReportedOn().compareTo(beginOn) < 0)
                .sorted((p1,p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                .collect(Collectors.toList());
        final BigDecimal preTurnover = previousPosts.stream().map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal preRepTurnover = previousPosts.stream().map(p -> p.getLocalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal startBalance = pages.get(0).getStartBalance().add(preTurnover);
        final BigDecimal startRepBalance = pages.get(0).getStartRepBalance().add(preRepTurnover);
        final List<Post> periodPosts = pages.stream()
                .map(p -> p.getPosts())
                .flatMap(posts -> posts.stream())
                .filter(post -> post.getReportedOn().compareTo(beginOn) >= 0 &&
                        post.getReportedOn().compareTo(finishOn) <= 0)
                .sorted((p1,p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                .collect(Collectors.toList());
        final BigDecimal periodTurnover = periodPosts.stream().map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal periodRepTurnover = periodPosts.stream().map(p -> p.getLocalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal finishBalance = startBalance.add(periodTurnover);
        final BigDecimal finishRepBalance = startRepBalance.add(periodRepTurnover);
        final ReportingCollection collection = new ReportingCollection();
        collection.setStartedOn(beginOn);
        collection.setFinishedOn(finishOn);
        collection.setStartBalance(startBalance);
        collection.setStartRepBalance(startRepBalance);
        collection.setFinishBalance(finishBalance);
        collection.setFinishRepBalance(finishRepBalance);
        collection.setPosts(periodPosts);
        return collection;
    }

}